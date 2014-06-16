package crazypants.enderio.machine.generator.combustion;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.fuels.IronEngineCoolant;
import buildcraft.api.fuels.IronEngineCoolant.Coolant;
import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.fuels.IronEngineFuel.Fuel;
import buildcraft.api.power.IPowerEmitter;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.PowerDistributor;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;

public class TileCombustionGenerator extends AbstractMachineEntity implements IPowerEmitter, IFluidHandler {

  private final FluidTank coolantTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  private final FluidTank fuelTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  private boolean tanksDirty;

  private int ticksRemaingFuel;
  private int ticksRemaingCoolant;
  private boolean active;

  private PowerDistributor powerDis;

  private float generated;

  private boolean inPause = false;

  private int maxOutputTick = 128;

  private static int IO_MB_TICK = 250;

  private Fuel curFuel;
  private Coolant curCoolant;

  public TileCombustionGenerator() {
    super(new SlotDefinition(-1, -1, -1, -1, -1, -1));
    powerHandler.configure(0, 0, 0, capacitorType.capacitor.getMaxEnergyStored());
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {
    boolean res = super.doPull(dir);
    BlockCoord loc = getLocation().getLocation(dir);
    IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
    if(target != null) {
      FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
      if(infos != null) {
        for (FluidTankInfo info : infos) {
          if(info.fluid != null && info.fluid.amount > 0) {
            if(canFill(dir, info.fluid.getFluid())) {
              FluidStack canPull = info.fluid.copy();
              canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
              FluidStack drained = target.drain(dir.getOpposite(), canPull, false);
              if(drained != null && drained.amount > 0) {
                int filled = fill(dir, drained, false);
                if(filled > 0) {
                  drained = target.drain(dir.getOpposite(), filled, true);
                  fill(dir, drained, true);
                  return res;
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
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCombustionGenerator.unlocalisedName;
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return !isSideDisabled(side.ordinal());
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    int res = 0;    
    if(IronEngineCoolant.isCoolant(resource.getFluid())) {
      res = getCoolantTank().fill(resource, doFill);
    } else if(IronEngineFuel.getFuelForFluid(resource.getFluid()) != null) {
      res = getFuelTank().fill(resource, doFill);
    }
    if(res > 0) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
    if(powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    boolean res = false;

    if(!redstoneChecksPassed) {
      if(active) {
        active = false;
        res = true;
      }
      return res;
    } else {

      boolean isActive = generateEnergy();
      if(isActive != this.active) {
        active = isActive;
        res = true;
      }

      if(storedEnergy >= capacitorType.capacitor.getMaxEnergyStored()) {
        inPause = true;        
      }       

      transmitEnergy();
    }

    if(tanksDirty && worldObj.getTotalWorldTime() % 10 == 0) {
      PacketHandler.sendToAllAround(new PacketCombustionTank(this), this);
      tanksDirty = false;
    }

    return res;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return 0;
  }

  private boolean transmitEnergy() {
    if(storedEnergy <= 0) {
      return false;
    }
    if(powerDis == null) {
      powerDis = new PowerDistributor(new BlockCoord(this));
    }
    float transmitted = powerDis.transmitEnergy(worldObj, Math.min(maxOutputTick, storedEnergy));
    storedEnergy -= transmitted;
    return transmitted > 0;
  }

  private boolean generateEnergy() {
   
    generated = 0;

    if((ticksRemaingCoolant <= 0 && getCoolantTank().getFluidAmount() <= 0) ||
        (ticksRemaingFuel <= 0 && getFuelTank().getFluidAmount() <= 0) ||
        storedEnergy >= powerHandler.getMaxEnergyStored()) {
      return false;
    }

    //once full, don't start again until we have drained 10 seconds worth of power to prevent
    //flickering on and off constantly when powering a machine that draws less than this produces
    if(inPause && storedEnergy >= (powerHandler.getMaxEnergyStored() - (curFuel.powerPerCycle * 200))) {
      return false;
    }
    inPause = false;

    boolean res = false;
    ticksRemaingFuel--;
    if(ticksRemaingFuel <= 0) {
      curFuel = getFuelTank().getFluid() == null ? null : IronEngineFuel.getFuelForFluid(getFuelTank().getFluid().getFluid());
      if(curFuel == null) {
        return false;
      }
      FluidStack drained = getFuelTank().drain(100, true);
      if(drained == null) {
        return false;
      }
      ticksRemaingFuel = getNumTicksPerMbFuel(curFuel) * drained.amount;
      
      res = true;
      tanksDirty = true;
    } else if(curFuel == null) {
      curFuel = getFuelTank().getFluid() == null ? null : IronEngineFuel.getFuelForFluid(getFuelTank().getFluid().getFluid());
      if(curFuel == null) {
        return false;
      }
    }

    ticksRemaingCoolant--;
    if(ticksRemaingCoolant <= 0) {
      curCoolant = IronEngineCoolant.getCoolant(getCoolantTank().getFluid());
      if(curCoolant == null) {
        return false;
      }
      FluidStack drained = getCoolantTank().drain(100, true);
      if(drained == null) {
        return false;
      }
      ticksRemaingCoolant = getNumTicksPerMbCoolant(curCoolant, curFuel) * drained.amount;
      res = true;
    } else if(curCoolant == null) {
      curCoolant = IronEngineCoolant.getCoolant(getCoolantTank().getFluid());
      if(curCoolant == null) {
        return false;
      }
    }

    float oldVal = storedEnergy;
    storedEnergy += curFuel.powerPerCycle;
    generated = curFuel.powerPerCycle;
    storedEnergy = Math.min(storedEnergy, capacitorType.capacitor.getMaxEnergyStored());

    return getFuelTank().getFluidAmount() > 0 && getCoolantTank().getFluidAmount() > 0;
  }

  public int getNumTicksPerMbFuel() {
    if(getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    return getNumTicksPerMbFuel(IronEngineFuel.getFuelForFluid(getFuelTank().getFluid().getFluid()));
  }

  public int getNumTicksPerMbCoolant() {
    if(getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    if(worldObj.isRemote) {
      curFuel = IronEngineFuel.getFuelForFluid(getFuelTank().getFluid().getFluid());
      curCoolant = IronEngineCoolant.getCoolant(getCoolantTank().getFluid());
    }
    return getNumTicksPerMbCoolant(curCoolant, curFuel);
  }

  static int getNumTicksPerMbFuel(Fuel fuel) {
    if(fuel == null) {
      return 0;
    }
    return fuel.totalBurningTime / 1000;
  }

  static int getNumTicksPerMbCoolant(Coolant coolant, Fuel fuel) {
    if(coolant == null || fuel == null) {
      return 0;
    }
    float power = fuel.powerPerCycle;
    float cooling = coolant.getDegreesCoolingPerMB(100);
    double toCool = 1d / (0.027 * power);
    int numTicks = (int) Math.round(toCool / (cooling * 1000));
    return numTicks;
  }

  @Override
  protected void updateStoredEnergyFromPowerHandler() {
    //no-op as we don't actually need a BC power handler for a generator
    //Need to clean this up
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (storedEnergy * 10);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(isSideDisabled(from.ordinal())) {
      return false;
    }
    return IronEngineCoolant.isCoolant(fluid) || IronEngineFuel.getFuelForFluid(fluid) != null;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if(isSideDisabled(from.ordinal())) {
      return new FluidTankInfo[0];
    }
    return new FluidTankInfo[] { getCoolantTank().getInfo(), getFuelTank().getInfo() };
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    active = nbtRoot.getBoolean("active");
    generated = nbtRoot.getFloat("generated");
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    if(nbtRoot.hasKey("coolantTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("coolantTank");
      if(tankRoot != null) {
        getCoolantTank().readFromNBT(tankRoot);
      } else {
        getCoolantTank().setFluid(null);
      }
    } else {
      getCoolantTank().setFluid(null);
    }

    if(nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("fuelTank");
      if(tankRoot != null) {
        getFuelTank().readFromNBT(tankRoot);
      } else {
        getFuelTank().setFluid(null);
      }
    } else {
      getFuelTank().setFluid(null);
    }

    ticksRemaingFuel = nbtRoot.getInteger("ticksRemaingFuel");
    ticksRemaingCoolant = nbtRoot.getInteger("ticksRemaingCoolant");
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(getCoolantTank().getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      getCoolantTank().writeToNBT(tankRoot);
      nbtRoot.setTag("coolantTank", tankRoot);
    }
    if(getFuelTank().getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      getFuelTank().writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
    }
    nbtRoot.setInteger("ticksRemaingFuel", ticksRemaingFuel);
    nbtRoot.setInteger("ticksRemaingCoolant", ticksRemaingCoolant);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("active", active);
    nbtRoot.setFloat("generated", generated);
  }

  public double getMjGeneratedLastTick() {
    if(!active) {
      return 0;
    }
    return generated;
  }

  @Override
  public float getPowerUsePerTick() {
    if(getFuelTank().getFluidAmount() <= 0) {
      return 0;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(getFuelTank().getFluid().getFluid());
    if(fuel == null) {
      return 0;
    }
    return fuel.powerPerCycle;
  }

  public FluidTank getCoolantTank() {
    return coolantTank;
  }

  public FluidTank getFuelTank() {
    return fuelTank;
  }

}
