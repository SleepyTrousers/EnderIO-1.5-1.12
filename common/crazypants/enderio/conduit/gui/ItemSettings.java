package crazypants.enderio.conduit.gui;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;

public class ItemSettings extends BaseSettingsPanel {

  protected ItemSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ModObject.itemItemConduit.name, gui, con);
  }

}
