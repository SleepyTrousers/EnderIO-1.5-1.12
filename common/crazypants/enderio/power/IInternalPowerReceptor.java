package crazypants.enderio.power;

import buildcraft.api.power.IPowerReceptor;

public interface IInternalPowerReceptor extends IPowerReceptor {

  MutablePowerProvider getPowerHandler();

  void applyPerdition();

}
