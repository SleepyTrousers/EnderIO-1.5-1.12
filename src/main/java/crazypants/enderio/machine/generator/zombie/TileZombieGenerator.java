package crazypants.enderio.machine.generator.zombie;

import net.minecraft.block.Block;
import net.minecraft.inventory.ISidedInventory;
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
import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.fuels.IronEngineCoolant.Coolant;
import buildcraft.api.fuels.IronEngineFuel.Fuel;
import buildcraft.api.power.IPowerEmitter;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.PowerDistributor;
import crazypants.enderio.machine.generator.combustion.PacketTanks;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;

public class TileZombieGenerator extends AbstractMachineEntity implements IPowerEmitter, IFluidHandler {

  private static int IO_MB_TICK = 250;

  final NutrientTank fuelTank = new NutrientTank(FluidContainerRegistry.BUCKET_VOLUME * 2);

  float outputPerTick = (float) Config.zombieGeneratorMjPerTick;
  int tickPerMbFuel = Config.zombieGeneratorTicksPerMbFuel;

  private boolean tanksDirty;
  private boolean active = false;
  private PowerDistributor powerDis;

  private int ticksRemaingFuel;
  private boolean inPause;

  public TileZombieGenerator() {
    super(new SlotDefinition(0, 0, 0));
    powerHandler.configure(0, 0, 0, capacitorType.capacitor.getMaxEnergyStored());
  }

  @Override
  public String getMachineName() {
    return ModObject.blockZombieGenerator.unlocalisedName;
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return !isSideDisabled(side.ordinal());
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    return mode != IoMode.PUSH && mode != IoMode.PUSH_PULL;
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
  public float getPowerUsePerTick() {
    return outputPerTick;
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
    return 0.5f;
  }

  @Override
  public void onNeighborBlockChange(Block blockId) {
    super.onNeighborBlockChange(blockId);
    if(powerDis != null) {
      powerDis.neighboursChanged();
    }
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
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean res = false;

    if(!redstoneCheckPassed) {
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
      EnderIO.packetPipeline.sendToAllAround(new PacketTank(this), this);
      tanksDirty = false;
    }

    return res;
  }

  private boolean generateEnergy() {

    int generated = 0;

    //once full, don't start again until we have drained 10 seconds worth of power to prevent
    //flickering on and off constantly when powering a machine that draws less than this produces
    if(inPause && storedEnergy >= (powerHandler.getMaxEnergyStored() - (outputPerTick * 200))) {
      return false;
    }
    inPause = false;

    if(fuelTank.getFluidAmount() < fuelTank.getCapacity() * 0.9f) {      
      return false;
    }

    
    ticksRemaingFuel--;
    if(ticksRemaingFuel <= 0) {
      fuelTank.drain(1, true);
      ticksRemaingFuel = tickPerMbFuel;    
      tanksDirty = true;
    }
    
    float oldVal = storedEnergy;
    storedEnergy += outputPerTick;
    storedEnergy = Math.min(storedEnergy, capacitorType.capacitor.getMaxEnergyStored());
    return true;
  }

  private boolean transmitEnergy() {
    if(storedEnergy <= 0) {
      return false;
    }
    if(powerDis == null) {
      powerDis = new PowerDistributor(new BlockCoord(this));
    }
    float transmitted = powerDis.transmitEnergy(worldObj, Math.min(outputPerTick * 2, storedEnergy));
    storedEnergy -= transmitted;
    return transmitted > 0;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    tanksDirty = true;
    return fuelTank.fill(resource, doFill);
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
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return fluid != null && fluid.getID() == EnderIO.fluidNutrientDistillation.getID();
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { fuelTank.getInfo() };
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    active = nbtRoot.getBoolean("active");
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);

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

  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    if(fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      fuelTank.writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
    }
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("active", active);
  }

}
