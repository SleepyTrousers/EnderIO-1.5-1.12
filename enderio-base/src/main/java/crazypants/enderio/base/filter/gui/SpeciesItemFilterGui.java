package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.SpeciesItemFilter;
import crazypants.enderio.base.filter.filters.SpeciesMode;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class SpeciesItemFilterGui extends AbstractGuiItemFilter {

  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();
  private static final int ID_SPECIES_MODE = FilterGuiUtil.nextButtonId();
  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private final IconButton whiteListB;
  private final CycleButton<SpeciesMode.IconHolder> speciesModeB;
  private final ToggleButton stickyB;

  private final SpeciesItemFilter filter;

  private int buttonIdOffset;
  private int xOffset;
  private int yOffset;

  public SpeciesItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    this(playerInv, filterContainer, te, 13, 34, 0);
  }

  public SpeciesItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, int xOffset, int yOffset,
      int buttonIdOffset) {
    super(playerInv, filterContainer, te, "advanced_item_filter");
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.buttonIdOffset = buttonIdOffset;

    filter = (SpeciesItemFilter) filterContainer.getItemFilter();

    int butLeft = xOffset + 98;
    int x = butLeft;
    int y = yOffset + 1;
    whiteListB = new IconButton(this, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled"));
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    speciesModeB = new CycleButton<SpeciesMode.IconHolder>(this, ID_SPECIES_MODE + buttonIdOffset, x, y, SpeciesMode.IconHolder.class);
  }

  @Override
  public void initGui() {
    createFilterSlots();
    super.initGui();
  }

  public void createFilterSlots() {
    filter.createGhostSlots(getGhostSlotHandler().getGhostSlots(), xOffset + 1, yOffset + 1, new Runnable() {
      @Override
      public void run() {
        sendFilterChange();
      }
    });
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    if (isStickyModeAvailable) {
      stickyB.onGuiInit();
      stickyB.setSelected(filter.isSticky());
    }

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
    }

    speciesModeB.onGuiInit();
    speciesModeB.setMode(SpeciesMode.IconHolder.getFromMode(filter.getSpeciesMode()));
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);

    if (guiButton.id == ID_STICKY + buttonIdOffset) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_SPECIES_MODE + buttonIdOffset) {
      filter.setSpeciesMode(speciesModeB.getMode().getMode());
      sendFilterChange();
    } else if (guiButton.id == ID_WHITELIST + buttonIdOffset) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  private void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

}
