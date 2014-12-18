package crazypants.enderio.machine.buffer;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiBuffer extends GuiPoweredMachineBase<TileBuffer> {
  
  public GuiBuffer(InventoryPlayer par1InventoryPlayer, TileBuffer te) {
    super(te, new ContainerBuffer(par1InventoryPlayer, te));
    redstoneButton.setPosition(116, 24);
    configB.setPosition(116, 42);
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  
  @Override
  protected boolean renderPowerBar() {
    return getTileEntity().hasPower();
  }
  
  @Override
  public int getYSize() {
    return ySize + 12;
  }
  
  @Override
  protected int getPowerHeight() {
   return 52;
  }
  
  @Override
  protected int getPowerX() {
    return 48;
  }
  
  @Override
  protected int getPowerY() {
    return 15;
  }
  
  @Override
  protected int getPowerV() {
    return 0;
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/buffer.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    
    if (getTileEntity().hasPower()) {
      drawPowerBg(sx, sy);
    }
    
    if (getTileEntity().hasInventory()) {
      drawSlotBg(sx, sy);
    }
    
    String invName = Lang.localize(getTileEntity().getInventoryName() + ".name", false);
    getFontRenderer().drawStringWithShadow(invName, sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2), sy + 4, 0xFFFFFF);
    RenderUtil.bindTexture("enderio:textures/gui/buffer.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  public void renderSlotHighlights(IoMode mode) {
    for (int slot = 0; slot <= getTileEntity().getSizeInventory(); slot++) {
      renderSlotHighlight(slot, mode);
    }
  }
  
  protected void renderSlotHighlight(int slot, IoMode mode) {
    Slot invSlot = (Slot) inventorySlots.inventorySlots.get(slot);
    if (mode == IoMode.PULL) {
      renderSlotHighlight(slot, PULL_COLOR);
    } else if (mode == IoMode.PUSH) {
      renderSlotHighlight(slot, PUSH_COLOR);
    } else if (mode == IoMode.PUSH_PULL) {
      renderSplitHighlight(invSlot.xDisplayPosition, invSlot.yDisplayPosition, 16, 16);
    }
  }
  
  protected void renderSplitHighlight(int x, int y, int width, int height) {
    GL11.glEnable(GL11.GL_BLEND);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, width, height / 2, PULL_COLOR);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y + (height / 2), 0, width, height / 2, PUSH_COLOR);
    GL11.glDisable(GL11.GL_BLEND);
  }
  
  private void drawPowerBg(int sx, int sy) {
    drawTexturedModalRect(sx + 47, sy + 14, xSize + 10, 0, 12, 54);
  }
  
  private void drawSlotBg(int sx, int sy) {
    drawTexturedModalRect(sx + 61, sy + 14, xSize + 22, 0, 54, 54);
  }
}
