package crazypants.enderio.machine.invpanel.client;

import crazypants.enderio.machine.invpanel.GuiInventoryPanel;

public interface ICraftingHelper {

  void remove();

  void refill(GuiInventoryPanel gui, int amount);

}
