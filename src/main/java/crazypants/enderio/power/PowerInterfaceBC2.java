package crazypants.enderio.power;

import buildcraft.api.mj.IBatteryObject;
import net.minecraftforge.common.util.ForgeDirection;

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
  public float getEnergyStored(ForgeDirection dir) {
    return (float) battery.getEnergyStored();
  }

  @Override
  public float getMaxEnergyStored(ForgeDirection dir) {
    return (float) battery.maxCapacity();
  }

  @Override
  public float getPowerRequest(ForgeDirection dir) {
    return (float) battery.getEnergyRequested();
  }

  @Override
  public float getMinEnergyReceived(ForgeDirection dir) {
    return (float) battery.minimumConsumption();
  }

  @Override
  public float recieveEnergy(ForgeDirection opposite, float canOffer) {
    return (float) battery.addEnergy(canOffer);
  }
}
