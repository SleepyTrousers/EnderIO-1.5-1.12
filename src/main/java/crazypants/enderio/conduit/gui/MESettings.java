package crazypants.enderio.conduit.gui;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.util.Lang;

public class MESettings extends BaseSettingsPanel {

  public MESettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, Lang.localize("itemMEConduit.name"), gui, con);
  }
  
  

}
