package crazypants.enderio.base.filter.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.DamageModeIconHolder;
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class BasicItemFilterGui extends AbstractGuiItemFilter {

  private static final int ID_WHITELIST = FilterGuiUtil.nextButtonId();
  private static final int ID_NBT = FilterGuiUtil.nextButtonId();
  private static final int ID_META = FilterGuiUtil.nextButtonId();
  private static final int ID_ORE_DICT = FilterGuiUtil.nextButtonId();
  private static final int ID_STICKY = FilterGuiUtil.nextButtonId();
  private static final int ID_DAMAGE = FilterGuiUtil.nextButtonId();

  private final ToggleButton useMetaB;
  private final ToggleButton useNbtB;
  private final IconButton whiteListB;
  private final ToggleButton useOreDictB;
  private final ToggleButton stickyB;
  private final CycleButton<DamageModeIconHolder> damageB;

  final boolean isAdvanced, isLimited;
  final boolean isStickyModeAvailable;

  private final ContainerFilter filterContainer;
  private final ItemFilter filter;

  private int buttonIdOffset;
  private int xOffset;
  private int yOffset;

  public BasicItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, boolean isStickyModeAvailable, TileEntity te) {
    this(playerInv, filterContainer, isStickyModeAvailable, 32, 68, 0, te);
  }
  // TODO Lang

  public BasicItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, boolean isStickyModeAvailable, int xOffset,
      int yOffset, int buttonIdOffset, TileEntity te) {
    super(playerInv, filterContainer, te);
    this.isStickyModeAvailable = isStickyModeAvailable;
    this.filterContainer = filterContainer;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.buttonIdOffset = buttonIdOffset;

    filter = (ItemFilter) filterContainer.getItemFilter();

    isAdvanced = filter.isAdvanced();
    isLimited = filter.isLimited();

    int butLeft = xOffset + 92;
    int x = butLeft;
    int y = yOffset + 1;
    whiteListB = new IconButton(this, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));

    x += 20;
    useMetaB = new ToggleButton(this, ID_META + buttonIdOffset, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData"));
    useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData"));
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY + buttonIdOffset, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled"));
    stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    useOreDictB = new ToggleButton(this, ID_ORE_DICT + buttonIdOffset, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicEnabled"));
    useOreDictB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicDisabled"));
    useOreDictB.setPaintSelectedBorder(false);

    x += 20;
    useNbtB = new ToggleButton(this, ID_NBT + buttonIdOffset, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchNBT"));
    useNbtB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreNBT"));
    useNbtB.setPaintSelectedBorder(false);

    x += 20;
    damageB = new CycleButton<DamageModeIconHolder>(this, ID_DAMAGE + buttonIdOffset, x, y, DamageModeIconHolder.class);
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
  public void initGui() {
    createFilterSlots();
    super.initGui();
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    ItemFilter activeFilter = filter;

    if (isAdvanced) {
      useNbtB.onGuiInit();
      useNbtB.setSelected(activeFilter.isMatchNBT());

      useOreDictB.onGuiInit();
      useOreDictB.setSelected(activeFilter.isUseOreDict());

      if (isStickyModeAvailable) {
        stickyB.onGuiInit();
        stickyB.setSelected(activeFilter.isSticky());
      }

      damageB.onGuiInit();
      damageB.setMode(DamageModeIconHolder.getFromMode(activeFilter.getDamageMode()));
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());

    if (!isLimited) {
      whiteListB.onGuiInit();
      if (activeFilter.isBlacklist()) {
        whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
        whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
      } else {
        whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
        whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
      }
    }

  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_META + buttonIdOffset) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_NBT + buttonIdOffset) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_STICKY + buttonIdOffset) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_ORE_DICT + buttonIdOffset) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_DAMAGE + buttonIdOffset) {
      filter.setDamageMode(damageB.getMode().getMode());
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

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    drawTexturedModalRect(getGuiLeft() + xOffset, getGuiTop() + yOffset, 0, 238, 18 * 5, 18);
    if (filter.isAdvanced()) {
      drawTexturedModalRect(getGuiLeft() + xOffset, getGuiTop() + yOffset + 20, 0, 238, 18 * 5, 18);
    }
  }

}
