package crazypants.enderio.conduit.refinedstorage.conduit.gui;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.conduit.refinedstorage.conduit.IRefinedStorageConduit;
import crazypants.enderio.conduit.refinedstorage.conduit.RefinedStorageConduit;
import crazypants.enderio.conduit.refinedstorage.init.ConduitRefinedStorageObject;
import crazypants.enderio.conduit.refinedstorage.lang.Lang;
import crazypants.enderio.conduits.gui.BaseSettingsPanel;
import crazypants.enderio.conduits.init.ConduitObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public class RefinedStorageSettings extends BaseSettingsPanel {

  private IRefinedStorageConduit rsCon;

  public RefinedStorageSettings(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_RS, ConduitRefinedStorageObject.item_refined_storage_conduit.getUnlocalisedName(), gui, con, "filter_upgrade_settings");

    rsCon = (IRefinedStorageConduit) con;

    filterExtractUpgradeTooltip = new GuiToolTip(new Rectangle(rightColumn, 70, 18, 18), Lang.GUI_RS_FILTER_UPGRADE_IN.get(),
        Lang.GUI_RS_FILTER_UPGRADE_IN_2.get(), Lang.GUI_RS_FILTER_UPGRADE_IN_3.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFilter(false) && super.shouldDraw();
      }
    };

    filterInsertUpgradeTooltip = new GuiToolTip(new Rectangle(leftColumn, 70, 18, 18), Lang.GUI_RS_FILTER_UPGRADE_OUT.get(),
        Lang.GUI_RS_FILTER_UPGRADE_OUT_2.get(), Lang.GUI_RS_FILTER_UPGRADE_OUT_3.get()) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFilter(true) && super.shouldDraw();
      }
    };
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
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(RefinedStorageConduit.INDEX_OUTPUT_REFINED_STROAGE);
      return;
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(RefinedStorageConduit.INDEX_INPUT_REFINED_STROAGE);
      return;
    }
  }

  @Override
  protected void initCustomOptions() {
    gui.getContainer().setInOutSlotsVisible(true, true, rsCon);

    NNList<ItemStack> filtersAll = new NNList<>(new ItemStack(ModObject.itemBasicItemFilter.getItemNN()), new ItemStack(ModObject.itemFluidFilter.getItemNN()));
    NNList<ItemStack> upgrades = new NNList<>(new ItemStack(ConduitObject.item_extract_speed_upgrade.getItemNN()),
        new ItemStack(ConduitObject.item_extract_speed_downgrade.getItemNN()), new ItemStack(ConduitRefinedStorageObject.item_rs_crafting_upgrade.getItemNN()),
        new ItemStack(ConduitRefinedStorageObject.item_rs_crafting_speed_upgrade.getItemNN()),
        new ItemStack(ConduitRefinedStorageObject.item_rs_crafting_speed_downgrade.getItemNN()));
    gui.getContainer().createGhostSlots(gui.getGhostSlotHandler().getGhostSlots(), filtersAll, filtersAll, upgrades);
  }
}
