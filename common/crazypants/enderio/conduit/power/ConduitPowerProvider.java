package crazypants.enderio.conduit.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerProvider;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.MutablePowerProvider;

public class ConduitPowerProvider extends PowerProvider implements MutablePowerProvider {

  public static ConduitPowerProvider createHandler(ICapacitor capacitor, PowerConduit conduit) {
    ConduitPowerProvider ph = new ConduitPowerProvider(conduit);
    // EnderPowerProvider ph = new EnderPowerProvider();
    ph.configure(0, capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    ph.configurePowerPerdition(0, 0);
    // ph.setPerdition(new NullPerditionCalculator());
    return ph;
  }

  PowerConduit conduit;

  private ConduitPowerProvider(PowerConduit conduit) {
    this.conduit = conduit;
  }

  @Override
  public void receiveEnergy(float quantity, ForgeDirection from) {
    if (from != null) {
      powerSources[from.ordinal()] = 2;
    }

    if (conduit.getConectionMode(from) == ConnectionMode.OUTPUT) {
      return;
    }
    energyStored += quantity;

    if (energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    }
  }

  @Override
  public void setEnergy(float energyStored) {
    this.energyStored = energyStored;
  }

}
