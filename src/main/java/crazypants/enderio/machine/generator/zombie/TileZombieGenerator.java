package crazypants.enderio.machine.generator.zombie;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.AbstractGeneratorEntity;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerDistributor;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;

public class TileZombieGenerator extends AbstractGeneratorEntity implements IFluidHandler {

  private static int IO_MB_TICK = 250;

  final NutrientTank fuelTank = new NutrientTank(FluidContainerRegistry.BUCKET_VOLUME * 2);

  int outputPerTick = Config.zombieGeneratorRfPerTick;
  int tickPerBucketOfFuel = Config.zombieGeneratorTicksPerBucketFuel;

  private boolean tanksDirty;
  private boolean active = false;
  private PowerDistributor powerDis;

  private int ticksRemaingFuel;
  private boolean inPause;

  int pass = 0;

  public TileZombieGenerator() {
    super(new SlotDefinition(0, 0, 0));    
  }

  @Override
  public String getMachineName() {
    return ModObject.blockZombieGenerator.unlocalisedName;
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
  public int getPowerUsePerTick() {
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
      if(isActive != active) {
        active = isActive;
        res = true;
      }

      if(getEnergyStored() >= capacitorType.capacitor.getMaxEnergyStored()) {
        inPause = true;
      }

      transmitEnergy();
    }

    if(tanksDirty) {
      PacketHandler.sendToAllAround(new PacketZombieTank(this), this);
      tanksDirty = false;
    }

    return res;
  }

  private boolean generateEnergy() {

    int generated = 0;

    //once full, don't start again until we have drained 10 seconds worth of power to prevent
    //flickering on and off constantly when powering a machine that draws less than this produces
    if(inPause && getEnergyStored() >= (getMaxEnergyStored() - (outputPerTick * 200))) {
      return false;
    }
    inPause = false;

    if(fuelTank.getFluidAmount() < fuelTank.getCapacity() * 0.7f) {      
      return false;
    }
    
    
    ticksRemaingFuel--;    
    if(ticksRemaingFuel <= 0) {
      fuelTank.drain(1, true);
      ticksRemaingFuel = tickPerBucketOfFuel/1000;    
      tanksDirty = true;
    }    
    setEnergyStored(getEnergyStored() + outputPerTick);     
    return true;
  }

  private boolean transmitEnergy() {
    if(getEnergyStored() <= 0) {
      return false;
    }
    if(powerDis == null) {
      powerDis = new PowerDistributor(new BlockCoord(this));
    }
    int transmitted = powerDis.transmitEnergy(worldObj, Math.min(outputPerTick * 2, getEnergyStored()));
    setEnergyStored(getEnergyStored() - transmitted);    
    return transmitted > 0;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(resource == null || resource.getFluid() == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    int res = fuelTank.fill(resource, doFill);
    if(res > 0 && doFill) {
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

  public int getFluidStored(ForgeDirection from) {
    return fuelTank.getFluidAmount();
  }
  
  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    active = nbtRoot.getBoolean("active");
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    this.pass = pass;
    if(pass == 0) {
      return true;
    }
    if(pass == 1) {
      return fuelTank.getFluidAmount() > 0;
    }
    return false;
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
