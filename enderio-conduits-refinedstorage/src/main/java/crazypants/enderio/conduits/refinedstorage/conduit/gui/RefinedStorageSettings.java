package crazypants.enderio.conduits.refinedstorage.conduit.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.conduits.refinedstorage.init.ConduitRefinedStorageObject;

public class RefinedStorageSettings extends BaseSettingsPanel {

  public RefinedStorageSettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconRS.WRENCH_OVERLAY_RS, ConduitRefinedStorageObject.item_refined_storage_conduit.getUnlocalisedName(), gui, con, "simple_settings");
  }

}
