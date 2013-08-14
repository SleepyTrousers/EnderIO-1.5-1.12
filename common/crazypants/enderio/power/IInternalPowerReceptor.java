package crazypants.enderio.power;

import buildcraft.api.power.*;

public interface IInternalPowerReceptor extends IPowerReceptor {

  EnderPowerProvider getPowerHandler();

  void applyPerdition();

}
