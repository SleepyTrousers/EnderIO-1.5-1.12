package crazypants.enderio.power.rf;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IPowerInterface;
import net.minecraft.util.EnumFacing;

public class PowerInterfaceRF implements IPowerInterface {

  private final IEnergyConnection con;
  private IEnergyHandler eh;
  private IEnergyReceiver er;
  private final EnumFacing side;

  public PowerInterfaceRF(IEnergyConnection con, EnumFacing side) {
    this.con = con;
    if(con instanceof IEnergyHandler) {
      eh = (IEnergyHandler)con;
    }
    if(con instanceof IEnergyReceiver) {
      er = (IEnergyReceiver)con;
    }
    this.side = side;
  }

  @Override
  public Object getProvider() {
    return con;
  }

  @Override
  public int getEnergyStored() {
    if (eh == null) {
      return 0;
    }
    return eh.getEnergyStored(side);
  }

  @Override
  public int getMaxEnergyStored() {
    if (eh == null) {
      return 0;
    }
    return eh.getMaxEnergyStored(side);

  }

  @Override
  public int receiveEnergy(int canOffer, boolean sim) {
    if(er == null) {
      return 0;
    }
    return er.receiveEnergy(side, canOffer, sim);
  }

  public static int getPowerRequest(EnumFacing north, IInternalPowerReceiver pr) {
    return pr.receiveEnergy(north, 999999, true);
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
