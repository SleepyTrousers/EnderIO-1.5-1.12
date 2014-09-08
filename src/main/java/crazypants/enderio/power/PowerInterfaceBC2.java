package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.mj.IBatteryObject;

public class PowerInterfaceBC2 implements IPowerInterface {
  
  private final IBatteryObject battery;

  public PowerInterfaceBC2(IBatteryObject battery) {
    this.battery = battery;
  }

  @Override
  public Object getDelegate() {
    return battery;
  }

  @Override
  public boolean canConduitConnect(ForgeDirection direction) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection dir) {
    return (int) (battery.getEnergyStored() * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection dir) {
    return (int) (battery.maxCapacity() * 10);
  }

  @Override
  public int getPowerRequest(ForgeDirection dir) {
    return (int)( battery.getEnergyRequested() * 10);
  }

  @Override
  public int getMinEnergyReceived(ForgeDirection dir) {
    return (int) (battery.minimumConsumption() * 10);
  }

  @Override
  public int recieveEnergy(ForgeDirection opposite, int canOffer) {
    double offer = Math.min(battery.getEnergyRequested(), canOffer/10f);
    return (int) (battery.addEnergy(offer) * 10);
  }
}
