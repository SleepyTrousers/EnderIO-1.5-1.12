package crazypants.enderio.power;

import cofh.api.energy.IEnergyReceiver;

public class EnergyHandlerPI extends EnergyReceiverPI {

  public EnergyHandlerPI(IEnergyReceiver powerReceptor) {
    super(powerReceptor);
  }

  @Override
  public boolean isInputOnly() {
    return false;
  }

  @Override
  public boolean isOutputOnly() {
    return false;
  }

}
