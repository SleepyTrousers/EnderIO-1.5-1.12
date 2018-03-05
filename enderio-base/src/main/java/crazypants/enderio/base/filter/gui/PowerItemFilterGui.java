package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.PowerItemFilter;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class PowerItemFilterGui extends AbstractGuiItemFilter {

  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private static final int ID_MORE = FilterGuiUtil.nextButtonId();
  private static final int ID_LEVEL = FilterGuiUtil.nextButtonId();

  private final @Nonnull ToggleButton stickyB;

  private final @Nonnull GuiButton modeB;
  private final @Nonnull GuiButton levelB;

  private final @Nonnull PowerItemFilter filter;

  public PowerItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(playerInv, filterContainer, te);

    filter = (PowerItemFilter) filterContainer.getItemFilter();

    int butLeft = 13;
    int x = butLeft;
    int y = 34;

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    String[] lines = EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled");
    stickyB.setSelectedToolTip(lines);
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    x += 28;
    int x1 = x + 116;
    int y1 = y + 18;
    modeB = new GuiButton(ID_MORE, x1, y1, 40, 20, "");

    x1 += 46;
    levelB = new GuiButton(ID_LEVEL, x1, y1, 40, 20, "");
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

    addButton(modeB);
    addButton(levelB);
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
}
