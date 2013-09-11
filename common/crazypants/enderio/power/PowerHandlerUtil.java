package crazypants.enderio.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PerditionCalculator;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;

public class PowerHandlerUtil {

  public static float getStoredEnergyForItem(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag == null) {
      return 0;
    }
    return tag.getFloat("storedEnergy");
  }
  
  public static void setStoredEnergyForItem(ItemStack item, float storedEnergy) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag == null) {
      tag = new NBTTagCompound();
    }    
    tag.setFloat("storedEnergy", storedEnergy);   
    item.setTagCompound(tag);    
  }
  
  public static PowerHandler createHandler(ICapacitor capacitor, IPowerReceptor pr, Type type) {
    PowerHandler ph = new PowerHandler(pr, type);
    ph.configure(capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    ph.configurePowerPerdition(0, 0);
    ph.setPerdition(new NullPerditionCalculator());
    return ph;
  }

  public static void configure(PowerHandler ph, ICapacitor capacitor) {
    ph.configure(capacitor.getMinEnergyReceived(), capacitor.getMaxEnergyReceived(), capacitor.getMinActivationEnergy(), capacitor.getMaxEnergyStored());
    if (ph.getEnergyStored() > ph.getMaxEnergyStored()) {
      ph.setEnergy(ph.getMaxEnergyStored());
    }
    ph.configurePowerPerdition(0, 0);
    ph.setPerdition(new NullPerditionCalculator());
  }

  public static float transmitInternal(IInternalPowerReceptor receptor, PowerReceiver pp, float quantity, Type type, ForgeDirection from) {

    PowerHandler ph = receptor.getPowerHandler();
    if (ph == null) {
      return 0;
    }

    // Do all required functions except:
    // - We will handle perd'n ourselves and there is not need to drain excess
    // from our engines as they are self regulating
    // - Also not making use of the doWork calls.
    float energyStored = pp.getEnergyStored();
    pp.receiveEnergy(type, quantity, from);
    ph.setEnergy(energyStored);

    float canUse = quantity;
    if (canUse < pp.getMinEnergyReceived()) {
      return 0;
    }
        
    canUse = Math.min(canUse, pp.getMaxEnergyReceived());
    //Don't overflow it    
    canUse = Math.min(canUse, pp.getMaxEnergyStored() - energyStored);    

    ph.setEnergy(energyStored + canUse);
    receptor.applyPerdition();

    return canUse;
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
