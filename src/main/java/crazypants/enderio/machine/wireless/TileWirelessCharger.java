package crazypants.enderio.machine.wireless;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileWirelessCharger extends TileEntityEio implements IInternalPowerReceiver, IWirelessCharger {

  public static final int MAX_ENERGY_STORED = 200000;
  public static final int MAX_ENERGY_IN = 10000;
  public static final int MAX_ENERGY_OUT = 10000;
  
  int storedEnergyRF; 
  
  private double lastPowerUpdate = -1;
  
  private boolean registered = false;
  
  public TileWirelessCharger() {    
  }
 
  @Override
  public void invalidate() {
    super.invalidate();    
    WirelessChargerController.instance.deregisterCharger(this);
    registered = false;
  }

  @Override
  public void doUpdate() {
    
    if (worldObj.isRemote) {
      return;
    }
    
    if(!registered) {
      WirelessChargerController.instance.registerCharger(this);
      registered = true;
    }
    
    if( (lastPowerUpdate == -1) || 
        (lastPowerUpdate == 0 && storedEnergyRF > 0) ||
        (lastPowerUpdate > 0 && storedEnergyRF == 0) ||
        (lastPowerUpdate != storedEnergyRF && shouldDoWorkThisTick(20))
        ) {
      lastPowerUpdate = storedEnergyRF;
      PacketHandler.sendToAllAround(new PacketStoredEnergy(this), this);
    }

  }
  
  @Override
  public boolean chargeItems(ItemStack[] items) {
    boolean chargedItem = false;
    int available = Math.min(MAX_ENERGY_OUT, storedEnergyRF);
    for (int i=0,end=items.length ; i<end  && available>0 ; i++) {
      ItemStack item = items[i];
      if(item != null) {
        if(item.getItem() instanceof IEnergyContainerItem && item.stackSize == 1) {
          IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

          int max = chargable.getMaxEnergyStored(item);
          int cur = chargable.getEnergyStored(item);
          int canUse = Math.min(available, max - cur);
          if(cur < max) {
            int used = chargable.receiveEnergy(item, canUse, false);
            if(used > 0) {
              storedEnergyRF = storedEnergyRF - used;
              chargedItem = true;
              available -= used;
            }
          }
        }
      }
    }
    return chargedItem;
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {    
    root.setInteger("storedEnergyRF", storedEnergyRF);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    if(root.hasKey("storedEnergy")) {
      double storedMJ = root.getDouble("storedEnergy");
      storedEnergyRF = (int)(storedMJ * 10);
    } else {
      storedEnergyRF = root.getInteger("storedEnergyRF");
    }
  }

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return MAX_ENERGY_IN;
  }

  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyStored() {
    return MAX_ENERGY_STORED;
  }

  @Override
  public void setEnergyStored(int stored) {
    storedEnergyRF = stored;    
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int takeEnergy(int max) {
    if (isActive()) {
      int prev = storedEnergyRF;
      storedEnergyRF = Math.max(0, storedEnergyRF - max);
      return prev - storedEnergyRF;
    }
    return 0;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return MAX_ENERGY_STORED;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return true;
  }

  @Override
  public World getWorld() {    
    return getWorldObj();
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  public boolean isActive() {
    return getEnergyStored() > 0 && !isPoweredRedstone();
  }
}
