package crazypants.enderio.machine.monitor.v2;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;

public class GuiPMon extends GuiPoweredMachineBase<TilePMon> {

  private static enum Tab {
    GRAPH(0, new ItemStack(BlockPMon.blockPMon)),
    STAT(1, new ItemStack(EnderIO.blockPowerMonitor)),
    CONTROL(2, new ItemStack(Items.redstone));

    int tabNo;
    ItemStack itemStack;
    InvisibleButton button;

    private Tab(int tabNo, ItemStack itemStack) {
      this.tabNo = tabNo;
      this.itemStack = itemStack;
    }

  }

  protected Tab tab = Tab.GRAPH;

  protected int timebase = 2;
  protected int timebaseOffset = 0;
  protected InvisibleButton plus;
  protected InvisibleButton minus;

  public GuiPMon(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TilePMon te) {
    super(te, new ContainerPMon(par1InventoryPlayer, te), "pmon");

    plus = new InvisibleButton(this, 1, 154, 28);
    plus.setToolTip("+");
    minus = new InvisibleButton(this, 2, 154, 52);
    minus.setToolTip("-");

    for (Tab drawTab : Tab.values()) {
      drawTab.button = new InvisibleButton(this, 3, 0, 0);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.visible = false;
    configB.visible = false;
    plus.onGuiInit();
    minus.onGuiInit();

    for (Tab drawTab : Tab.values()) {
      drawTab.button.onGuiInit();
    }
  }

  protected void updateVisibility() {
    switch (tab) {
    case GRAPH:
      plus.enabled = timebase < 6;
      minus.enabled = timebase > 0;
      break;
    case STAT:
      plus.enabled = minus.enabled = false;
      break;
    case CONTROL:
      plus.enabled = minus.enabled = false;
      break;
    }
    for (Tab drawTab : Tab.values()) {
      drawTab.button.enabled = drawTab != tab;
    }
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    if (btn == plus) {
      if (timebase >= 6) {
        return;
      }
      timebase++;
      timebaseOffset -= 16;
    } else if (btn == minus) {
      if (timebase <= 0) {
        return;
      }
      timebase--;
      timebaseOffset += 16;
    } else {
      for (Tab drawTab : Tab.values()) {
        if (btn == drawTab.button) {
          tab = drawTab;
          return;
        }
      }
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected int getPowerX() {
    return 8;
  }

  @Override
  protected int getPowerY() {
    return 10;
  }

  @Override
  protected int getPowerWidth() {
    return 4;
  }

  @Override
  protected int getPowerHeight() {
    return 66;
  }

  private long lastTick = 0;

  private void drawTimebase(int x, int y) {
    int u = 200, v = timebase * 16 + timebaseOffset, w = 18, h = 16;
    if (v < 0) {
      v = 0;
    } else if (v > 6 * 16) {
      v = 6 * 16;
    }
    drawTexturedModalRect(x, y, u, v, w, h);
    if (lastTick != EnderIO.proxy.getTickCount()) {
      lastTick = EnderIO.proxy.getTickCount();
      if (timebaseOffset < 0) {
        timebaseOffset += 1 - timebaseOffset / 8;
      } else if (timebaseOffset > 0) {
        timebaseOffset -= 1 + timebaseOffset / 8;
      }
    }
  }

  private void drawGraph(int x, int y) {
    StatCollector stat = getTileEntity().getStatCollector(timebase);
    int[][] values = stat.getValues();
    for (int i = 0; i < stat.MAX_VALUES; i++) {
      int min = values[0][i], max = values[1][i];
      drawTexturedModalRect(x + i, y + 63 - max, 220, 63 - max, 1, max - min + 1);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    updateVisibility();

    switch (tab) {
    case GRAPH:
      bindGuiTexture(0);
      drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
      drawTimebase(sx + 149, sy + 35);
      drawGraph(sx + 48, sy + 11);
      break;
    case STAT:
      bindGuiTexture(0);
      break;
    case CONTROL:
      bindGuiTexture(0);
      break;
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    for (Tab drawTab : Tab.values()) {
      drawTab(sx, sy, drawTab, drawTab == tab);
    }
  }

  // offset from the upper right corner of gui background
  private final static int tabXOffset = -3;
  private final static int tabYOffset = 4;
  private static final int TAB_WIDTH = 4 + 16 + 4;
  private static final int TAB_HEIGHT = 24;

  private void drawTab(int sx, int sy, Tab drawTab, boolean active) {
    int tabX = sx + xSize + tabXOffset;
    int tabY = sy + tabYOffset + TAB_HEIGHT * drawTab.tabNo;

    // (1) Tab
    if (active) {
      IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, tabY, true);
    } else {
      IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, tabY, true);
    }
    IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX + 5, tabY, true);

    // (2) Icon
    RenderHelper.enableGUIStandardItemLighting();
    itemRender.renderItemIntoGUI(drawTab.itemStack, tabX + 4, tabY + 4);
    RenderHelper.disableStandardItemLighting();

    // (3) Button
    drawTab.button.xPosition = tabX + 4;
    drawTab.button.yPosition = tabY + 4;
    drawTab.button.width = 16;
    drawTab.button.height = 16;
  }

  @Override
  public List<Rectangle> getBlockingAreas() {
    return Collections.singletonList(new Rectangle((width + xSize) / 2 + tabXOffset, (height - ySize) / 2 + tabYOffset, TAB_WIDTH + 1, TAB_HEIGHT
        * Tab.values().length));
  }

}
