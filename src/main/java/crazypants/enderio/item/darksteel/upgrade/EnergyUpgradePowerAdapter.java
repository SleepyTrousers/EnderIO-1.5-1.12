package crazypants.enderio.item.darksteel.upgrade;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import crazypants.enderio.power.tesla.ForgeToTeslaAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnergyUpgradePowerAdapter {

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(EnergyUpgradePowerAdapter.class);
  }

  private static final ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "powerhandler");
  private static final ResourceLocation KEY_TESLA = new ResourceLocation(EnderIO.DOMAIN, "teslahandler");

  private static boolean addTesla = Loader.isModLoaded("tesla");

  @SubscribeEvent
  public static void attachCapabilities(@Nonnull Item evt) {
    if (evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    if (evt.getItem() instanceof IDarkSteelItem) {
      EnergyUpgadeCap cap = new EnergyUpgadeCap(evt.getItemStack());
      evt.addCapability(KEY, cap);
      if (addTesla) {
        addTeslaWrapper(evt, cap);
      }
    }

  }

  private static void addTeslaWrapper(@Nonnull Item evt, @Nonnull EnergyUpgadeCap cap) {
    evt.addCapability(KEY_TESLA, new ForgeToTeslaAdapter(cap));
  }

}
