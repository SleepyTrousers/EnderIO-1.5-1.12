package crazypants.enderio.machine.still;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.PoweredTask;
import crazypants.enderio.machine.SlotDefinition;

public class TileVat extends AbstractPoweredTaskEntity implements IFluidHandler {

  private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);

  final FluidTank inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);
  final FluidTank outputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);

  boolean tanksDirty = false;

  public TileVat() {
    super(new SlotDefinition(0, 1, -1, -1, -1, -1));
  }

  @Override
  public String getInventoryName() {
    return ModObject.blockVat.unlocalisedName;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockVat.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return VatRecipeManager.getInstance().isValidInput(new MachineRecipeInput(i, itemstack));
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || !resource.isFluidEqual(WATER)) {
      return 0;
    }
    tanksDirty = true;
    return inputTank.fill(resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(outputTank.getFluid() == null || resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
      return null;
    }
    tanksDirty = true;
    return outputTank.drain(resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    tanksDirty = true;
    return outputTank.drain(maxDrain, doDrain);
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed) || tanksDirty;
    tanksDirty = false;
    return res;
  }

  @Override
  protected void mergeFluidResult(ResultStack result) {
    outputTank.fill(result.fluid, true);
    tanksDirty = true;
  }

  @Override
  protected void drainInputFluid(MachineRecipeInput fluid) {
    inputTank.drain(fluid.fluid.amount, true);
    tanksDirty = true;
  }

  @Override
  protected boolean canInsertResultFluid(ResultStack fluid) {
    int res = outputTank.fill(fluid.fluid, false);
    return res >= fluid.fluid.amount;
  }

  @Override
  protected MachineRecipeInput[] getInputs() {
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
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(fluid == null || fluid.getID() != WATER.getFluid().getID()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return outputTank.getFluid() != null && outputTank.getFluid().getFluid().getID() == fluid.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { inputTank.getInfo(), outputTank.getInfo() };
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);

    if(nbtRoot.hasKey("inputTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("inputTank");
      if(tankRoot != null) {
        inputTank.readFromNBT(tankRoot);
      } else {
        inputTank.setFluid(null);
      }
    } else {
      inputTank.setFluid(null);
    }

    if(nbtRoot.hasKey("outputTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("outputTank");
      if(tankRoot != null) {
        outputTank.readFromNBT(tankRoot);
      } else {
        outputTank.setFluid(null);
      }
    } else {
      outputTank.setFluid(null);
    }

  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    if(inputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      inputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("inputTank", tankRoot);
    }
    if(outputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      outputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("outputTank", tankRoot);
    }

  }

  public PoweredTask getCurrentTask() {
    return currentTask;
  }

  @Override
  protected float getPowerUsePerTick() {
    return 1;
  }

}
