package crazypants.enderio.base.filter.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.filter.filters.DamageModeIconHolder;
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.GhostSlotTarget;
import crazypants.enderio.base.lang.Lang;
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

  final boolean isAdvanced, isLimited, isBig;

  private final ItemFilter filter;

  private int xOffset;
  private int yOffset;

  public BasicItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    this(playerInv, filterContainer, 13, 34, te);
  }

  public BasicItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, int xOffset, int yOffset, TileEntity te) {
    super(playerInv, filterContainer, te, "basic_item_filter", "advanced_item_filter", "big_item_filter");
    this.xOffset = xOffset;
    this.yOffset = yOffset;

    filter = (ItemFilter) filterContainer.getItemFilter();

    isAdvanced = filter.isAdvanced();
    isLimited = filter.isLimited();
    isBig = filter.isBig();

    int butLeft = xOffset + 98;
    int x = butLeft;
    int y = yOffset + 1;

    if (isBig) {
      y = 13;
      x += 27;
    }
    whiteListB = new IconButton(this, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());

    x += 20;
    useMetaB = new ToggleButton(this, ID_META, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_MATCH_META.get());
    useMetaB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_IGNORE_META.get());
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButton(this, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_ENABLED.get(), Lang.GUI_ITEM_FILTER_STICKY_ENABLED_2.get());
    stickyB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_STICKY_DISABLED.get());
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = butLeft;

    useOreDictB = new ToggleButton(this, ID_ORE_DICT, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_ORE_DIC_ENABLED.get());
    useOreDictB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_ORE_DIC_DISABLED.get());
    useOreDictB.setPaintSelectedBorder(false);

    x += 20;
    useNbtB = new ToggleButton(this, ID_NBT, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip(Lang.GUI_ITEM_FILTER_MATCH_NBT.get());
    useNbtB.setUnselectedToolTip(Lang.GUI_ITEM_FILTER_IGNORE_NBT.get());
    useNbtB.setPaintSelectedBorder(false);

    x += 20;
    damageB = new CycleButton<DamageModeIconHolder>(this, ID_DAMAGE, x, y, DamageModeIconHolder.class);
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
        whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
      } else {
        whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
        whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
      }
    }

  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_META) {
      filter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_NBT) {
      filter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_STICKY) {
      filter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_ORE_DICT) {
      filter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if (guiButton.id == ID_DAMAGE) {
      filter.setDamageMode(damageB.getMode().getMode());
      sendFilterChange();
    } else if (guiButton.id == ID_WHITELIST) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  public void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

  @Override
  public void bindGuiTexture() {
    super.bindGuiTexture(isBig ? 2 : (isAdvanced ? 1 : 0));
  }

  @Override
  @Nonnull
  protected String getUnlocalisedNameForHeading() {
    if (filter.isBig()) {
      return Lang.GUI_BIG_ITEM_FILTER.get();
    } else if (filter.isLimited()) {
      return Lang.GUI_LIMITED_ITEM_FILTER.get();
    } else if (filter.isAdvanced()) {
      return Lang.GUI_ADVANCED_ITEM_FILTER.get();
    } else {
      return Lang.GUI_BASIC_ITEM_FILTER.get();
    }
  }

  public @Nonnull <I> List<GhostSlotTarget<I>> getTargetSlots() {
    List<GhostSlotTarget<I>> targets = new ArrayList<>();
    for (GhostSlot slot : getGhostSlotHandler().getGhostSlots()) {
      targets.add(new GhostSlotTarget<I>(filter, slot, getGuiLeft(), getGuiTop(), this));
    }
    return targets;
  }

}
