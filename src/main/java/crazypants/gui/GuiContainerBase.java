package crazypants.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.gui.IGuiOverlay;
import crazypants.enderio.gui.TextFieldEIO;
import crazypants.gui.ToolTipManager.ToolTipRenderer;
import crazypants.render.RenderUtil;

@Optional.InterfaceList({
    @Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
})
public abstract class GuiContainerBase extends GuiContainer implements ToolTipRenderer, IGuiScreen, INEIGuiHandler {

  protected ToolTipManager ttMan = new ToolTipManager();
  protected List<IGuiOverlay> overlays = new ArrayList<IGuiOverlay>();
  protected List<TextFieldEIO> textFields = Lists.newArrayList();
  protected List<GhostSlot> ghostSlots = new ArrayList<GhostSlot>();

  protected GhostSlot hoverGhostSlot;

  protected GuiContainerBase(Container par1Container) {
    super(par1Container);
  }

  @Override
  public void initGui() {
    super.initGui();
    for (IGuiOverlay overlay : overlays) {
      overlay.init(this);
    }
    for (TextFieldEIO f : textFields) {
      f.init(this);
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    GuiTextField focused = null;
    for (GuiTextField f : textFields) {
      if (f.isFocused()) {
        focused = f;
      }
    }
    if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
      if(hideOverlays()) {
        return;
      }
      if(focused == null) {
        this.mc.thePlayer.closeScreen();
      } else {
        focused.setFocused(false);
      }
    }

    if (par1 == '\t') {
      for (int i = 0; i < textFields.size(); i++) {
        TextFieldEIO f = textFields.get(i);
        if (f.isFocused()) {
          textFields.get((i + 1) % textFields.size()).setFocused(true);
          f.setFocused(false);
          return;
        }
      }
    }
    if(focused != null) {
      focused.textboxKeyTyped(par1, par2);
    }
  }

  public boolean hideOverlays() {
    for (IGuiOverlay overlay : overlays) {
      if(overlay.isVisible()) {
        overlay.setVisible(false);
        return true;
      }
    }
    return false;
  }

  @Override
  public void addToolTip(GuiToolTip toolTip) {
    ttMan.addToolTip(toolTip);
  }
  
  @Override
  public void updateScreen() {
    super.updateScreen();
    
    for (GuiTextField f : textFields) {
      f.updateCursorCounter();
    }
  }

  @Override
  public void handleMouseInput() {
    int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    int b = Mouse.getEventButton();
    for (IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && overlay.handleMouseInput(x, y, b)) {
        return;
      }
    }
    super.handleMouseInput();
  }

  @Override
  protected boolean func_146978_c(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_) {
    int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    for (IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && overlay.isMouseInBounds(x, y)) {
        return false;
      }
    }
    return super.func_146978_c(p_146978_1_, p_146978_2_, p_146978_3_, p_146978_4_, p_146978_5_, p_146978_6_);
  }

  @Override
  public List<GhostSlot> getGhostSlots() {
    return ghostSlots;
  }

  @Override
  protected void mouseClicked(int x, int y, int p_73864_3_) {
    for (GuiTextField f : textFields) {
      f.mouseClicked(x, y, p_73864_3_);
    }
    if(!ghostSlots.isEmpty()) {
      GhostSlot slot = getGhostSlot(x, y);
      if(slot != null) {
        ItemStack st = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
        slot.putStack(st);
        return;
      }
    }
    super.mouseClicked(x, y, p_73864_3_);
  }

  public void addOverlay(IGuiOverlay overlay) {
    overlays.add(overlay);
  }

  public void removeOverlay(IGuiOverlay overlay) {
    overlays.remove(overlay);
  }

  private int realMx, realMy;

  @Override
  protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    drawForegroundImpl(mouseX, mouseY);

    Timer t = RenderUtil.getTimer();
    
    if(t != null) {
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      for (IGuiOverlay overlay : overlays) {
        if(overlay != null && overlay.isVisible()) {
          overlay.draw(realMx, realMy, t.renderPartialTicks);
        }
      }
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glPopMatrix();
    }

    ttMan.drawTooltips(this, realMx, realMy);
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
    for (GuiTextField f : textFields) {
      f.drawTextBox();
    }
    drawGhostSlots(mouseX, mouseY);
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {
    hoverGhostSlot = null;
    int mx = realMx = par1;
    int my = realMy = par2;
    for (IGuiOverlay overlay : overlays) {
      if(overlay != null && overlay.isVisible() && isMouseInOverlay(par1, par2, overlay)) {
        mx = -5000;
        my = -5000;
        this.drawItemStack(this.mc.thePlayer.inventory.getItemStack(), par1 - this.guiLeft - 8, par2 - this.guiTop - 8, null);
      }
    }

    super.drawScreen(mx, my, par3);

    if(hoverGhostSlot != null && mc.thePlayer.inventory.getItemStack() == null) {
      ItemStack stack = hoverGhostSlot.getStack();
      if(stack != null) {
        renderToolTip(stack, par1, par2);
      }
    }
  }

  // copied from super with hate
  private void drawItemStack(ItemStack stack, int mouseX, int mouseY, String str)
  {
    GL11.glTranslatef(0.0F, 0.0F, 32.0F);
    this.zLevel = 200.0F;
    itemRender.zLevel = 200.0F;
    FontRenderer font = null;
    if(stack != null) {
      font = stack.getItem().getFontRenderer(stack);
    }
    if(font == null) {
      font = fontRendererObj;
    }
    itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), stack, mouseX, mouseY);
    itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), stack, mouseX, mouseY, str);
    this.zLevel = 0.0F;
    itemRender.zLevel = 0.0F;
  }

  protected void drawFakeItemsStart() {
    zLevel = 100.0F;
    itemRender.zLevel = 100.0F;
    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    RenderHelper.enableGUIStandardItemLighting();
  }

  protected void drawFakeItemStack(int x, int y, ItemStack stack) {
    itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);
  }

  protected void drawFakeItemHover(int x, int y) {
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glColorMask(true, true, true, false);
    drawGradientRect(x, y, x + 16, y + 16, 0x80FFFFFF, 0x80FFFFFF);
    GL11.glColorMask(true, true, true, true);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  protected void drawFakeItemsEnd() {
    GL11.glPopAttrib();
    itemRender.zLevel = 0.0F;
    zLevel = 0.0F;
  }

  protected void drawGhostSlots(int mouseX, int mouseY) {
    if(ghostSlots.isEmpty()) {
      return;
    }
    int sx = getGuiLeft();
    int sy = getGuiTop();
    drawFakeItemsStart();
    try {
      hoverGhostSlot = null;
      for(GhostSlot slot : ghostSlots) {
        ItemStack stack = slot.getStack();
        if(slot.isVisible()) {
          if(stack != null) {
            drawFakeItemStack(slot.x + sx, slot.y + sy, stack);
          }
          if(slot.isMouseOver(mouseX - sx, mouseY - sy)) {
            hoverGhostSlot = slot;
          }
        }
      }
      if(hoverGhostSlot != null) {
        // draw hover last to prevent it from affecting rendering of other slots ...
        drawFakeItemHover(hoverGhostSlot.x + sx, hoverGhostSlot.y + sy);
      }
    } finally {
      drawFakeItemsEnd();
    }
  }

  protected GhostSlot getGhostSlot(int mouseX, int mouseY) {
    mouseX -= getGuiLeft();
    mouseY -= getGuiTop();
    for(GhostSlot slot : ghostSlots) {
      if(slot.isVisible() && slot.isMouseOver(mouseX, mouseY)) {
        return slot;
      }
    }
    return null;
  }

  private boolean isMouseInOverlay(int mouseX, int mouseY, IGuiOverlay overlay) {
    int x = mouseX - getGuiLeft();
    int y = mouseY - getGuiTop();
    return overlay.getBounds().contains(x, y);
  }

  @Override
  public void removeToolTip(GuiToolTip toolTip) {
    ttMan.removeToolTip(toolTip);
  }

  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    copyOfdrawHoveringText(par1List, par2, par3, font);
    GL11.glPopAttrib();
    GL11.glPopAttrib();
  }

  //This is a copy of the super class method due to 'Method not found' errors
  // reported with some mods installed.
  protected void copyOfdrawHoveringText(List<String> par1List, int par2, int par3, FontRenderer font) {
    if(!par1List.isEmpty())
    {
      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      int k = 0;
      Iterator<String> iterator = par1List.iterator();

      while (iterator.hasNext())
      {
        String s = (String) iterator.next();
        int l = font.getStringWidth(s);

        if(l > k)
        {
          k = l;
        }
      }

      int i1 = par2 + 12;
      int j1 = par3 - 12;
      int k1 = 8;

      if(par1List.size() > 1)
      {
        k1 += 2 + (par1List.size() - 1) * 10;
      }

      if(i1 + k > this.width)
      {
        i1 -= 28 + k;
      }

      if(j1 + k1 + 6 > this.height)
      {
        j1 = this.height - k1 - 6;
      }

      this.zLevel = 300.0F;
      //itemRenderer.zLevel = 300.0F;
      int l1 = -267386864;
      this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
      this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
      this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
      this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
      this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
      int i2 = 1347420415;
      int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
      this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
      this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
      this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
      this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

      for (int k2 = 0; k2 < par1List.size(); ++k2)
      {
        String s1 = (String) par1List.get(k2);
        font.drawStringWithShadow(s1, i1, j1, -1);

        if(k2 == 0)
        {
          j1 += 2;
        }

        j1 += 10;
      }

      this.zLevel = 0.0F;
      //itemRenderer.zLevel = 0.0F;
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      RenderHelper.enableStandardItemLighting();
      GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
  }

  @Override
  public int getGuiLeft() {
    return guiLeft;
  }

  @Override
  public int getGuiTop() {
    return guiTop;
  }

  @Override
  public int getXSize() {
    return xSize;
  }

  @Override
  public int getYSize() {
    return ySize;
  }
  
  public void setGuiLeft(int i) {
    guiLeft = i;
  }

  public void setGuiTop(int i) {
    guiTop = i;
  }

  public void setXSize(int i) {
    xSize = i;
  }

  public void setYSize(int i) {
    ySize = i;
  }

  @Override
  public FontRenderer getFontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addButton(GuiButton button) {
    if(!buttonList.contains(button)) {
      buttonList.add(button);
    }
  }

  @Override
  public void removeButton(GuiButton button) {
    buttonList.remove(button);
  }

  @Override
  public int getOverlayOffsetX() {
    return 0;
  }

  @Override
  public void doActionPerformed(GuiButton guiButton) {
    actionPerformed(guiButton);
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public VisiblityData modifyVisiblity(GuiContainer gc, VisiblityData vd) {
    return vd;
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public Iterable<Integer> getItemSpawnSlots(GuiContainer gc, ItemStack is) {
    return null;
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gc) {
    return Collections.<TaggedInventoryArea>emptyList();
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public boolean handleDragNDrop(GuiContainer gc, int i, int i1, ItemStack is, int i2) {
    return false;
  }

  @Override
  @Optional.Method(modid = "NotEnoughItems")
  public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
    return false;
  }
}
