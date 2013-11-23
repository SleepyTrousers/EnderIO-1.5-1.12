package crazypants.enderio.conduit.gui;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;

public class RedstoneSettings extends BaseSettingsPanel {

  protected RedstoneSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_REDSTONE, ModObject.itemRedstoneConduit.name, gui, con);
  }

}
