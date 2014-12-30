package crazypants.enderio.power;

import cofh.api.energy.IEnergyHandler;

public class EnergyHandlerPI extends EnergyReceiverPI {

  private IEnergyHandler rfPower;

  public EnergyHandlerPI(IEnergyHandler powerReceptor) {
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
