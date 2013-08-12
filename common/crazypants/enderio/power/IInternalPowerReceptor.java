package crazypants.enderio.power;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;

public interface IInternalPowerReceptor extends IPowerReceptor {

  PowerHandler getPowerHandler();

  void applyPerdition();

}
