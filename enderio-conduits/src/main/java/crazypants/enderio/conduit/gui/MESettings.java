package crazypants.enderio.conduit.gui;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.gui.IconEIO;

public class MESettings extends BaseSettingsPanel {

  public MESettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, EnderIO.lang.localize("itemMEConduit.name"), gui, con);
  }
  
  

}
