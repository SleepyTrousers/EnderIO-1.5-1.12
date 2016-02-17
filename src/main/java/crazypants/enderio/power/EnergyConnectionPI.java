package crazypants.enderio.power;

import cofh.api.energy.IEnergyConnection;
import net.minecraft.util.EnumFacing;

public class EnergyConnectionPI implements IPowerInterface {

  private IEnergyConnection delegate;

  public EnergyConnectionPI(IEnergyConnection delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object getDelegate() {
    return delegate;
  }

  @Override
  public boolean canConduitConnect(EnumFacing direction) {
    if(delegate != null && direction != null) {
      return delegate.canConnectEnergy(direction.getOpposite());
    }
    return false;
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    return 0;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
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
