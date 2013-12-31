package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class PowerInterfaceBC implements IPowerInterface {

  private IPowerReceptor bcPower;

  public PowerInterfaceBC(IPowerReceptor powerReceptor) {
    bcPower = powerReceptor;
  }

  @Override
  public Object getDelegate() {
    return bcPower;
  }

  @Override
  public boolean canConduitConnect(ForgeDirection direction) {
    if(bcPower != null) {
      if(bcPower instanceof IPowerEmitter) {
        return ((IPowerEmitter) bcPower).canEmitPowerFrom(direction.getOpposite());
      }
      return PowerHandlerUtil.canConnectRecievePower(bcPower);
    }
    return false;
  }

  @Override
  public float getEnergyStored(ForgeDirection dir) {
    float result = 0;
    if(bcPower instanceof IInternalPowerReceptor) {
      result = ((IInternalPowerReceptor) bcPower).getPowerHandler().getEnergyStored();
    } else if(bcPower != null && !(bcPower instanceof IPowerEmitter)) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr != null) {
        result += pr.getEnergyStored();
      }
    }
    return result;
  }

  @Override
  public float getMaxEnergyStored(ForgeDirection dir) {
    float result = 0;
    if(bcPower instanceof IInternalPowerReceptor) {
      result = ((IInternalPowerReceptor) bcPower).getPowerHandler().getMaxEnergyStored();
    } else if(bcPower != null && !(bcPower instanceof IPowerEmitter)) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr != null) {
        result += pr.getMaxEnergyStored();
      }
    }
    return result;
  }

  @Override
  public float getPowerRequest(ForgeDirection dir) {
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null || pr.getType() == Type.ENGINE) {
        return 0;
      }
      return pr.powerRequest();
    }

    return 0;
  }

  @Override
  public float getMinEnergyReceived(ForgeDirection dir) {
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null) {
        return 0;
      }
      return pr.getMinEnergyReceived();
    }
    return 0;
  }

  @Override
  public float recieveEnergy(ForgeDirection opposite, float canOffer) {
    if(bcPower != null) {
      if(bcPower instanceof IInternalPowerReceptor) {
        return PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) bcPower, bcPower.getPowerReceiver(opposite), canOffer, Type.PIPE, opposite);
      }
      if(bcPower instanceof IPowerEmitter) {
        return 0;
      }
      PowerReceiver pr = bcPower.getPowerReceiver(opposite);
      if(pr == null) {
        return 0;
      }
      float offer = Math.min(pr.powerRequest(), canOffer);
      return pr.receiveEnergy(Type.PIPE, offer, opposite);
    }
    return 0;
  }

}
