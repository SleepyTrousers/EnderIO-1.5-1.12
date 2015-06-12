package crazypants.enderio.machine.tank;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.SmartTank;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import crazypants.util.ITankAccess;
import crazypants.util.ItemUtil;
import crazypants.util.FluidUtil.FluidAndStackResult;

public class TileTank extends AbstractMachineEntity implements IFluidHandler, ITankAccess {

  private static int IO_MB_TICK = 100;

  protected SmartTank tank;// = new FluidTankEio(16000);
  protected int lastUpdateLevel = -1;
  
  private boolean tankDirty = false;
  private Fluid lastFluid = null;
  private int metaTankType;

  public TileTank(int meta) {
    super(new SlotDefinition(0, 1, 2, 3, -1, -1));
    tank = makeTank(meta);
    metaTankType = meta;
  }

  public TileTank() {
    this(0);
  }

  private static SmartTank makeTank(int tankType) {
    if (tankType == 1) {
      return new SmartTank(32000);
    } else {
      return new SmartTank(16000);
    }
  }

  @Override
  protected boolean doPush(ForgeDirection dir) {

    if(isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPush(dir);
    if(tank.getFluidAmount() > 0) {

      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {
        if(target.canFill(dir.getOpposite(), tank.getFluid().getFluid())) {
          FluidStack push = tank.getFluid().copy();
          push.amount = Math.min(push.amount, IO_MB_TICK);
          int filled = target.fill(dir.getOpposite(), push, true);
          if(filled > 0) {
            tank.drain(filled, true);
            tankDirty = true;
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
    if(tank.getFluidAmount() < tank.getCapacity()) {
      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {

        if(tank.getFluidAmount() > 0) {
          FluidStack canPull = tank.getFluid().copy();
          canPull.amount = tank.getCapacity() - tank.getFluidAmount();
          canPull.amount = Math.min(canPull.amount, IO_MB_TICK);
          FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
          if(drained != null && drained.amount > 0) {
            tank.fill(drained, true);
            tankDirty = true;
            return res;
          }
        } else {

          FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
          if(infos != null) {
            for (FluidTankInfo info : infos) {
              if(info.fluid != null && info.fluid.amount > 0) {
                if(canFill(dir, info.fluid.getFluid())) {
                  FluidStack canPull = info.fluid.copy();
                  canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
                  FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
                  if(drained != null && drained.amount > 0) {
                    tank.fill(drained, true);
                    tankDirty = true;
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
    if(!canFill(from)) {
      return 0;
    }
    return fillInternal(resource, doFill);
  }

  int fillInternal(FluidStack resource, boolean doFill) {
    int res = tank.fill(resource, doFill);
    if(res > 0 && doFill) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(!canDrain(from)) {
      return null;
    }
    return drainInternal(resource, doDrain);
  }

  FluidStack drainInternal(FluidStack resource, boolean doDrain) {
    FluidStack res = tank.drain(resource, doDrain);
    if(res != null && res.amount > 0 && doDrain) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if(!canDrain(from)) {
      return null;
    }
    return drainInternal(maxDrain, doDrain);
  }

  FluidStack drainInternal(int maxDrain, boolean doDrain) {
    FluidStack res = tank.drain(maxDrain, doDrain);
    if(res != null && res.amount > 0 && doDrain) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return canFill(from) && fluid != null && (tank.getFluidAmount() > 0 && tank.getFluid().fluidID == fluid.getID() || tank.getFluidAmount() == 0);
  }

  private boolean canFill(ForgeDirection from) {
    IoMode mode = getIoMode(from);
    return mode != IoMode.PUSH && mode != IoMode.DISABLED;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return canDrain(from) && tank.canDrainFluidType(fluid);
  }

  private boolean canDrain(ForgeDirection from) {
    IoMode mode = getIoMode(from);
    return mode != IoMode.PULL && mode != IoMode.DISABLED;
  }
  
  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { new FluidTankInfo(tank) };
  }

  private int getFilledLevel() {
    int level = (int) Math.floor(16 * tank.getFilledRatio());
    if(level == 0 && tank.getFluidAmount() > 0) {
      level = 1;
    }
    return level;
  }

  @Override
  public String getMachineName() {
    return "tank";
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack item) {
    if(i == 0) {
      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
      if(fluid != null) {
        return true;
      }
      if(item.getItem() == Items.water_bucket) {
        return true;
      }
      if(item.getItem() == Items.lava_bucket) {
        return true;
      }
      if (item.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) item.getItem()).getFluid(item) != null) {
        return true;
      }
      return false;
    } else if(i == 1) {
      if (item.getItem() instanceof IFluidContainerItem
          && (((IFluidContainerItem) item.getItem()).getFluid(item) == null || ((IFluidContainerItem) item.getItem())
              .getFluid(item).amount < ((IFluidContainerItem) item.getItem()).getCapacity(item))) {
        return true;
      }
      return FluidContainerRegistry.isEmptyContainer(item) || item.getItem() == Items.bucket;
    }
    return false;
  }

  @Override
  public boolean isActive() {
    return false;
  }
  
  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean res = processItems(redstoneCheckPassed);
    int filledLevel = getFilledLevel();
    if(lastUpdateLevel != filledLevel) {
      lastUpdateLevel = filledLevel;
      tankDirty = false;
      return true;
    }
    if(tankDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketTank(this), this);
      worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
      Fluid held = tank.getFluid() == null ? null : tank.getFluid().getFluid();
      if(lastFluid != held) {
        lastFluid = held;
        worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
      }
      tankDirty = false;
    }
    return res;
  }
  
  public int getComparatorOutput() {
    FluidTankInfo info = getTankInfo(null)[0];
    return info == null || info.fluid == null ? 0 : (int) (((double) info.fluid.amount / (double) info.capacity) * 15);
  }

  private boolean processItems(boolean redstoneCheckPassed) {
    if(!redstoneCheckPassed) {
      return false;
    }
    if(!shouldDoWorkThisTick(20)) {
      return false;
    }
    return drainFullContainer() || fillEmptyContainer();
  }

  private boolean fillEmptyContainer() {
    FluidAndStackResult fill = FluidUtil.tryFillContainer(inventory[1], getOutputTanks()[0].getFluid());
    if (fill.result.fluidStack == null) {
      return false;
    }

    if (inventory[3] != null) {
      if (inventory[3].isStackable() && ItemUtil.areStackMergable(inventory[3], fill.result.itemStack)
          && inventory[3].stackSize < inventory[3].getMaxStackSize()) {
        fill.result.itemStack.stackSize += inventory[3].stackSize;
      } else {
        return false;
      }
    }

    getOutputTanks()[0].setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(1, fill.remainder.itemStack);
    setInventorySlotContents(3, fill.result.itemStack);

    setTanksDirty();
    markDirty();
    return false;
  }

  private boolean drainFullContainer() {
    FluidAndStackResult fill = FluidUtil.tryDrainContainer(inventory[0], this);
    if (fill.result.fluidStack == null) {
      return false;
    }

    if (inventory[2] != null) {
      if (inventory[2].isStackable() && ItemUtil.areStackMergable(inventory[2], fill.result.itemStack)
          && inventory[2].stackSize < inventory[2].getMaxStackSize()) {
        fill.result.itemStack.stackSize += inventory[2].stackSize;
      } else {
        return false;
      }
    }

    getInputTank(fill.result.fluidStack).setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(0, fill.remainder.itemStack);
    setInventorySlotContents(2, fill.result.itemStack);

    setTanksDirty();
    markDirty();
    return false;
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(tank.getFluidAmount() > 0) {
      NBTTagCompound fluidRoot = new NBTTagCompound();
      tank.getFluid().writeToNBT(fluidRoot);
      nbtRoot.setTag("tankContents", fluidRoot);
    } else {
      nbtRoot.removeTag("tankContents");
    }
  }

  public static SmartTank readTankFromItem(ItemStack stack) {
    if (stack == null || stack.stackTagCompound == null) {
      return null;
    }
    return readTankFromNbt(stack.getItemDamage(), stack.stackTagCompound);
  }

  private static SmartTank readTankFromNbt(int tankSize, NBTTagCompound nbtRoot) {
    SmartTank result = nbtRoot.hasKey("tankType") ? makeTank(nbtRoot.getInteger("tankType")) : makeTank(tankSize);
    if (nbtRoot.hasKey("tankContents")) {
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbtRoot.getTag("tankContents"));
      result.setFluid(fl);
    }
    return result;
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    tank = readTankFromNbt(metaTankType, nbtRoot);
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    metaTankType = getBlockMetadata();
    super.readCustomNBT(nbtRoot);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setInteger("tankType", getBlockMetadata());
    super.writeCustomNBT(nbtRoot);
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return tank;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { tank };
  }

  @Override
  public void setTanksDirty() {
    tankDirty = true;
  }

}
