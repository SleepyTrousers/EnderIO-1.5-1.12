package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

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
    if(bcPower instanceof IPipeTile) {
      IPipeTile pipeTile = (IPipeTile) bcPower;
      return pipeTile.getPipeType() == PipeType.POWER;
    }
    return true;    
  }

  @Override
  public int getEnergyStored(ForgeDirection dir) {    
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr != null) {
         return (int)(pr.getEnergyStored() * 10);
      }
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection dir) {
    if(bcPower != null && !(bcPower instanceof IPowerEmitter)) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr != null) {
        return (int)(pr.getMaxEnergyStored() * 10);
      }
    }
    return 0;
  }

  @Override
  public int getPowerRequest(ForgeDirection dir) {
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null || pr.getType() == Type.ENGINE) {
        return 0;
      }
      return (int)( pr.powerRequest() * 10);
    }
    return 0;
  }

  @Override
  public int getMinEnergyReceived(ForgeDirection dir) {
    if(bcPower != null) {
      PowerReceiver pr = bcPower.getPowerReceiver(dir);
      if(pr == null) {
        return 0;
      }
      return (int) (pr.getMinEnergyReceived() * 10);
    }
    return 0;
  }

  @Override
  public int recieveEnergy(ForgeDirection opposite, int canOffer) {
    if(bcPower != null) {     
      if(bcPower instanceof IPowerEmitter) {
        return 0;
      }
      PowerReceiver pr = bcPower.getPowerReceiver(opposite);
      if(pr == null) {
        return 0;
      }
      double offer = Math.min(pr.powerRequest(), canOffer / 10f);      
      double used = pr.receiveEnergy(Type.PIPE, offer, opposite);
      return (int) (used * 10);
    }
    return 0;
  }



}
