package crazypants.enderio.invpanel.autosave;

import crazypants.enderio.base.autosave.BaseHandlers;

public class InvPanelHandlers extends BaseHandlers {
  
  public static void register() {
    REGISTRY.register(new HandleStoredCraftingRecipe());
  }
}

