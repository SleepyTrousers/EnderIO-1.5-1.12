package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.button.TooltipButton;

import crazypants.enderio.base.filter.item.PowerItemFilter;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class PowerItemFilterGui extends AbstractGuiItemFilter {

  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private static final int ID_MORE = FilterGuiUtil.nextButtonId();
  private static final int ID_LEVEL = FilterGuiUtil.nextButtonId();

  private final @Nonnull ToggleButton stickyB;

  private final @Nonnull TooltipButton modeB;
  private final @Nonnull TooltipButton levelB;

  private final @Nonnull PowerItemFilter filter;

  public PowerItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(playerInv, filterContainer, te);

    filter = (PowerItemFilter) filterContainer.getItemFilter();

    int butLeft = 13;
    int x = getGuiLeft() + butLeft;
    int y = getGuiTop() + 34;

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_ENABLED.get(), Lang.GUI_ITEM_FILTER_STICKY_ENABLED_2.get());
    stickyB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_DISABLED.get());
    stickyB.setPaintSelectedBorder(false);

    x += 24;
    modeB = new TooltipButton(this, ID_MORE, x, y, 40, 20, "");
    modeB.setToolTip(Lang.GUI_POWER_ITEM_FILTER_COMPARE.get());

    x += 44;
    levelB = new TooltipButton(this, ID_LEVEL, x, y, 40, 20, "");
    levelB.setToolTip(Lang.GUI_POWER_ITEM_FILTER_PERCENT.get());
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    if (isStickyModeAvailable) {
      stickyB.onGuiInit();
      stickyB.setSelected(filter.isSticky());
    }

    switch (filter.getMode()) {
    case LESS:
      modeB.displayString = "<";
      break;
    case LESS_EQUAL:
      modeB.displayString = "<=";
      break;
    case EQUAL:
      modeB.displayString = "=";
      break;
    case MORE_EQUAL:
      modeB.displayString = ">=";
      break;
    case MORE:
      modeB.displayString = ">";
      break;
    default:
      modeB.displayString = "??";
      break;
    }

    levelB.displayString = String.format("%d%%", filter.getLevel() * 100 / PowerItemFilter.MAX_LEVEL);

    modeB.onGuiInit();
    levelB.onGuiInit();
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_MORE) {
      filter.setMode(filter.getMode().next());
      sendFilterChange();
    } else if (guiButton.id == ID_LEVEL) {
      filter.setLevel((filter.getLevel() + 1) % (PowerItemFilter.MAX_LEVEL + 1));
      sendFilterChange();
    }
  }

  private void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_POWER_ITEM_FILTER.get();
  }
}
