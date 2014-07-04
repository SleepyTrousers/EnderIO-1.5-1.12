package crazypants.enderio.machine.wireless;

import cofh.api.energy.IEnergyContainerItem;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileWirelessCharger extends TileEntityEio implements IInternalPowerReceptor, IWirelessCharger {

  public static final int MAX_ENERGY_STORED_MJ = 100000;
  public static final int MAX_ENERGY_IN = 1000;
  public static final int MAX_ENERGY_OUT = 1000;
  
  double storedEnergy;
  private PowerHandler powerHandler;
  
  private double lastPowerUpdate = -1;
  
  private boolean registered = false;
  
  public TileWirelessCharger() {
    powerHandler = new PowerHandler(this, Type.STORAGE);
    powerHandler.configure(0, MAX_ENERGY_IN, 0, MAX_ENERGY_IN);
  }
 
  @Override
  public void invalidate() {
    super.invalidate();    
    WirelessChargerController.instance.deregisterCharger(this);
    registered = false;
  }

  @Override
  public void updateEntity() {
    
    if(worldObj == null || worldObj.isRemote) {
      return;
    }
    
    if(!registered) {
      WirelessChargerController.instance.registerCharger(this);
      registered = true;
    }
    
    double stored = powerHandler.getEnergyStored();
    if(stored > 0) {
      storedEnergy += stored;
      storedEnergy = Math.min(storedEnergy, MAX_ENERGY_STORED_MJ);
      if(stored > (MAX_ENERGY_STORED_MJ - 2)) {
        powerHandler.configure(0, 0, 0, 0);
      } else {
        powerHandler.configure(0, MAX_ENERGY_IN, 0, MAX_ENERGY_IN);
      }
      powerHandler.setEnergy(0);
    }
    
    if( (lastPowerUpdate == -1) || 
        (lastPowerUpdate == 0 && storedEnergy > 0) ||
        (lastPowerUpdate > 0 && storedEnergy == 0) ||
        (lastPowerUpdate != storedEnergy && worldObj.getTotalWorldTime() % 20 == 0)
        ) {
      lastPowerUpdate = storedEnergy;
      EnderIO.packetPipeline.sendToAllAround(new PacketStoredEnergy(this), this);
    }

  }
  
  public boolean chargeItems(ItemStack[] items) {    
    boolean chargedItem = false;
    double available = Math.min(MAX_ENERGY_OUT, storedEnergy);
    for (ItemStack item : items) {
      if(item != null && available > 0) {
        float used = 0;
        if(item.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

          float max = chargable.getMaxEnergyStored(item);
          float cur = chargable.getEnergyStored(item);
          double canUse = Math.min(available * 10, max - cur);
          if(cur < max) {
            used = chargable.receiveEnergy(item, (int) canUse, false) / 10;
            // TODO: I should be able to use 'used' but it is always returning 0
            // ATM.
            used = (chargable.getEnergyStored(item) - cur) / 10;
          }

        }
        if(used > 0) {
          storedEnergy = storedEnergy - used;
          chargedItem = true;
          available -= used;
        }
      }
    }
    return chargedItem;
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    root.setDouble("storedEnergy", storedEnergy);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    storedEnergy = root.getDouble("storedEnergy");
  }

  @Override
  public void doWork(PowerHandler arg0) {
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection arg0) {
    return powerHandler.getPowerReceiver();
  }

  @Override
  public World getWorld() {
    return getWorldObj();
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {    
    double maxMj = Math.min(maxReceive / 10, MAX_ENERGY_IN);
    double canRecieve = Math.min(MAX_ENERGY_STORED_MJ - storedEnergy, maxMj);
    int canRecieveRF = (int)(canRecieve * 10);
    if(!simulate) {
      storedEnergy += (canRecieveRF / 10f);
    }    
    return canRecieveRF;
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int)(storedEnergy * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return MAX_ENERGY_STORED_MJ * 10;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return true;
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(this);
  }

}
