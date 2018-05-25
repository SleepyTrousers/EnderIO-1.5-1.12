package crazypants.enderio.invpanel.conduit.data;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.invpanel.init.InvpanelObject;

public class DataSettings extends BaseSettingsPanel {

  public DataSettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_DATA, InvpanelObject.item_data_conduit.getUnlocalisedName(), gui, con, "simple_settings");
  }

  @Override
  protected boolean hasInOutModes() {
    return false;
  }

}
