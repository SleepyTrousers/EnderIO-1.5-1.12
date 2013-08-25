package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PerditionCalculator;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;

public class PowerHandlerUtil {

  public static PowerHandler createHandler(ICapacitor capacitor, IPowerReceptor pr, Type type) {
    PowerHandler ph = new PowerHandler(pr, type);
    ph.configure(capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    // TODO: Setup perdition properly
    ph.configurePowerPerdition(0, 0);
    ph.setPerdition(new NullPerditionCalculator());
    return ph;
  }

  public static void configure(PowerHandler ph, ICapacitor capacitor) {
    ph.configure(capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    if (ph.getEnergyStored() > ph.getMaxEnergyStored()) {
      ph.setEnergy(ph.getMaxEnergyStored());
    }
    // TODO: Setup perdition properly
    ph.configurePowerPerdition(0, 0);
    ph.setPerdition(new NullPerditionCalculator());
  }

  public static float transmitInternal(IInternalPowerReceptor receptor, PowerReceiver pp, float quantity, Type type, ForgeDirection from) {
    float used = quantity;

    if (receptor instanceof IConduitBundle) {
      return transferToPowerNetwork((IConduitBundle) receptor, pp, quantity);
    }

    PowerHandler ph = receptor.getPowerHandler();
    if (ph == null) {
      return 0;
    }

    float maxEnergyStored = pp.getMaxEnergyStored();
    float maxEnergyRecieved = pp.getMaxEnergyReceived();
    float energyStored = pp.getEnergyStored();

    // Do all required functions except:
    // - We will handle perd'n ourselves and there is not need to drain excess
    // from our engines as they are self regulating
    // - Also not making use of the doWork calls.
    pp.receiveEnergy(type, quantity, from);

    ph.setEnergy(energyStored);

    if (used < pp.getMinEnergyReceived()) {
      return 0;
    }

    if (used > maxEnergyRecieved) {
      used = maxEnergyRecieved;
    }

    energyStored += used;

    if (energyStored > maxEnergyStored) {
      used -= energyStored - maxEnergyStored;
      energyStored = maxEnergyStored;
    } else if (energyStored < 0) {
      used -= energyStored;
      energyStored = 0;
    }

    ph.setEnergy(energyStored);

    receptor.applyPerdition();

    return used;

  }

  public static float transferToPowerNetwork(IConduitBundle bundle, PowerReceiver pp, float quantity) {
    IPowerConduit receptor = bundle.getConduit(IPowerConduit.class);
    if (receptor.getNetwork() == null) {
      return 0;
    }
    NetworkPowerManager network = ((PowerConduitNetwork) receptor.getNetwork()).getPowerManager();
    float used = network.addEnergy(quantity);
    return used;
  }

  private static class NullPerditionCalculator extends PerditionCalculator {

    @Override
    public float applyPerdition(PowerHandler powerHandler, float current, long ticksPassed) {
      if (current <= 0) {
        return 0;
      }
      float decAmount = 0.001f;
      float res;
      do {
        res = current - decAmount;
        decAmount *= 10;
      } while(res >= current && decAmount < PerditionCalculator.MIN_POWERLOSS);
      return res;
    }
  }

}
