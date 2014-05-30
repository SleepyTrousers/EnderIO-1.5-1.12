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
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.PowerDistributor;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;

public class TileCombustionGenerator extends AbstractMachineEntity implements IPowerEmitter, IFluidHandler {

  final FluidTank coolantTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  final FluidTank fuelTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
  private boolean tanksDirty;

  private int ticksRemaingFuel;
  private int ticksRemaingCoolant;
  private boolean active;

  private PowerDistributor powerDis;

  private float generated;

  private boolean inPause = false;

  private int maxOutputTick = 128;

  private static int IO_MB_TICK = 250;

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
    tanksDirty = true;
    if(IronEngineCoolant.isCoolant(resource.getFluid())) {
      return coolantTank.fill(resource, doFill);
    }
    if(IronEngineFuel.getFuelForFluid(resource.getFluid()) != null) {
      return fuelTank.fill(resource, doFill);
    }
    return 0;
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
    
    if(tanksDirty) {     
      EnderIO.packetPipeline.sendToAllAround(new PacketTanks(this), this);
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

    Fuel fuel = fuelTank.getFluid() == null ? null : IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    if(fuel == null) {
      return false;
    }

    if(coolantTank.getFluidAmount() <= 0 || storedEnergy >= powerHandler.getMaxEnergyStored()) {
      return false;
    }

    //once full, don't start again until we have drained 10 seconds worth of power to prevent
    //flickering on and off constantly when powering a machine that draws less than this produces
    if(inPause && storedEnergy >= (powerHandler.getMaxEnergyStored() - (fuel.powerPerCycle * 200))) {
      return false;
    }
    inPause = false;

    Coolant coolant = IronEngineCoolant.getCoolant(coolantTank.getFluid());
    if(coolant == null) {
      return false;
    }

    boolean res = false;
    ticksRemaingFuel--;
    if(ticksRemaingFuel <= 0) {
      fuelTank.drain(1, true);
      ticksRemaingFuel = getNumTicksPerMbFuel(fuel);
      res = true;
      tanksDirty = true;
    }
    ticksRemaingCoolant--;
    if(ticksRemaingCoolant <= 0) {
      coolantTank.drain(1, true);
      ticksRemaingCoolant = getNumTicksPerMbCoolant(coolant, fuel);
      res = true;
      tanksDirty = true;
    }

    float oldVal = storedEnergy;
    storedEnergy += fuel.powerPerCycle;
    generated = fuel.powerPerCycle;
    storedEnergy = Math.min(storedEnergy, capacitorType.capacitor.getMaxEnergyStored());

    return fuelTank.getFluidAmount() > 0 && coolantTank.getFluidAmount() > 0;
  }

  public int getNumTicksPerMbFuel() {
    if(fuelTank.getFluidAmount() <= 0) {
      return 0;
    }
    return getNumTicksPerMbFuel(IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid()));
  }

  public int getNumTicksPerMbCoolant() {
    if(fuelTank.getFluidAmount() <= 0) {
      return 0;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    Coolant coolant = IronEngineCoolant.getCoolant(coolantTank.getFluid());
    return getNumTicksPerMbCoolant(coolant, fuel);
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
    return new FluidTankInfo[] { coolantTank.getInfo(), fuelTank.getInfo() };
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
        coolantTank.readFromNBT(tankRoot);
      } else {
        coolantTank.setFluid(null);
      }
    } else {
      coolantTank.setFluid(null);
    }

    if(nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("fuelTank");
      if(tankRoot != null) {
        fuelTank.readFromNBT(tankRoot);
      } else {
        fuelTank.setFluid(null);
      }
    } else {
      fuelTank.setFluid(null);
    }

    ticksRemaingFuel = nbtRoot.getInteger("ticksRemaingFuel");
    ticksRemaingCoolant = nbtRoot.getInteger("ticksRemaingCoolant");
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(coolantTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      coolantTank.writeToNBT(tankRoot);
      nbtRoot.setTag("coolantTank", tankRoot);
    }
    if(fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      fuelTank.writeToNBT(tankRoot);
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
    if(fuelTank.getFluidAmount() <= 0) {
      return 0;
    }
    Fuel fuel = IronEngineFuel.getFuelForFluid(fuelTank.getFluid().getFluid());
    if(fuel == null) {
      return 0;
    }
    return fuel.powerPerCycle;
  }

}
