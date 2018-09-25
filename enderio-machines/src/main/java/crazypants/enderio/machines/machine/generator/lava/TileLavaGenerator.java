package crazypants.enderio.machines.machine.generator.lava;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityGeneratorEntity;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.LavaGenConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static crazypants.enderio.base.lang.LangTemperature.C2K;
import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.LAVAGEN_POWER_LOSS;

@Storable
public class TileLavaGenerator extends AbstractCapabilityGeneratorEntity implements IPaintable.IPaintableTileEntity, ITankAccess.IExtendedTankAccess {

  public static final @Nonnull String OUTPUT_COB = "OUTCOB";
  public static final @Nonnull String OUTPUT_STO = "OUTSTO";
  public static final @Nonnull String OUTPUT_OBS = "OUTOBS";

  @Store
  public int burnTime = 0;
  @Store
  public int heat = 0;
  @Store
  protected final @Nonnull SmartTank tank = new SmartTank(FluidRegistry.LAVA, LavaGenConfig.tankSize.get());
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  public int lavaUsed = 0;
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  public int cobblePoints = 0;
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  public int stonePoints = 0;
  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  public int obsidianPoints = 0;

  private PowerDistributor powerDis;
  private int coolingSide = 0;

  public TileLavaGenerator() {
    super(LAVAGEN_POWER_BUFFER, LAVAGEN_POWER_GEN);
    getEnergy().setEnergyLoss(LAVAGEN_POWER_LOSS);
    tank.setTileEntity(this);
    getInventory().add(Type.OUTPUT, OUTPUT_COB, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
    getInventory().add(Type.OUTPUT, OUTPUT_STO, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
    getInventory().add(Type.OUTPUT, OUTPUT_OBS, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_lava_generator.getUnlocalisedName();
  }

  @Override
  public boolean isActive() {
    return burnTime > 0;
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (heat > 0) {
      heat = Math.max(0, heat - LavaGenConfig.heatLossPassive.get());
      cobblePoints += LavaGenConfig.heatLossPassive.get();
      doActiveCooling();
    }

    if (redstoneCheck && !getEnergy().isFull() && getEnergy().hasCapacitor() && !isOutputFull()) {
      if (burnTime > 0) {
        getEnergy().setEnergyStored(getEnergy().getEnergyStored() + getPowerGenPerTick());
        burnTime--;
        heat = Math.min(heat + LavaGenConfig.heatGain.get(), getMaxHeat());
      }
      if (burnTime <= 0 && !tank.isEmpty()) {
        tank.drain(1, true);
        burnTime = getLavaBurntime() / Fluid.BUCKET_VOLUME / CapacitorKey.LAVAGEN_POWER_FLUID_USE.get(getCapacitorData());
        if (LavaGenConfig.outputEnabled.get()) {
          lavaUsed++;
          doGenOutput();
        }
      }
    }

    if (getHeat() > LavaGenConfig.overheatThreshold.get() && shouldDoWorkThisTick(20)) {
      int dx = random.nextInt(8) - random.nextInt(8);
      int dy = random.nextInt(4) - random.nextInt(4);
      int dz = random.nextInt(8) - random.nextInt(8);
      BlockPos pos2 = pos.east(dx).up(dy).north(dz);
      if (world.isAirBlock(pos2)) {
        world.setBlockState(pos2, Blocks.FIRE.getDefaultState());
      }
    }

    transmitEnergy();

    return false;
  }

  private void doGenOutput() {
    while (lavaUsed >= LavaGenConfig.outputAmount.get()) {
      final ItemStack stack;
      final @Nonnull String slotName;

      if (cobblePoints > stonePoints && cobblePoints > obsidianPoints) {
        cobblePoints = 0;
        if (!LavaGenConfig.cobbleEnabled.get()) {
          continue;
        }
        stack = new ItemStack(Blocks.COBBLESTONE);
        slotName = OUTPUT_COB;
      } else if (obsidianPoints > stonePoints) {
        obsidianPoints = 0;
        if (!LavaGenConfig.obsidianEnabled.get()) {
          continue;
        }
        stack = new ItemStack(Blocks.OBSIDIAN);
        slotName = OUTPUT_OBS;
      } else {
        stonePoints = 0;
        if (!LavaGenConfig.stoneEnabled.get()) {
          continue;
        }
        stack = new ItemStack(Blocks.STONE);
        slotName = OUTPUT_STO;
      }

      if (mergeOutput(slotName, stack)) {
        lavaUsed -= LavaGenConfig.outputAmount.get();
      } else {
        break;
      }
    }
  }

  private void doActiveCooling() {
    if (heat > 0 && LavaGenConfig.heatLossActive.get() > 0) {
      coolingSide++;
      if (coolingSide > 5) {
        coolingSide = 0;
      }
      final EnumFacing side = NNList.FACING.get(coolingSide);
      BlockPos pos2 = pos.offset(side);
      if (world.isBlockLoaded(pos2)) {
        Block block = world.getBlockState(pos2).getBlock();
        if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
          IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, pos2, side.getOpposite());
          if (targetFluidHandler != null) {
            FluidStack fluidStack = targetFluidHandler.drain(1000, false);
            final float heatInKelvin = getHeatDisplayValue();
            if (fluidStack != null && fluidStack.amount >= 1000 && fluidStack.getFluid().getTemperature(fluidStack) < heatInKelvin) {
              heat = Math.max(0, heat - LavaGenConfig.heatLossActive.get());
              if (fluidStack.getFluid() == FluidRegistry.WATER) {
                if (heatInKelvin > C2K(100) && random.nextFloat() < LavaGenConfig.activeCoolingEvaporatesWater.get()) {
                  world.setBlockToAir(pos2);
                  world.playSound(null, pos2.getX() + .5f, pos2.getY() + .5f, pos2.getZ() + .5f, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
                      2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                  for (int k = 0; k < 8; ++k) {
                    PacketSpawnParticles.create(world, pos2.getX() + random.nextDouble(), pos2.getY() + random.nextDouble(), pos2.getZ() + random.nextDouble(),
                        1, EnumParticleTypes.SMOKE_LARGE);
                  }
                  stonePoints += LavaGenConfig.heatLossActive.get(); // water, consumed
                } else {
                  cobblePoints += LavaGenConfig.heatLossActive.get(); // water, not consumed
                }
              } else if (fluidStack.getFluid().getTemperature(fluidStack) < C2K(0)) {
                obsidianPoints += LavaGenConfig.heatLossActive.get(); // other fluid, very cold
              } else {
                cobblePoints += LavaGenConfig.heatLossActive.get(); // other fluid, luke warm
              }
            }
          }
        } else if (block == Blocks.ICE || block == Blocks.FROSTED_ICE || block == Blocks.PACKED_ICE) {
          heat = Math.max(0, heat - LavaGenConfig.heatLossActive.get());
          obsidianPoints += LavaGenConfig.heatLossActive.get(); // ice is always cold
          if (random.nextFloat() < LavaGenConfig.activeCoolingLiquefiesIce.get()) {
            if (world.provider.doesWaterVaporize()) {
              world.setBlockToAir(pos2);
              world.playSound(null, pos2.getX() + .5f, pos2.getY() + .5f, pos2.getZ() + .5f, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
                  2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
              for (int k = 0; k < 8; ++k) {
                PacketSpawnParticles.create(world, pos2.getX() + random.nextDouble(), pos2.getY() + random.nextDouble(), pos2.getZ() + random.nextDouble(), 1,
                    EnumParticleTypes.SMOKE_LARGE);
              }
            } else {
              world.setBlockState(pos2, Blocks.WATER.getDefaultState());
              for (int k = 0; k < 4; ++k) {
                PacketSpawnParticles.create(world, pos2.getX() + random.nextDouble(), pos2.getY() + 1, pos2.getZ() + random.nextDouble(), 1,
                    EnumParticleTypes.SMOKE_LARGE);
              }
              Blocks.WATER.neighborChanged(Blocks.WATER.getDefaultState(), world, pos2, block, pos2);
            }
          }
        }
      }
    }
  }

  private int getLavaBurntime() {
    return LavaGenConfig.useVanillaBurnTime.get() ? 20000 : TileEntityFurnace.getItemBurnTime(new ItemStack(Items.LAVA_BUCKET));
  }

  protected int getPowerGenPerTick() {
    return getEnergy().hasCapacitor() ? (int) Math.max(1, getEnergy().getMaxUsage() * getHeatFactor()) : 0;
  }

  protected float getHeatFactor() {
    return Math.max(LavaGenConfig.minEfficiency.get(), 1f - getHeat());
  }

  protected float getHeat() {
    return heat / (float) getMaxHeat();
  }

  protected float getHeatDisplayValue() {
    float factor = getHeat();
    int ambient = FluidRegistry.WATER.getTemperature();
    int reallyhot = FluidRegistry.LAVA.getTemperature();
    return ambient + (reallyhot - ambient) * factor;
  }

  protected int getMaxHeat() {
    return getLavaBurntime() * LavaGenConfig.maxHeatFactor.get();
  }

  private boolean mergeOutput(@Nonnull String slotName, @Nonnull ItemStack stack) {
    final InventorySlot slot = getInventory().getSlot(slotName);
    final ItemStack oldStack = slot.get();
    if (Prep.isInvalid(oldStack)) {
      slot.set(stack);
      return true;
    } else if (ItemUtil.areStackMergable(oldStack, stack)) {
      oldStack
          .grow(stack.splitStack(Math.min(Math.min(oldStack.getMaxStackSize(), slot.getMaxStackSize()) - oldStack.getCount(), stack.getCount())).getCount());
      slot.set(oldStack);
      return stack.isEmpty();
    }
    return false;
  }

  /**
   * Checks if all output slots can accept more items.
   * 
   * @return <code>true</code> if at least one slot if full, <code>false</code> otherwise
   */
  private boolean isOutputFull() {
    for (InventorySlot slot : getInventory().getView(Type.OUTPUT)) {
      final ItemStack stack = slot.get();
      if (Prep.isInvalid(stack)) {
        return false;
      }
      if (stack.getCount() >= stack.getMaxStackSize() || stack.getCount() >= slot.getMaxStackSize()) {
        return true;
      }
    }
    return false;
  }

  private boolean transmitEnergy() {
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int canTransmit = Math.min(getEnergy().getEnergyStored(), getEnergy().getMaxUsage() * 2);
    if (canTransmit <= 0) {
      return false;
    }
    int transmitted = powerDis.transmitEnergy(world, canTransmit);
    getEnergy().setEnergyStored(getEnergy().getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

  @Override
  @Nullable
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (tank.canFill(forFluidType)) {
      return tank;
    }
    return null;
  }

  @Override
  @Nonnull
  public FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    markDirty();
  }

  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
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

}
