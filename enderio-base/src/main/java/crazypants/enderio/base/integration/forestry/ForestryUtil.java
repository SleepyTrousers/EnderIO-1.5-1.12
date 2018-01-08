package crazypants.enderio.base.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import crazypants.enderio.base.handler.darksteel.IDarkSteelUpgrade;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ForestryUtil {

  private ForestryUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init(event);
      Log.info("Farming Station: Forestry integration for farming fully loaded");
    } else {
      Log.info("Farming Station: Forestry integration for farming not loaded");
    }
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    if (Loader.isModLoaded("forestry")) {
      event.getRegistry().register(new Bonemeal(FarmersRegistry.findItem("forestry", "fertilizer_compound")));
      Log.info("Farming Station: Forestry integration for fertilizing fully loaded");
    } else {
      Log.info("Farming Station: Forestry integration for fertilizing not loaded");
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded("forestry")) {
      final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
      registry.register(NaturalistEyeUpgrade.INSTANCE);
      registry.register(ApiaristArmorUpgrade.HELMET);
      registry.register(ApiaristArmorUpgrade.CHEST);
      registry.register(ApiaristArmorUpgrade.LEGS);
      registry.register(ApiaristArmorUpgrade.BOOTS);
      Log.info("Dark Steel Upgrades: Forestry integration loaded");
    }
  }

}
