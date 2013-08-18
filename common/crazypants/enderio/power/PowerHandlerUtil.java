package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerProvider;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.*;

public class PowerHandlerUtil {
  
  public static EnderPowerProvider createHandler(ICapacitor capacitor) {
    EnderPowerProvider ph = new EnderPowerProvider();
    ph.configure(0,capacitor.getMinEnergyReceived(),capacitor.getMaxEnergyReceived(),capacitor.getMinActivationEnergy(),capacitor.getMaxEnergyStored());
    ph.configurePowerPerdition(0, 0);
    //ph.setPerdition(new NullPerditionCalculator());
    return ph;
  }  

  public static void configure(EnderPowerProvider ph, ICapacitor capacitor) {
    ph.configure(0,capacitor.getMinEnergyReceived(),capacitor.getMaxEnergyReceived(),capacitor.getMinActivationEnergy(),capacitor.getMaxEnergyStored());
    if(ph.getEnergyStored() > ph.getMaxEnergyStored()) {
      ph.setEnergy(ph.getMaxEnergyStored());
    }
    //TODO: Setup perdition properly
    ph.configurePowerPerdition(0, 0);    
  }

  
  public static float transmitInternal(IInternalPowerReceptor receptor,float quantity, ForgeDirection from) {
    float used = quantity;

    if(receptor instanceof IConduitBundle) {      
      return transferToPowerNetwork((IConduitBundle)receptor, quantity);
    }

    EnderPowerProvider ph = receptor.getPowerHandler();
    if(ph == null) {
      return 0;
    }
    
    float maxEnergyStored = ph.getMaxEnergyStored();
    float maxEnergyRecieved = ph.getMaxEnergyReceived();
    float energyStored = ph.getEnergyStored();
    
    //Do all required functions except:
    //- We will handle perd'n ourselves and there is not need to drain excess from our engines as they are self regulating
    //- Also not making use of the doWork calls.
    ph.receiveEnergy(quantity, from);
    
    ph.setEnergy(energyStored);

    if (used < ph.getMinEnergyReceived()) {
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
  
  public static float transferToPowerNetwork(IConduitBundle bundle, float quantity) {
    IPowerConduit receptor = bundle.getConduit(IPowerConduit.class);    
    if(receptor.getNetwork() == null) {
      return 0;
    }
    NetworkPowerManager network = ((PowerConduitNetwork)receptor.getNetwork()).getPowerManager();
    float used = network.addEnergy(quantity);    
    return used;
  }

//  private static class NullPerditionCalculator extends PerditionCalculator {
//    
//    public float applyPerdition(PowerHandler powerHandler, float current, long ticksPassed) {
//      if(current <= 0) {
//        return 0;
//      }
//      float res = current - 0.001f;      
//      if(res >= current) {
//        System.out.println("PowerHandlerUtil.NullPerditionCalculator.applyPerdition: Fail! current is: " + current + " res is:" + res);
//      }
//      return  res; 
//    }
//  }
  
}
