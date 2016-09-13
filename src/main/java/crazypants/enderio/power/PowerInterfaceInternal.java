package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public class PowerInterfaceInternal implements IPowerInterface {
  
  private final IInternalPoweredTile con;
  private IInternalPowerReceiver er;
  private final EnumFacing side;

  public PowerInterfaceInternal(IInternalPoweredTile con, EnumFacing side) {
    this.con = con;
    if(con instanceof IInternalPowerReceiver) {
      er = (IInternalPowerReceiver)con;
    }
    this.side = side;
  }

  @Override
  public Object getDelegate() {
    return con;
  }

  @Override
  public int getEnergyStored() {
    return con.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return con.getMaxEnergyStored();

  }

  @Override
  public int receiveEnergy(int canOffer, boolean sim) {
    if(er == null) {
      return 0;
    }
    return er.receiveEnergy(side, canOffer, sim);
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return er != null;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }
  
}
