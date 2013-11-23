package crazypants.enderio.conduit.gui;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;

public class LiquidSettings extends BaseSettingsPanel {

  protected LiquidSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, ModObject.itemLiquidConduit.name, gui, con);
  }

}
