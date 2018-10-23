package crazypants.enderio.conduits.autosave;

import crazypants.enderio.base.autosave.BaseHandlers;

public class ConduitHandlers extends BaseHandlers {

  public static void register() {
    // Conduits
    REGISTRY.register(new HandleIConduit());

    // Powertools
    REGISTRY.register(new HandleStatCollector());
  }
}
