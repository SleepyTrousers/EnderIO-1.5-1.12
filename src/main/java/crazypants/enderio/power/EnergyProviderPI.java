package crazypants.enderio.power;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.util.EnumFacing;

public class EnergyProviderPI implements IPowerInterface {
  
  private IEnergyProvider rfPower;

  public EnergyProviderPI(IEnergyProvider powerReceptor) {
    rfPower = powerReceptor;
  }

  @Override
  public Object getDelegate() {
    return rfPower;
  }

  @Override
  public boolean canConduitConnect(EnumFacing direction) {
    if(rfPower != null && direction != null) {
      return rfPower.canConnectEnergy(direction.getOpposite());
    }
    return false;
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    if(rfPower != null && dir != null) {
      return rfPower.getEnergyStored(dir);
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    if(rfPower != null && dir != null) {
      return rfPower.getMaxEnergyStored(dir);
    }
    return 0;
  }

  @Override
  public int getPowerRequest(EnumFacing dir) {
    return 0;
  }
  
  @Override
  public int getMinEnergyReceived(EnumFacing dir) {
    return 0;
  }

  @Override
  public int recieveEnergy(EnumFacing opposite, int canOffer) {
    return 0;
  }

  @Override
  public boolean isOutputOnly() {
    return true;
  }

  @Override
  public boolean isInputOnly() {
    return false;
  }

}
