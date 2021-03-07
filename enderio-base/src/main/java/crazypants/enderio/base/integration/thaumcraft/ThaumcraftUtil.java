package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ThaumcraftUtil {

  static final @Nonnull String MODID_THAUMCRAFT = "thaumcraft";

  @SubscribeEvent
  public static void onPost(EnderIOLifecycleEvent.PostInit.Post event) {
    if (Loader.isModLoaded(MODID_THAUMCRAFT) && IntegrationConfig.enableThaumcraftAspects.get()) {
      ThaumcraftAspects.loadAspects();
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded(MODID_THAUMCRAFT)) {
      event.getRegistry().registerAll(ThaumaturgeRobesUpgrade.BOOTS, ThaumaturgeRobesUpgrade.LEGS, ThaumaturgeRobesUpgrade.CHEST,
          GogglesOfRevealingUpgrade.INSTANCE);
      Log.info("Dark Steel Upgrades: Thaumcraft integration loaded");
    }
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes(MODID_THAUMCRAFT, "thaumium_hoe", "void_hoe", "elemental_hoe");
    Log.info("Farming Station: Thaumcraft integration for hoes loaded");
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    if (Loader.isModLoaded(MODID_THAUMCRAFT)) {
      String manaBean = "ItemManaBean"; // TODO find real IDs, check if the farmer still works in 1.12
      String manaPod = "blockManaPod";

      Things blockThing = new Things("block:" + MODID_THAUMCRAFT + ":" + manaPod);
      Things itemThing = new Things("block:" + MODID_THAUMCRAFT + ":" + manaBean);

      NNList<Block> blocks = blockThing.getBlocks();
      ItemStack itemStack = itemThing.getItemStack();

      if (!blocks.isEmpty() && Prep.isValid(itemStack)) {
        event.getRegistry().register(new ManaBeanFarmer(blocks.get(0), itemStack).setRegistryName(MODID_THAUMCRAFT, "manabean"));
        Log.info("Farming Station: Thaumcraft integration for farming mana beans loaded");
      } else {
        Log.info("Farming Station: Thaumcraft integration for farming mana beans not loaded");
      }
    }
  }

}
