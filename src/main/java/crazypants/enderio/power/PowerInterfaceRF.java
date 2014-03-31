package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;

public class PowerInterfaceRF implements IPowerInterface {
  private IEnergyHandler rfPower;

  public PowerInterfaceRF(IEnergyHandler powerReceptor) {
    rfPower = powerReceptor;
  }

  @Override
  public Object getDelegate() {
    return rfPower;
  }

  @Override
  public boolean canConduitConnect(ForgeDirection direction) {
    if(rfPower != null && direction != null) {
      return rfPower.canInterface(direction.getOpposite());
    }
    return false;
  }

  @Override
  public float getEnergyStored(ForgeDirection dir) {
    if(rfPower != null && dir != null) {
      return rfPower.getEnergyStored(dir) / 10f;
    }
    return 0;
  }

  @Override
  public float getMaxEnergyStored(ForgeDirection dir) {
    if(rfPower != null && dir != null) {
      return rfPower.getMaxEnergyStored(dir) / 10f;
    }
    return 0;
  }

  @Override
  public float getPowerRequest(ForgeDirection dir) {
    if(rfPower != null && dir != null && rfPower.canInterface(dir)) {
      return rfPower.receiveEnergy(dir, 99999999, true) / 10f;
    }
    return 0;
  }


  public static float getPowerRequestMJ(ForgeDirection dir, IEnergyHandler handler) {
    if(handler != null && dir != null && handler.canInterface(dir)) {
      return handler.receiveEnergy(dir, 99999999, true) / 10f;
    }
    return 0;
  }

  @Override
  public float getMinEnergyReceived(ForgeDirection dir) {
    return 0;
  }

  @Override
  public float recieveEnergy(ForgeDirection opposite, float canOffer) {
    if(rfPower != null && opposite != null) {
      return rfPower.receiveEnergy(opposite, (int) (canOffer * 10), false) / 10f;
    }
    return 0;
  }
}
