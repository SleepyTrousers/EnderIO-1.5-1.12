package crazypants.enderio.machine.drain;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.drain.FluidHelper.ReturnObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import crazypants.util.FluidUtil.FluidAndStackResult;
import crazypants.util.ITankAccess;
import crazypants.util.ItemUtil;

public class TileDrain extends AbstractPoweredTaskEntity implements IFluidHandler, IWaterSensitive, IDrainingCallback, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  private static int IO_MB_TICK = 100;

  protected SmartTank tank = new SmartTank(2 * ONE_BLOCK_OF_LIQUID);
  protected int lastUpdateLevel = -1;
  
  private boolean tankDirty = false;

  public TileDrain(int meta) {
    super(new SlotDefinition(1, 1, 1));
  }

  public TileDrain() {
    this(0);
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
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return 0;
  }

  protected int fillInternal(FluidStack resource, boolean doFill) {
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

  protected FluidStack drainInternal(FluidStack resource, boolean doDrain) {
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

  protected FluidStack drainInternal(int maxDrain, boolean doDrain) {
    FluidStack res = tank.drain(maxDrain, doDrain);
    if(res != null && res.amount > 0 && doDrain) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return false;
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
    return ModObject.blockDrain.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack item) {
    System.out.println(i + ": " + item);
    if(i == 0) {
      return FluidContainerRegistry.isEmptyContainer(item) || item.getItem() == Items.bucket;
    }
    return false;
  }

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if(canTick(redstoneChecksPassed) && redstoneChecksPassed) {
      return doTick();
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if(redstoneChecksPassed) {
      if(getEnergyStored() < getPowerUsePerTick()) {
        return false;
      }
      usePower();
    }
    int curScaled = getProgressScaled(16);
    if(curScaled != lastProgressScaled) {
      sendTaskProgressPacket();
      lastProgressScaled = curScaled;
    }
    return true;
  }

  protected boolean doTick() {
    if(shouldDoWorkThisTick(20)) {
      fillEmptyContainer();
    }

    int filledLevel = getFilledLevel();
    if(lastUpdateLevel != filledLevel) {
      lastUpdateLevel = filledLevel;
      tankDirty = false;
      return true;
    }

    if(tankDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketDrain(this), this);
      worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
      tankDirty = false;
    }
    
    // scale by cap
    int modulo = 10;
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      modulo = 20;
      break;
    case ACTIVATED_CAPACITOR:
      modulo = 10;
      break;
    case ENDER_CAPACITOR:
      modulo = 2;
      break;
    }
    
    if (shouldDoWorkThisTick(modulo) && tank.getAvailableSpace() >= ONE_BLOCK_OF_LIQUID) {
      FluidHelper instance;
      if (tank.getFluid() != null) {
        instance = crazypants.enderio.machine.drain.FluidHelper.getInstance(worldObj, getLocation(), tank.getFluid());
      } else {
        instance = crazypants.enderio.machine.drain.FluidHelper.getInstance(worldObj, getLocation());
      }
      if (instance != null) {
        instance.setDrainingCallback(this);
        ReturnObject pullFluid = instance.eatOrPullFluid();
        if (pullFluid.result != null) {
          fillInternal(pullFluid.result, true);
          usePower(Config.drainPerBucketEnergyUseRF);
        } else if (pullFluid.inProgress) {
          usePower(Config.drainPerSourceBlockMoveEnergyUseRF);
        }
        dryruncount = 0;
        return true;
      } else {
        if (dryruncount++ > 60) {
          dryruncount = 0;
          nowater.clear();
          if (registered) {
            InfiniteWaterSourceStopper.getInstance().unregister(worldObj, this);
            registered = false;
          }
        }
      }
    }
    return false;
  }
  
  public int getComparatorOutput() {
    FluidTankInfo info = getTankInfo(null)[0];
    return info == null || info.fluid == null ? 0 : (int) (((double) info.fluid.amount / (double) info.capacity) * 15);
  }

  private boolean fillEmptyContainer() {
    FluidAndStackResult fill = FluidUtil.tryFillContainer(inventory[0], getOutputTanks()[0].getFluid());
    if (fill.result.fluidStack == null) {
      return false;
    }

    if (inventory[1] != null) {
      if (inventory[1].isStackable() && ItemUtil.areStackMergable(inventory[1], fill.result.itemStack)
          && inventory[1].stackSize < inventory[1].getMaxStackSize()) {
        fill.result.itemStack.stackSize += inventory[1].stackSize;
      } else {
        return false;
      }
    }

    getOutputTanks()[0].setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(0, fill.remainder.itemStack);
    setInventorySlotContents(1, fill.result.itemStack);

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
    }
    if (!nowater.isEmpty()) {
      int[] nowaterArray = new int[nowater.size() * 3];
      int i = 0;
      for (BlockCoord bc : nowater) {
        nowaterArray[i++] = bc.x;
        nowaterArray[i++] = bc.y;
        nowaterArray[i++] = bc.z;
      }
      nbtRoot.setIntArray("nowater", nowaterArray);
    }
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    if(nbtRoot.hasKey("tankContents")) {
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbtRoot.getTag("tankContents"));
      tank.setFluid(fl);
    } else {
      tank.setFluid(null);
    }
    
    if(nbtRoot.hasKey("nowater")) {
      int[] nowaterArray = nbtRoot.getIntArray("nowater");
      int i = 0;
      while (i < nowaterArray.length) {
        nowater.add(new BlockCoord(nowaterArray[i++], nowaterArray[i++], nowaterArray[i++]));
      }
    } else {
      nowater.clear();
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    currentTask = createTask();
  }
  
  IPoweredTask createTask() {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF * 40, 250000, Config.drainContinuousEnergyUseRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF * 40, 500000, Config.drainContinuousEnergyUseRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF * 40, 1000000, Config.drainContinuousEnergyUseRF));
      break;
    }
    currentTask = createTask();
  }

  @Override
  public void setWorldObj(World p_145834_1_) {
    super.setWorldObj(p_145834_1_);
    if (!nowater.isEmpty() && !registered) {
      // actually part of readCommon(nbt), but the world object is not yet set
      // when that is called
      InfiniteWaterSourceStopper.getInstance().register(worldObj, this);
      registered = true;
    }
  }

  protected Set<BlockCoord> nowater = new HashSet<BlockCoord>();
  protected boolean registered = false;
  protected int dryruncount = 0;
  
  @Override
  public boolean preventInfiniteWaterForming(World world, BlockCoord bc) {
    return nowater.contains(bc);
  }

  @Override
  public void onWaterDrain(World world, BlockCoord bc) {
	  if (!registered) {
		  InfiniteWaterSourceStopper.getInstance().register(world, this);
		  registered = true;
	  }
	  nowater.add(bc);
  }

  @Override
  public void onWaterDrainNearby(World world, BlockCoord bc) {
	  nowater.add(bc);
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return null;
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
