package crazypants.enderio.base.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.IFarmerJoe;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import crazypants.enderio.base.farming.fertilizer.Fertilizer;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ForestryUtil {

  private ForestryUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init(event);
      Fertilizer.registerFertilizer(new Bonemeal(FarmersRegistry.findItem("forestry", "fertilizer_compound")));
      Log.info("Farming Station: Forestry integration fully loaded");
    } else {
      Log.info("Farming Station: Forestry integration not loaded");
    }
  }

  public static void addUpgrades(@Nonnull DarkSteelRecipeManager manager) {
    if (Loader.isModLoaded("forestry")) {
      manager.addUpgrade(NaturalistEyeUpgrade.INSTANCE);
      manager.addUpgrade(ApiaristArmorUpgrade.HELMET);
      manager.addUpgrade(ApiaristArmorUpgrade.CHEST);
      manager.addUpgrade(ApiaristArmorUpgrade.LEGS);
      manager.addUpgrade(ApiaristArmorUpgrade.BOOTS);
    }
  }

}
