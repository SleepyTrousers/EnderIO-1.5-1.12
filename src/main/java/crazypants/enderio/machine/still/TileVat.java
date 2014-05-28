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
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;

public class TileVat extends AbstractPoweredTaskEntity implements IFluidHandler {

  private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);

  final FluidTank inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);
  final FluidTank outputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);

  private static int IO_MB_TICK = 100;

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
    MachineRecipeInput[] inputs = getInputs();
    inputs[i] = new MachineRecipeInput(i, itemstack);
    return VatRecipeManager.getInstance().isValidInput(inputs);
  }

  @Override
  protected boolean doPush(ForgeDirection dir) {

    if(isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPush(dir);
    if(outputTank.getFluidAmount() > 0) {

      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {
        if(target.canFill(dir.getOpposite(), outputTank.getFluid().getFluid())) {
          FluidStack push = outputTank.getFluid().copy();
          push.amount = Math.min(push.amount, IO_MB_TICK);
          int filled = target.fill(dir.getOpposite(), push, true);
          if(filled > 0) {
            outputTank.drain(filled, true);
            tanksDirty = true;
            return res;
          }
        }
      }

    }
    return res;
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {

    if(isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPull(dir);
    if(inputTank.getFluidAmount() < inputTank.getCapacity()) {
      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {

        if(inputTank.getFluidAmount() > 0) {
          FluidStack canPull = inputTank.getFluid().copy();
          canPull.amount = inputTank.getCapacity() - inputTank.getFluidAmount();
          canPull.amount = Math.min(canPull.amount, IO_MB_TICK);
          FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
          if(drained != null && drained.amount > 0) {
            inputTank.fill(drained, true);
            tanksDirty = true;
            return res;
          }
        } else {
          //empty input tank
          FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
          if(infos != null) {
            for (FluidTankInfo info : infos) {
              if(info.fluid != null && info.fluid.amount > 0) {
                if(canFill(dir, info.fluid.getFluid())) {
                  FluidStack canPull = info.fluid.copy();
                  canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
                  FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
                  if(drained != null && drained.amount > 0) {
                    inputTank.fill(drained, true);
                    tanksDirty = true;
                    return res;
                  }
                }
              }
            }
          }
        }

      }
    }
    return res;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(isSideDisabled(from.ordinal())) {
      return 0;
    }

    if(resource == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    tanksDirty = true;
    return inputTank.fill(resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(isSideDisabled(from.ordinal())) {
      return null;
    }
    if(outputTank.getFluid() == null || resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
      return null;
    }
    tanksDirty = true;
    return outputTank.drain(resource.amount, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if(isSideDisabled(from.ordinal())) {
      return null;
    }
    tanksDirty = true;
    return outputTank.drain(maxDrain, doDrain);
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = super.processTasks(redstoneChecksPassed);
    if(tanksDirty && worldObj.getWorldTime() % 10 == 0) {
      EnderIO.packetPipeline.sendToAllAround(new PacketTanks(this), this);
      tanksDirty = false;
    }    
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
    if(isSideDisabled(from.ordinal())) {
      return false;
    }
    if(fluid == null || (inputTank.getFluid() != null && inputTank.getFluid().getFluid().getID() != fluid.getID())) {
      return false;
    }

    MachineRecipeInput[] inputs = getInputs();
    if(inputTank.getFluidAmount() <= 0) {
      inputs[inputs.length - 1] = new MachineRecipeInput(0, new FluidStack(fluid, 1));
    }

    return VatRecipeManager.getInstance().isValidInput(inputs);
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(isSideDisabled(from.ordinal())) {
      return false;
    }
    return outputTank.getFluid() != null && outputTank.getFluid().getFluid().getID() == fluid.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if(isSideDisabled(from.ordinal())) {
      return new FluidTankInfo[0];
    }
    return new FluidTankInfo[] { inputTank.getInfo(), outputTank.getInfo() };
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);

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
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
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

  public IPoweredTask getCurrentTask() {
    return currentTask;
  }

  @Override
  public float getPowerUsePerTick() {
    return Config.vatPowerUserPerTick;
  }

}
