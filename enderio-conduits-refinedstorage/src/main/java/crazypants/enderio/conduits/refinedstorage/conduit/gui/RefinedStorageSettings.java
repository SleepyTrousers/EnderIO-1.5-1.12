package crazypants.enderio.conduits.refinedstorage.conduit.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.conduits.refinedstorage.conduit.IRefinedStorageConduit;
import crazypants.enderio.conduits.refinedstorage.init.ConduitRefinedStorageObject;

public class RefinedStorageSettings extends BaseSettingsPanel {

  private IRefinedStorageConduit rsCon;

  public RefinedStorageSettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_RS, ConduitRefinedStorageObject.item_refined_storage_conduit.getUnlocalisedName(), gui, con, "filter_upgrade_settings");

    rsCon = (IRefinedStorageConduit) con;
  }

  @Override
  protected boolean hasInOutModes() {
    return false;
  }

  @Override
  protected boolean hasUpgrades() {
    return true;
  }

  @Override
  protected boolean hasFilters() {
    return true;
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, true, rsCon);

    filtersChanged();
  }
}
