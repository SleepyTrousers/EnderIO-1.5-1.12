package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;

public class PowerHandlerUtil {

  public static EnderPowerProvider createHandler(ICapacitor capacitor) {
    EnderPowerProvider ph = new EnderPowerProvider();
    ph.configure(0, capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    ph.configurePowerPerdition(0, 0);
    // ph.setPerdition(new NullPerditionCalculator());
    return ph;
  }

  public static void configure(EnderPowerProvider ph, ICapacitor capacitor) {
    ph.configure(0, capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    if (ph.getEnergyStored() > ph.getMaxEnergyStored()) {
      ph.setEnergy(ph.getMaxEnergyStored());
    }
    ph.configurePowerPerdition(0, 0);
  }

  public static float transmitInternal(IInternalPowerReceptor receptor, float quantity, ForgeDirection from) {

    MutablePowerProvider ph = receptor.getPowerHandler();
    if (ph == null) {
      return 0;
    }

    // Do all required functions except:
    // - We will handle perd'n ourselves and there is not need to drain excess
    // from our engines as they are self regulating
    // - Also not making use of the doWork calls.
    float energyStored = ph.getEnergyStored();    
    ph.receiveEnergy(quantity, from);
    ph.setEnergy(energyStored);

    float canUse = quantity;
    if (canUse < ph.getMinEnergyReceived()) {
      return 0;
    }

    canUse = Math.min(canUse, ph.getMaxEnergyReceived());
    //Don't overflow it    
    canUse = Math.min(canUse, ph.getMaxEnergyStored() - energyStored);    

    ph.setEnergy(energyStored + canUse);
    receptor.applyPerdition();

    return canUse;
  }



}
