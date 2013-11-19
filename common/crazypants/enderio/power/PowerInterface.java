package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import cofh.api.energy.IEnergyHandler;

public class PowerInterface {

  public static PowerInterface create(Object o) {
    if(o instanceof IEnergyHandler) {
      return new PowerInterface((IEnergyHandler) o);
    } else if(o instanceof IPowerReceptor) {
      return new PowerInterface((IPowerReceptor) o);
    }
    return null;
  }

  private IPowerReceptor bcPower;

  private IEnergyHandler rfPower;

  public PowerInterface(IPowerReceptor powerReceptor) {
    bcPower = powerReceptor;
  }

  public PowerInterface(IEnergyHandler powerReceptor) {
    rfPower = powerReceptor;
  }

  public Object getDelegate() {
    if(bcPower != null) {
      return bcPower;
    }
    return rfPower;
  }

  public boolean canConduitConnect(ForgeDirection direction) {
    if(rfPower != null) {
      return rfPower.canInterface(direction.getOpposite());
    }

    // bc
    if(bcPower != null) {
      if(bcPower instanceof IPowerEmitter) {
        return ((IPowerEmitter) bcPower).canEmitPowerFrom(direction.getOpposite());
      }
      return PowerHandlerUtil.canConnectRecievePower(bcPower);
    }
    return false;
  }

  public float getEnergyStored(ForgeDirection dir) {
    if(rfPower != null) {
      return rfPower.getEnergyStored(dir);
    }

    // bc
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

  public float getMaxEnergyStored(ForgeDirection dir) {
    if(rfPower != null) {
      return rfPower.getMaxEnergyStored(dir);
    }

    // bc
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

  public float getPowerRequest(ForgeDirection dir) {
    if(rfPower != null) {
      if(rfPower.canInterface(dir)) {
        return rfPower.receiveEnergy(dir, rfPower.getMaxEnergyStored(dir), true);
      }
      return 0;
    }

    // bc
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null || pr.getType() == Type.ENGINE) {
        return 0;
      }
      return pr.powerRequest();
    }

    return 0;
  }

  public float getMinEnergyReceived(ForgeDirection dir) {
    if(rfPower != null) {
      return 0;
    }

    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null) {
        return 0;
      }
      return pr.getMinEnergyReceived();
    }
    return 0;
  }

  public float recieveEnergy(ForgeDirection opposite, float canOffer) {
    if(rfPower != null) {
      return rfPower.receiveEnergy(opposite, (int) (canOffer * 10), false) / 10;
    }

    if(bcPower != null) {
      if(bcPower instanceof IInternalPowerReceptor) {
        return PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) bcPower, bcPower.getPowerReceiver(opposite), canOffer, Type.PIPE, opposite);
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
