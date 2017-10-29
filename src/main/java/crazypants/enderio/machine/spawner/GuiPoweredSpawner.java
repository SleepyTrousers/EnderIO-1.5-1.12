package crazypants.enderio.machine.spawner;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.GuiPacket;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPoweredSpawner extends GuiPoweredMachineBase<TilePoweredSpawner> {

  private final MultiIconButton modeB;
  private final Rectangle progressTooltipRect;
  private boolean wasSpawnMode;
  private String header;
  private ToggleButton showRangeB;

  public GuiPoweredSpawner(InventoryPlayer par1InventoryPlayer, TilePoweredSpawner te) {
    super(te, new ContainerPoweredSpawner(par1InventoryPlayer, te), "poweredSpawner");

    modeB = MultiIconButton.createRightArrowButton(this, 8888, 115, 10);
    modeB.setSize(10, 16);

    addProgressTooltip(80, 34, 14, 14);
    progressTooltipRect = progressTooltips.get(0).getBounds();

    updateSpawnMode(te.isSpawnMode());

    int x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, -1, x, 44, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public List<String> getToolTipText() {
        return Lists.newArrayList(EnderIO.lang.localize(showRangeB.isSelected() ? "gui.spawnGurad.hideRange" : "gui.spawnGurad.showRange"));
      }
    });

  }

  @Override
  public void initGui() {
    super.initGui();
    modeB.onGuiInit();    
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) throws IOException {
    if(par1GuiButton == modeB) {
      getTileEntity().setSpawnMode(!getTileEntity().isSpawnMode());
      GuiPacket.send(this, 0, getTileEntity().isSpawnMode());
    } else if (par1GuiButton == showRangeB) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    } else {
      super.actionPerformed(par1GuiButton);
    }
  }

  private void updateSpawnMode(boolean spawnMode) {
    wasSpawnMode = spawnMode;
    ((ContainerPoweredSpawner) inventorySlots).setSlotVisibility(!spawnMode);

    if(spawnMode) {
      getGhostSlotHandler().getGhostSlots().clear();
      header = EnderIO.lang.localize("gui.machine.poweredspawner.spawn");
      progressTooltipRect.x = 80;
      progressTooltipRect.y = 34;
      progressTooltipRect.width = 14;
      progressTooltipRect.height = 14;
    } else {
      ((ContainerPoweredSpawner) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
      header = EnderIO.lang.localize("gui.machine.poweredspawner.capture");
      progressTooltipRect.x = 52;
      progressTooltipRect.y = 40;
      progressTooltipRect.width = 72;
      progressTooltipRect.height = 21;
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    
    GlStateManager.color(1, 1, 1);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    TilePoweredSpawner spawner = getTileEntity();
    boolean spawnMode = spawner.isSpawnMode();

    if(spawnMode != wasSpawnMode) {
      updateSpawnMode(spawnMode);
    }

    FontRenderer fr = getFontRenderer();
    int x = sx + xSize / 2 - fr.getStringWidth(header) / 2;
    int y = sy + fr.FONT_HEIGHT + 6;
    fr.drawStringWithShadow(header, x, y, ColorUtil.getRGB(Color.WHITE));

    GlStateManager.color(1, 1, 1);
    bindGuiTexture();
    if(spawnMode) {
      drawTexturedModalRect(sx + 80, sy + 34, 207, 0, 17, 15);
      if(shouldRenderProgress()) {
        int scaled = getProgressScaled(14) + 1;
        drawTexturedModalRect(sx + 81, sy + 34 + 14 - scaled, 176, 14 - scaled, 14, scaled);
      }
    } else {
      drawTexturedModalRect(sx + 52, sy + 40, 52, 170, 72, 21);
      if(shouldRenderProgress()) {
        int scaled = getProgressScaled(24);
        drawTexturedModalRect(sx + 76, sy + 43, 176, 14, scaled + 1, 16);
      }
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
