package crazypants.enderio.machine.wireless;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileWirelessCharger extends TileEntityEio implements IInternalPowerReceptor, IWirelessCharger {

  public static final int MAX_ENERGY_STORED = 1000000;
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
  public void updateEntity() {
    
    if(worldObj == null || worldObj.isRemote) {
      return;
    }
    
    if(!registered) {
      WirelessChargerController.instance.registerCharger(this);
      registered = true;
    }
    
    if( (lastPowerUpdate == -1) || 
        (lastPowerUpdate == 0 && storedEnergyRF > 0) ||
        (lastPowerUpdate > 0 && storedEnergyRF == 0) ||
        (lastPowerUpdate != storedEnergyRF && worldObj.getTotalWorldTime() % 20 == 0)
        ) {
      lastPowerUpdate = storedEnergyRF;
      EnderIO.packetPipeline.sendToAllAround(new PacketStoredEnergy(this), this);
    }

  }
  
  public boolean chargeItems(ItemStack[] items) {    
    boolean chargedItem = false;
    int available = Math.min(MAX_ENERGY_OUT, storedEnergyRF);
    for (ItemStack item : items) {
      if(item != null && available > 0) {
        int used = 0;
        if(item.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

          int max = chargable.getMaxEnergyStored(item);
          int cur = chargable.getEnergyStored(item);
          int canUse = Math.min(available, max - cur);
          if(cur < max) {
            used = chargable.receiveEnergy(item, (int) canUse, false);
          }
        }
        if(used > 0) {
          storedEnergyRF = storedEnergyRF - used;
          chargedItem = true;
          available -= used;
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
      storedEnergyRF = root.getInteger("storedEnergy");
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
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
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
  public BlockCoord getLocation() {
    return new BlockCoord(this);
  }

  @Override
  public World getWorld() {    
    return getWorldObj();
  }
  
  

}
