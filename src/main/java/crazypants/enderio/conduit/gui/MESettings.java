package crazypants.enderio.conduit.gui;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconEIO;

public class MESettings extends BaseSettingsPanel {

  public MESettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, EnderIO.lang.localize("itemMEConduit.name"), gui, con);
  }
  
  

}
