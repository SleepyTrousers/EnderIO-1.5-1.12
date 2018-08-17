package crazypants.enderio.base.integration.actuallyadditions;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ActuallyadditionsUtil {

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("actuallyadditions", "item_fertilizer"));
    if (fertilizer.isValid()) {
      event.getRegistry().register(fertilizer);
      Log.info("Farming Station: Actually Additions integration loaded");
    } else {
      Log.info("Farming Station: Actually Additions integration not loaded");
    }
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes("actuallyadditions", "item_hoe_quartz", "item_hoe_emerald", "item_hoe_obsidian", "item_hoe_crystal_red",
        "item_hoe_crystal_blue", "item_hoe_crystal_light_blue", "item_hoe_crystal_black", "item_hoe_crystal_green", "item_hoe_crystal_white");
  }

  @ItemStackHolder("actuallyadditions:item_solidified_experience")
  public static final ItemStack AA_BOTTLE = null;

  public static boolean isAAXpBottle(@Nonnull ItemStack stack) {
    return AA_BOTTLE != null && stack.getItem() == AA_BOTTLE.getItem();
  }

  public static int getXpFromBottle(@Nonnull ItemStack stack) {
    return 8;
  }

}
