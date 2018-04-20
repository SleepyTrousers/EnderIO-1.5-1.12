package crazypants.enderio.integration.forestry.filters;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.base.filter.gui.AbstractFilterGui;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class SpeciesItemFilterGui extends AbstractFilterGui {

  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();
  private static final int ID_SPECIES_MODE = FilterGuiUtil.nextButtonId();
  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();

  private final IconButton whiteListB;
  private final CycleButton<SpeciesMode.IconHolder> speciesModeB;
  private final ToggleButton stickyB;

  private final @Nonnull SpeciesItemFilter filter;

  private int buttonIdOffset;
  private int xOffset;
  private int yOffset;

  public SpeciesItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, @Nonnull IItemFilter filter) {
    this(playerInv, filterContainer, te, 13, 34, 0, filter);
  }

  public SpeciesItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te, int xOffset, int yOffset,
      int buttonIdOffset, @Nonnull IItemFilter filterIn) {
    super(playerInv, filterContainer, te, filterIn, "advanced_item_filter");
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.buttonIdOffset = buttonIdOffset;

    filter = (SpeciesItemFilter) filterIn;

    int butLeft = xOffset + 98;
    int x = butLeft;
    int y = yOffset + 1;
    whiteListB = new IconButton(this, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_ENABLED.get(), Lang.GUI_ITEM_FILTER_STICKY_ENABLED_2.get());
    stickyB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_DISABLED.get());
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
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
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

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    return Lang.GUI_SPECIES_ITEM_FILTER.get();
  }

}
