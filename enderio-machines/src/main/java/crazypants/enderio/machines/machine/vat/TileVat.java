package crazypants.enderio.machines.machine.vat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crazypants.enderio.machines.config.config.VatConfig;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_VAT_DOUBLE_OP_CHANCE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_VAT_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_VAT_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_VAT_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.VAT_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.VAT_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.VAT_POWER_USE;

@Storable
public class TileVat extends AbstractPoweredTaskEntity implements ITankAccess.IExtendedTankAccess, IPaintable.IPaintableTileEntity {

  @Store
  final @Nonnull SmartTank inputTank = new SmartTank(VatConfig.vatInputTankSize.get());
  @Store
  final @Nonnull SmartTank outputTank = new SmartTank(VatConfig.vatOutputTankSize.get());

  private static int IO_MB_TICK = 100;

  boolean tanksDirty = false;

  // Used client side in the vat gui to render progress
  Fluid currentTaskInputFluid;
  Fluid currentTaskOutputFluid;

  public static class Enhanced extends TileVat {

    public Enhanced() {
      super(new SlotDefinition(2, 0, 1), ENHANCED_VAT_POWER_INTAKE, ENHANCED_VAT_POWER_BUFFER, ENHANCED_VAT_POWER_USE);
    }

    @Nonnull
    @Override
    public ICapacitorData getCapacitorData() {
      return CapacitorHelper.increaseCapacitorLevel(super.getCapacitorData(), 1f);
    }

    @Override
    public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
      return (faceHit != EnumFacing.UP || mode == IoMode.NONE) && super.supportsMode(faceHit, mode);
    }

    @Override
    protected boolean shouldDoubleTick(@Nonnull IPoweredTask task, int usedEnergy) {
      double chance = getCapacitorData().getUnscaledValue(ENHANCED_VAT_DOUBLE_OP_CHANCE) * (usedEnergy / task.getRequiredEnergy());
      if (random.nextDouble() < chance) {
        return true;
      }
      return super.shouldDoubleTick(task, usedEnergy);
    }

  }

  public TileVat(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    inputTank.setTileEntity(this);
    inputTank.setCanDrain(false);
    outputTank.setTileEntity(this);
    outputTank.setCanFill(false);
  }

  public TileVat() {
    this(new SlotDefinition(2, 0, 1), VAT_POWER_INTAKE, VAT_POWER_BUFFER, VAT_POWER_USE);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.VAT;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    MachineRecipeInput recipeInput = new MachineRecipeInput(i, itemstack);
    return isMachineRecipeInputValid(recipeInput);
  }

  protected boolean isMachineRecipeInputValid(@Nonnull MachineRecipeInput recipeInput) {
    NNList<MachineRecipeInput> inputs = getRecipeInputs();
    for (int j = 0; j < inputs.size(); j++) {
      if (inputs.get(j).slotNumber == recipeInput.slotNumber) {
        inputs.set(j, recipeInput);
        return VatRecipeManager.getInstance().isValidInput(inputs);
      }
    }
    inputs.add(recipeInput);
    return VatRecipeManager.getInstance().isValidInput(inputs);
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (super.doPush(dir)) {
      return true;
    }
    if (dir != null && outputTank.getFluidAmount() > 0) {
      if (FluidWrapper.transfer(outputTank, world, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
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
    if (dir != null && inputTank.getFluidAmount() < inputTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), inputTank, IO_MB_TICK) > 0) {
        setTanksDirty();
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed);
    if (tanksDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketTanks(this), this);
      tanksDirty = false;
    }
    return res;
  }

  @Override
  public @Nonnull IMessage getProgressPacket() {
    return new PacketVatProgress(this);
  }

  @Override
  protected void mergeFluidResult(@Nonnull ResultStack result) {
    outputTank.fillInternal(result.fluid, true);
    setTanksDirty();
  }

  @Override
  protected void drainInputFluid(@Nonnull MachineRecipeInput fluid) {
    inputTank.removeFluidAmount(fluid.fluid.amount);
  }

  @Override
  protected boolean canInsertResultFluid(@Nonnull ResultStack fluid) {
    final FluidStack fluid2 = fluid.fluid;
    if (fluid2 != null) {
      return outputTank.fillInternal(fluid2, false) >= fluid2.amount;
    } else {
      return false;
    }
  }

  @Override
  protected @Nonnull NNList<MachineRecipeInput> getRecipeInputs() {
    NNList<MachineRecipeInput> res = new NNList<>();
    for (int slot = slotDefinition.minInputSlot; slot <= slotDefinition.maxInputSlot; slot++) {
      final ItemStack item = getStackInSlot(slot);
      if (Prep.isValid(item)) {
        res.add(new MachineRecipeInput(slot, item));
      }
    }
    if (!inputTank.isEmpty()) {
      res.add(new MachineRecipeInput(0, inputTank.getFluid()));
    }
    return res;
  }

  @Override
  public ResourceLocation getSound() {
    return new ResourceLocation(EnderIO.DOMAIN, "machine.vat");
  }

  @Override
  public float getPitch() {
    return 0.3f;
  }

  @Override
  public float getVolume() {
    return super.getVolume() * 0.3f;
  }

  void setClientTask(IPoweredTask currentTask) {
    this.currentTask = currentTask;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    MachineRecipeInput recipeInput = new MachineRecipeInput(0, forFluidType);
    return isMachineRecipeInputValid(recipeInput) ? inputTank : null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] { outputTank };
  }

  @Override
  public void setTanksDirty() {
    if (!tanksDirty) {
      tanksDirty = true;
      markDirty();
    }
  }

  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    List<ITankData> result = new ArrayList<ITankData>();
    result.add(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return inputTank.getFluid();
      }

      @Override
      public int getCapacity() {
        return inputTank.getCapacity();
      }
    });
    result.add(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.OUTPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return outputTank.getFluid();
      }

      @Override
      public int getCapacity() {
        return outputTank.getCapacity();
      }
    });
    return result;
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, inputTank, outputTank);
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

}
