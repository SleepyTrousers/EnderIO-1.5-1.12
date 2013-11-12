package crazypants.enderio.power;

import cofh.api.energy.IEnergyHandler;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;

public interface IInternalPowerReceptor extends IPowerReceptor, IEnergyHandler {

  PowerHandler getPowerHandler();

  void applyPerdition();

}
