package crazypants.enderio.machines.machine.vacuum;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.google.common.collect.Lists;

import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.base.machine.gui.GuiMachineBase.BUTTON_SIZE;

public class GuiVacuumChest extends GuiContainerBaseEIO {

  private static final int RANGE_LEFT = 145;
  private static final int RANGE_TOP = 86;
  private static final int RANGE_WIDTH = 16;

  private static final int FILTER_LEFT = 29;
  private static final int FILTER_TOP = 85;

  private static final int ID_RANGE_UP = 4711;
  private static final int ID_RANGE_DOWN = 4712;
  private static final int ID_WHITELIST = 4713;
  private static final int ID_MATCHMETA = 4714;
  private static final int ID_REDSTONE = 4715;

  private final @Nonnull TileVacuumChest te;

  private final @Nonnull GuiToolTip rangeTooltip;
  private final @Nonnull MultiIconButton rangeUpB;
  private final @Nonnull MultiIconButton rangeDownB;
  private final @Nonnull ToggleButton whiteListB;
  private final @Nonnull ToggleButton useMetaB;
  private final @Nonnull RedstoneModeButton<TileVacuumChest> rsB;
  private final @Nonnull String headerChest;
  private final @Nonnull String headerFilter;
  private final @Nonnull String headerRange;
  private final @Nonnull String headerInventory;
  private final @Nonnull ToggleButton showRangeB;

  public GuiVacuumChest(@Nonnull InventoryPlayer inventory, @Nonnull TileVacuumChest te) {
    super(new ContainerVacuumChest(inventory, te), "vacuum_chest");
    this.te = te;

    ySize = 206;

    int x = RANGE_LEFT;
    int y = RANGE_TOP;

    rangeTooltip = new GuiToolTip(new Rectangle(x, y, RANGE_WIDTH, 16), Lang.GUI_VACUUM_RANGE_TOOLTIP.get());

    x += RANGE_WIDTH;
    rangeUpB = MultiIconButton.createAddButton(this, ID_RANGE_UP, x, y);

    y += 8;
    rangeDownB = MultiIconButton.createMinusButton(this, ID_RANGE_DOWN, x, y);

    x = xSize - 16 - 7;
    y = 104;
    rsB = new RedstoneModeButton<TileVacuumChest>(this, ID_REDSTONE, x, y, te);

    x -= BUTTON_SIZE + 2;
    showRangeB = new ToggleButton(this, -1, x, y, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
      }
    });

    x = FILTER_LEFT + TileVacuumChest.FILTER_SLOTS * 18 - BUTTON_SIZE - 1;
    whiteListB = new ToggleButton(this, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST, IconEIO.FILTER_BLACKLIST);
    // whiteListB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist")); // TODO 1.11 see filter revamp
    // whiteListB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist")); // TODO 1.11 see filter revamp
    whiteListB.setPaintSelectedBorder(false);

    x -= BUTTON_SIZE + 2;
    useMetaB = new ToggleButton(this, ID_MATCHMETA, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    // useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData")); // TODO 1.11 see filter revamp
    // useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData")); // TODO 1.11 see filter revamp
    useMetaB.setPaintSelectedBorder(false);

    for (int i = 0; i < TileVacuumChest.FILTER_SLOTS; i++) {
      getGhostSlotHandler().getGhostSlots().add(new FilterGhostSlot(i, FILTER_LEFT + i * 18 + 1, FILTER_TOP + 1));
    }

    headerChest = Lang.GUI_VACUUM_CHEST.get();
    headerFilter = Lang.GUI_VACUUM_FILTER.get();
    headerRange = Lang.GUI_VACUUM_RANGE.get();
    headerInventory = Lang.GUI_VACUUM_INVENTORY.get();

    ((ContainerVacuumChest) inventorySlots).setFilterChangedCB(new Runnable() {
      @Override
      public void run() {
        filterChanged();
      }
    });
  }

  @Override
  public void initGui() {
    super.initGui();

    rangeUpB.onGuiInit();
    rangeDownB.onGuiInit();
    rsB.onGuiInit();
    addToolTip(rangeTooltip);
    showRangeB.onGuiInit();
    showRangeB.setSelected(te.isShowingRange());

    filterChanged();
    ((ContainerVacuumChest) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    if (guiButton == showRangeB) {
      te.setShowRange(showRangeB.isSelected());
      return;
    }
    ItemFilter itemFilter;
    switch (guiButton.id) {
    case ID_RANGE_UP:
      setRange((int) (te.getRange() + 1));
      break;
    case ID_RANGE_DOWN:
      setRange((int) (te.getRange() - 1));
      break;
    case ID_WHITELIST:
      itemFilter = te.getItemFilter();
      if (itemFilter != null) {
        PacketHandler.INSTANCE.sendToServer(PacketVaccumChest.setFilterBlacklist(te, !itemFilter.isBlacklist()));
        updateButtons();
      }
      break;
    case ID_MATCHMETA:
      itemFilter = te.getItemFilter();
      if (itemFilter != null) {
        PacketHandler.INSTANCE.sendToServer(PacketVaccumChest.setFilterMatchMeta(te, !itemFilter.isMatchMeta()));
        updateButtons();
      }
      break;

    }
  }

  private void setRange(int range) {
    PacketHandler.INSTANCE.sendToServer(PacketVaccumChest.setRange(te, range));
  }

  void filterChanged() {
    if (te.hasItemFilter()) {
      whiteListB.onGuiInit();
      useMetaB.onGuiInit();
      updateButtons();
    } else {
      whiteListB.detach();
      useMetaB.detach();
    }
  }

  private void updateButtons() {
    ItemFilter itemFilter = te.getItemFilter();
    whiteListB.setSelected(itemFilter.isBlacklist());
    useMetaB.setSelected(itemFilter.isMatchMeta());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    if (te.hasItemFilter()) {
      drawTexturedModalRect(sx + FILTER_LEFT, sy + FILTER_TOP, 0, 238, TileVacuumChest.FILTER_SLOTS * 18, 18);
    }

    int headerColor = 0x404040;
    FontRenderer fr = getFontRenderer();
    fr.drawString(headerChest, sx + 7, sy + 6, headerColor);
    fr.drawString(headerFilter, sx + 7, sy + 74, headerColor);
    fr.drawString(headerRange, sx + xSize - 7 - fr.getStringWidth(headerRange), sy + 74, headerColor);
    fr.drawString(headerInventory, sx + 7, sy + 111, headerColor);

    IconEIO.map.render(EnderWidget.BUTTON_DOWN, sx + RANGE_LEFT, sy + RANGE_TOP, RANGE_WIDTH, 16, 0, true);
    String str = Integer.toString((int) te.getRange());
    int sw = fr.getStringWidth(str);
    fr.drawString(str, sx + RANGE_LEFT + RANGE_WIDTH - sw - 5, sy + RANGE_TOP + 5, ColorUtil.getRGB(Color.black));

    super.drawGuiContainerBackgroundLayer(par1, mouseX, mouseY);
  }

  class FilterGhostSlot extends GhostSlot {

    FilterGhostSlot(int slot, int x, int y) {
      this.setSlot(slot);
      this.setX(x);
      this.setY(y);
    }

    @Override
    public boolean isVisible() {
      return GuiVacuumChest.this.te.hasItemFilter();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      // TODO: super.putStack() can do this already...
      PacketHandler.INSTANCE.sendToServer(PacketVaccumChest.setFilterSlot(GuiVacuumChest.this.te, getSlot(), stack));
    }

    @Override
    public @Nonnull ItemStack getStack() {
      ItemFilter itemFilter = GuiVacuumChest.this.te.getItemFilter();
      if (itemFilter != null) {
        return itemFilter.getStackInSlot(getSlot());
      }
      return Prep.getEmpty();
    }
  }
}
