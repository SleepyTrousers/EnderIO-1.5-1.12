package crazypants.enderio.conduits.me.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.conduits.me.init.ConduitAppliedEnergisticsObject;

public class MESettings extends BaseSettingsPanel {

  public MESettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ME, ConduitAppliedEnergisticsObject.item_me_conduit.getUnlocalisedName(), gui, con, "simple_settings");
  }

  @Override
  protected boolean hasInOutModes() {
    return false;
  }

}
