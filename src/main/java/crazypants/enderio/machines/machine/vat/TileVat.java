package crazypants.enderio.machines.machine.vat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machine.interfaces.IPoweredTask;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.vat.VatRecipeManager;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

@Storable
public class TileVat extends AbstractPoweredTaskEntity implements ITankAccess.IExtendedTankAccess, IPaintable.IPaintableTileEntity {

  public static final int BUCKET_VOLUME = 1000;

  @Store
  final SmartTank inputTank = new SmartTank(BUCKET_VOLUME * 8);
  @Store
  final SmartTank outputTank = new SmartTank(BUCKET_VOLUME * 8);

  private static int IO_MB_TICK = 100;

  boolean tanksDirty = false;

  // Used client side in the vat gui to render progress
  Fluid currentTaskInputFluid;
  Fluid currentTaskOutputFluid;

  public TileVat() {
    super(new SlotDefinition(0, 1, -1, -1, -1, -1), MachineObject.block_vat);
    inputTank.setTileEntity(this);
    inputTank.setCanDrain(false);
    outputTank.setTileEntity(this);
    outputTank.setCanFill(false);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.VAT;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    MachineRecipeInput[] inputs = getRecipeInputs();
    inputs[i] = new MachineRecipeInput(i, itemstack);
    return VatRecipeManager.getInstance().isValidInput(inputs);
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    boolean res = super.doPush(dir);
    if (dir != null && outputTank.getFluidAmount() > 0) {
      if (FluidWrapper.transfer(outputTank, world, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    boolean res = super.doPull(dir);
    if (dir != null && inputTank.getFluidAmount() < inputTank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), inputTank, IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    return res;
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
  protected void sendTaskProgressPacket() {
    PacketHandler.sendToAllAround(new PacketVatProgress(this), this);
    ticksSinceLastProgressUpdate = 0;
  }

  @Override
  protected void mergeFluidResult(ResultStack result) {
    outputTank.fillInternal(result.fluid, true);
    setTanksDirty();
  }

  @Override
  protected void drainInputFluid(MachineRecipeInput fluid) {
    inputTank.removeFluidAmount(fluid.fluid.amount);
  }

  @Override
  protected boolean canInsertResultFluid(ResultStack fluid) {
    int res = outputTank.fillInternal(fluid.fluid, false);
    return res >= fluid.fluid.amount;
  }

  @Override
  protected MachineRecipeInput[] getRecipeInputs() {
    MachineRecipeInput[] res = new MachineRecipeInput[slotDefinition.getNumInputSlots() + 1];
    int fromSlot = slotDefinition.minInputSlot;
    for (int i = 0; i < res.length - 1; i++) {
      res[i] = new MachineRecipeInput(fromSlot, inventory[fromSlot]);
      fromSlot++;
    }

    res[res.length - 1] = new MachineRecipeInput(0, inputTank.getFluid());

    return res;
  }

  @Override
  public int getPowerUsePerTick() {
    return Config.vatPowerUserPerTickRF;
  }

  @Override
  public String getSoundName() {
    return "machine.vat";
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
    MachineRecipeInput[] inputs = getRecipeInputs();
    inputs[inputs.length - 1] = new MachineRecipeInput(0, forFluidType);
    if (VatRecipeManager.getInstance().isValidInput(inputs)) {
      return inputTank;
    } else {
      return null;
    }
  }

  @Override
  public FluidTank[] getOutputTanks() {
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
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
