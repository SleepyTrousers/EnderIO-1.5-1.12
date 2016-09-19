package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import crazypants.enderio.power.tesla.ForgeToTeslaAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnergyUpgradePowerAdapter {

  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "DSPowerHandler");
  private static final ResourceLocation KEY_TESLA = new ResourceLocation(EnderIO.DOMAIN, "DSPowerHandlerTeslae");
  
  private boolean addTesla = false;
  
  public EnergyUpgradePowerAdapter() {
    addTesla = Loader.isModLoaded("tesla");
  }
  
  @SubscribeEvent
  public void attachCapabilities(Item evt) {
    if(evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    if(evt.getItem() instanceof IDarkSteelItem) {
      EnergyUpgadeCap cap = new EnergyUpgadeCap(evt.getItemStack());
      evt.addCapability(KEY, cap);
      if(addTesla) {
        addTeslaWrapper(evt, cap);
      }
    }
    
  }

  private void addTeslaWrapper(Item evt, EnergyUpgadeCap cap) {
    evt.addCapability(KEY_TESLA, new ForgeToTeslaAdapter(cap));
  }

}
