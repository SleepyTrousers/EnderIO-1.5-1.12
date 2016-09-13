package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public class PowerInterfaceInternal implements IPowerInterface {
  
  private final IInternalPoweredTile con;
  private IInternalPowerReceiver er;

  public PowerInterfaceInternal(IInternalPoweredTile con) {
    this.con = con;
    if(con instanceof IInternalPowerReceiver) {
      er = (IInternalPowerReceiver)con;
    }
  }

  @Override
  public Object getDelegate() {
    return con;
  }

  @Override
  public boolean canConnect(EnumFacing from) {
    if(from != null) {
      from = from.getOpposite();
    }
    return con.canConnectEnergy(from);
  }

  @Override
  public int getEnergyStored(EnumFacing dir) {
    return con.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing dir) {
    return con.getMaxEnergyStored();

  }

  @Override
  public int recieveEnergy(EnumFacing dir, int canOffer) {
    if(er == null) {
      return 0;
    }
    return er.receiveEnergy(dir, canOffer, false);
  }

  @Override
  public boolean isOutputOnly() {
    return er == null;
  }

  @Override
  public boolean isInputOnly() {
    return false;
  }

  public static int getPowerRequest(EnumFacing north, IInternalPowerReceiver pr) {
    return pr.receiveEnergy(north, 999999, true);
  }

  
}
