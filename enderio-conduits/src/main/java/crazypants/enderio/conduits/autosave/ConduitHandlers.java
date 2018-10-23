package crazypants.enderio.conduits.autosave;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.conduits.EnderIOConduits;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public class ConduitHandlers extends BaseHandlers {

  @SubscribeEvent
  public static void register(EnderIOLifecycleEvent.PreInit event) {
    // Conduits
    REGISTRY.register(new HandleIConduit());

    // Powertools
    REGISTRY.register(new HandleStatCollector());
  }
}
