package crazypants.enderio.conduit.gui;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;

public class PowerSettings extends BaseSettingsPanel {

  protected PowerSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_POWER, ModObject.itemPowerConduit.name, gui, con);
  }
}
