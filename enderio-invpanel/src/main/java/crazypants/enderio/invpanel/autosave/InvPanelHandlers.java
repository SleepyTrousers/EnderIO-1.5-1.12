package crazypants.enderio.invpanel.autosave;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.invpanel.EnderIOInvPanel;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOInvPanel.MODID)
public class InvPanelHandlers extends BaseHandlers {
  
  @SubscribeEvent
  public static void register(EnderIOLifecycleEvent.PreInit event) {
    REGISTRY.register(new HandleStoredCraftingRecipe());
  }
}

