package crazypants.enderio.machines.machine.tank;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.config.TankConfig;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileTank extends AbstractInventoryMachineEntity implements ITankAccess.IExtendedTankAccess, IPaintable.IPaintableTileEntity {

  private static int IO_MB_TICK = 100;

  @Store
  protected final @Nonnull SmartTank tank;
  protected int lastUpdateLevel = -1;

  private boolean tankDirty = false;
  private int lastFluidLuminosity = 0;
  private final @Nonnull EnumTankType tankType;

  @Store
  private @Nonnull VoidMode voidMode = VoidMode.NEVER;

  public TileTank(@Nonnull EnumTankType tankType) {
    super(new SlotDefinition(0, 2, 3, 4, -1, -1));
    tank = tankType.getTank();
    tank.setTileEntity(this);
    this.tankType = tankType;
    addICap(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingIn -> getSmartTankFluidHandler().get(facingIn));
  }

  public TileTank() {
    this(EnumTankType.NORMAL);
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (super.doPush(dir)) {
      return true;
    }
    if (dir != null && !tank.isEmpty()) {
      if (FluidWrapper.transfer(tank, world, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (super.doPull(dir)) {
      return true;
    }
    if (dir != null && !tank.isFull()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), tank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  private int getFilledLevel() {
    int level = (int) Math.floor(16 * tank.getFilledRatio());
    if (level == 0 && tank.getFluidAmount() > 0) {
      level = 1;
    }
    return level;
  }

  public boolean canVoidItems() {
    final FluidStack fluid = tank.getFluid();
    return fluid != null && fluid.getFluid().getTemperature() > 973 && TankConfig.allowVoiding.get();
  }

  public @Nonnull VoidMode getVoidMode() {
    return TankConfig.allowVoiding.get() ? voidMode : VoidMode.NEVER;
  }

  public void setVoidMode(@Nonnull VoidMode mode) {
    this.voidMode = mode;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack item) {
    if (canVoidItems() && voidMode == VoidMode.ALWAYS && i < 2) {
      return false;
    }
    if (i == 0) {
      FluidStack fluidType = FluidUtil.getFluidTypeFromItem(item);
      return (fluidType != null && fluidType.amount > 0) || isValidInputItem(item, false);
    } else if (i == 1) {
      return FluidUtil.hasEmptyCapacity(item) || canBeMended(item) || isValidInputItem(item, true);
    } else if (i == 2 && canVoidItems()) {
      return voidMode == VoidMode.ALWAYS || (voidMode == VoidMode.NEVER ? false : !FluidUtil.isFluidContainer(item));
    }
    return false;
  }

  private boolean isValidInputItem(@Nonnull ItemStack item, boolean isFilling) {
    NNList<MachineRecipeInput> list = new NNList<>();
    list.add(new MachineRecipeInput(0, item));
    list.add(new MachineRecipeInput(1, NullHelper.first(tank.getFluid(), TankMachineRecipe.NOTHING)));

    return getRecipe(isFilling, list) != null;
  }

  private IMachineRecipe getRecipe(boolean isFilling, @Nonnull NNList<MachineRecipeInput> inputs) {
    return MachineRecipeRegistry.instance.getRecipeForInputs(tankType.isExplosionResistant() ? RecipeLevel.ADVANCED : RecipeLevel.NORMAL,
        isFilling ? MachineRecipeRegistry.TANK_FILLING : MachineRecipeRegistry.TANK_EMPTYING, inputs);
  }

  private @Nonnull NNList<MachineRecipeInput> getRecipeInputs(boolean isFilling) {
    NNList<MachineRecipeInput> list = new NNList<>();
    list.add(new MachineRecipeInput(0, getStackInSlot(isFilling ? 1 : 0)));
    list.add(new MachineRecipeInput(1, NullHelper.first(tank.getFluid(), TankMachineRecipe.NOTHING)));
    return list;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  private static long lastSendTickAll = -1;
  private long nextSendTickThis = -1;
  private int sendPrio = 0;

  /*
   * Limit sending of client updates because a group of tanks pushing into each other can severely kill the clients fps.
   */
  private boolean canSendClientUpdate() {
    long tick = EnderIO.proxy.getServerTickCount();
    if (nextSendTickThis > tick) {
      return false;
    }
    if (tick == lastSendTickAll && sendPrio++ < 200) {
      return false;
    }
    nextSendTickThis = (lastSendTickAll = tick) + 10 + sendPrio * 2;
    sendPrio = 0;
    return true;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    processItems(redstoneCheck);
    int filledLevel = getFilledLevel();
    if (lastUpdateLevel != filledLevel) {
      lastUpdateLevel = filledLevel;
      setTanksDirty();
    }
    if (tankDirty && canSendClientUpdate()) {
      PacketHandler.sendToAllAround(new PacketTankFluid(this), this);
      world.updateComparatorOutputLevel(pos, getBlockType());
      updateLight();
      tankDirty = false;
    }
    return false;
  }

  public void updateLight() {
    final FluidStack fluid = tank.getFluid();
    int thisFluidLuminosity = fluid == null || fluid.getFluid() == null || tank.isEmpty() ? 0 : fluid.getFluid().getLuminosity(fluid);
    if (thisFluidLuminosity != lastFluidLuminosity) {
      if (world.checkLightFor(EnumSkyBlock.BLOCK, getPos())) {
        updateBlock();
      }
      lastFluidLuminosity = thisFluidLuminosity;
    }
  }

  public int getComparatorOutput() {
    if (tank.isEmpty()) {
      return 0;
    }

    return (int) (1 + ((double) tank.getFluidAmount() / (double) tank.getCapacity()) * 14);
  }

  private boolean processItems(boolean redstoneCheck) {
    if (!redstoneCheck) {
      return false;
    }
    if (!shouldDoWorkThisTick(getBlockMetadata() > 0 ? 10 : 20)) {
      return false;
    }
    voidItems();
    return drainFullContainer() || fillEmptyContainer() || mendItem();
  }

  private void voidItems() {
    final ItemStack stack = getStackInSlot(2);
    if (Prep.isValid(stack) && canVoidItems()) {
      if (!tank.isFull() && tank.hasFluid(FluidRegistry.LAVA) && TankConfig.smeltTrashIntoLava.get().smelt(stack)) {
        tank.addFluidAmount((int) MathHelper.clamp(world.rand.nextGaussian() * .75 + 3.5, 1, 10)); // 49% for 3, 22%: for 2 and 4, 2.2% for 1 and 5
        stack.shrink(1);
      } else {
        stack.shrink(10);
      }
      SoundHelper.playSound(world, pos, SoundHelper.BLOCK_CENTER, SoundRegistry.ITEM_BURN, 0.05F, 2.0F + world.rand.nextFloat() * 0.4F);
      markDirty();
    }
  }

  private boolean canBeMended(@Nonnull ItemStack stack) {
    return TankConfig.allowMending.get() && Prep.isValid(stack) && stack.isItemDamaged()
        && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0 && tank.hasFluid(Fluids.XP_JUICE.getFluid());
  }

  private boolean mendItem() {
    final int output = getSlotDefinition().getMaxOutputSlot();
    final int input = getSlotDefinition().getMinInputSlot() + 1;
    if (tank.isEmpty() || !canBeMended(getStackInSlot(input)) || Prep.isValid(getStackInSlot(output))) {
      return false;
    }

    int damageMendable = Math.min(xpToDurability(XpUtil.liquidToExperience(tank.getFluidAmount())), getStackInSlot(input).getItemDamage());
    if (damageMendable < 1) {
      return false;
    }
    getStackInSlot(input).setItemDamage(inventory[input].getItemDamage() - damageMendable);
    tank.drainInternal(XpUtil.experienceToLiquid(durabilityToXp(damageMendable)), true);

    if (!getStackInSlot(input).isItemDamaged()) {
      setInventorySlotContents(output, getStackInSlot(input));
      setInventorySlotContents(input, Prep.getEmpty());
    }

    markDirty();

    return true;
  }

  public static int durabilityToXp(int durability) {
    return durability / 2;
  }

  public static int xpToDurability(int xp) {
    return xp * 2;
  }

  private boolean fillEmptyContainer() {
    final int input = getSlotDefinition().getMinInputSlot() + 1;
    final ItemStack inputStack = getStackInSlot(input);
    if (Prep.isInvalid(inputStack) || tank.isEmpty()) {
      return false;
    }

    IMachineRecipe recipe = null;
    final FluidTank outputTank = getOutputTanks()[0];
    FluidAndStackResult fill = FluidUtil.tryFillContainer(inputStack, outputTank.getFluid());
    if (fill.result.fluidStack == null) {
      NNList<MachineRecipeInput> recipeInputs = getRecipeInputs(true);
      recipe = getRecipe(true, recipeInputs);
      if (recipe != null) {
        FluidStack fluidStack = null;
        ItemStack remainderStack, resultStack = null;
        for (ResultStack result : recipe.getCompletedResult(0L, 0f, recipeInputs)) {
          if (result.fluid != null) {
            fluidStack = result.fluid;
          } else {
            resultStack = result.item;
          }
        }
        if (fluidStack == null || resultStack == null) {
          return false;
        }
        remainderStack = inputStack.copy();
        remainderStack.shrink(1);
        fill = new FluidAndStackResult(resultStack, fluidStack, remainderStack, fluidStack);
      } else {
        return false;
      }
    }

    final int output = getSlotDefinition().getMaxOutputSlot();
    final ItemStack outputStack = getStackInSlot(output);

    if (Prep.isValid(outputStack) && Prep.isValid(fill.result.itemStack)) {
      if (outputStack.isStackable() && ItemUtil.areStackMergable(outputStack, fill.result.itemStack)
          && (fill.result.itemStack.getCount() + outputStack.getCount()) <= outputStack.getMaxStackSize()) {
        fill.result.itemStack.grow(outputStack.getCount());
      } else {
        return false;
      }
    }

    outputTank.setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(input, fill.remainder.itemStack);
    setInventorySlotContents(output, fill.result.itemStack);

    if (recipe instanceof TankMachineRecipe) {
      ((TankMachineRecipe) recipe).getLogic().executeSFX(true, world, pos);
    }

    setTanksDirty();
    markDirty();
    return false;
  }

  private boolean drainFullContainer() {
    final int input = getSlotDefinition().getMinInputSlot();
    final ItemStack inputStack = getStackInSlot(input);
    if (Prep.isInvalid(inputStack) || tank.isFull()) {
      return false;
    }

    IMachineRecipe recipe = null;
    FluidAndStackResult fill = FluidUtil.tryDrainContainer(inputStack, this);
    if (fill.result.fluidStack == null) {
      NNList<MachineRecipeInput> recipeInputs = getRecipeInputs(false);
      recipe = getRecipe(false, recipeInputs);
      if (recipe != null) {
        FluidStack fluidStack = null;
        ItemStack remainderStack, resultStack = null;
        for (ResultStack result : recipe.getCompletedResult(0L, 0f, recipeInputs)) {
          if (result.fluid != null) {
            fluidStack = result.fluid;
          } else {
            resultStack = result.item;
          }
        }
        if (fluidStack == null || resultStack == null) {
          return false;
        }
        remainderStack = inputStack.copy();
        remainderStack.shrink(1);
        fill = new FluidAndStackResult(resultStack, fluidStack, remainderStack, fluidStack);
      } else {
        return false;
      }
    }

    final int output = getSlotDefinition().getMinOutputSlot();
    final ItemStack outputStack = getStackInSlot(output);

    if (Prep.isValid(outputStack) && Prep.isValid(fill.result.itemStack)) {
      if (outputStack.isStackable() && ItemUtil.areStackMergable(outputStack, fill.result.itemStack)
          && (fill.result.itemStack.getCount() + outputStack.getCount()) <= outputStack.getMaxStackSize()) {
        fill.result.itemStack.grow(outputStack.getCount());
      } else {
        return false;
      }
    }

    getInputTank(fill.result.fluidStack).setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(input, fill.remainder.itemStack);
    if (Prep.isValid(fill.result.itemStack)) {
      setInventorySlotContents(output, fill.result.itemStack);
    }

    if (recipe instanceof TankMachineRecipe) {
      ((TankMachineRecipe) recipe).getLogic().executeSFX(false, world, pos);
    }

    setTanksDirty();
    markDirty();
    return false;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return tank;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { tank };
  }

  @Override
  public void setTanksDirty() {
    if (!tankDirty) {
      tankDirty = true;
      markDirty();
    }
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1 && tank.getFluidAmount() > 0;
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.STORAGE;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return tank.getFluid();
      }

      @Override
      public int getCapacity() {
        return tank.getCapacity();
      }
    });
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

}
