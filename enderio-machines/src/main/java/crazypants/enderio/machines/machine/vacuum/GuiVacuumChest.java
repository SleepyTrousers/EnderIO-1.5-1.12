package crazypants.enderio.machines.machine.vacuum;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.google.common.collect.Lists;

import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import static crazypants.enderio.base.machine.gui.GuiMachineBase.BUTTON_SIZE;

public class GuiVacuumChest extends GuiContainerBaseEIO implements IOpenFilterRemoteExec.GUI {

  private static final int RANGE_LEFT = 145;
  private static final int RANGE_TOP = 86;
  private static final int RANGE_WIDTH = 16;

  private static final int FILTER_LEFT = 29;
  private static final int FILTER_TOP = 85;

  private static final int ID_RANGE_UP = 4711;
  private static final int ID_RANGE_DOWN = 4712;
  private static final int ID_REDSTONE = 4715;
  private static final int ID_OPEN_FILTER = 4713;

  private final @Nonnull TileVacuumChest te;

  private final @Nonnull GuiToolTip rangeTooltip;
  private final @Nonnull MultiIconButton rangeUpB;
  private final @Nonnull MultiIconButton rangeDownB;
  private final @Nonnull RedstoneModeButton<TileVacuumChest> rsB;
  private final @Nonnull String headerChest;
  private final @Nonnull String headerFilter;
  private final @Nonnull String headerRange;
  private final @Nonnull String headerInventory;
  private final @Nonnull ToggleButton showRangeB;
  private final @Nonnull IconButton openFilterGuiB;

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

    openFilterGuiB = new IconButton(this, ID_OPEN_FILTER, FILTER_LEFT, FILTER_TOP, IconEIO.GEAR_LIGHT);
    openFilterGuiB.setToolTip(crazypants.enderio.base.lang.Lang.GUI_EDIT_ITEM_FILTER.get());

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

    openFilterGuiB.onGuiInit();
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
    switch (guiButton.id) {
    case ID_RANGE_UP:
      setRange((int) (te.getRange() + 1));
      break;
    case ID_RANGE_DOWN:
      setRange((int) (te.getRange() - 1));
      break;
    case ID_OPEN_FILTER:
      doOpenFilterGui(FilterGuiUtil.INDEX_NONE);
      break;
    }
  }

  private void setRange(int range) {
    PacketHandler.INSTANCE.sendToServer(PacketVaccumChest.setRange(te, range));
  }

  void filterChanged() {
    if (te.hasItemFilter()) {
      openFilterGuiB.setIsVisible(true);
    } else {
      openFilterGuiB.setIsVisible(false);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

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
}
