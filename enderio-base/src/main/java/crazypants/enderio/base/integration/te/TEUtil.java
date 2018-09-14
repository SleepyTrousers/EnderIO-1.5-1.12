package crazypants.enderio.base.integration.te;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TEUtil {

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    if (Loader.isModLoaded("cofhcore")) {
      // Add support for TE wrench
      try {
        Class.forName("crazypants.enderio.base.integration.te.TEToolProvider").newInstance();
      } catch (Exception e) {
        Log.warn("Could not find Thermal Expansion Wrench definition. Wrench integration with it may fail");
      }
    }
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes("thermalfoundation", "tool.hoe_invar", "tool.hoe_copper", "tool.hoe_bronze", "tool.hoe_silver", "tool.hoe_electrum",
        "tool.hoe_tin", "tool.hoe_lead", "tool.hoe_nickel", "tool.hoe_platinum", "tool.hoe_aluminum", "tool.hoe_steel", "tool.hoe_constantan");
  }

}
