package crazypants.enderio.machine;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IIcon;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IGuiOverlay;
import crazypants.enderio.gui.IoConfigRenderer;
import crazypants.enderio.gui.IoConfigRenderer.SelectedFace;
import crazypants.gui.IGuiScreen;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;

public class GuiOverlayIoConfig implements IGuiOverlay {

  private boolean visible = false;

  private IGuiScreen screen;

  private Rectangle bounds;
  int height = 80;

  private IoConfigRenderer renderer;

  private List<BlockCoord> coords = new ArrayList<BlockCoord>();

  public GuiOverlayIoConfig(IIoConfigurable ioConf) {
    coords.add(ioConf.getLocation());
  }

  public GuiOverlayIoConfig(Collection<BlockCoord> bc) {
    coords.addAll(bc);
  }

  @Override
  public IIcon getIcon() {
    return EnderIOTab.tabEnderIO.getIconItemStack().getIconIndex();
  }

  @Override
  public void init(IGuiScreen screen) {
    this.screen = screen;
    renderer = new IoConfigRenderer(coords) {

      @Override
      protected String getLabelForMode(IoMode mode) {
        return GuiOverlayIoConfig.this.getLabelForMode(mode);
      }

    };
    renderer.init();
    bounds = new Rectangle(5, screen.getYSize() - height -5, screen.getXSize() - 10, height);
  }

  protected String getLabelForMode(IoMode mode) {
    return mode.getLocalisedName();
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTick) {

    RenderUtil.renderQuad2D(bounds.x, bounds.y, 0, bounds.width, bounds.height, ColorUtil.getRGB(Color.black));
    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);

    int vpx = ( (screen.getGuiLeft() + bounds.x)* scaledresolution.getScaleFactor());
    int vpy = (screen.getGuiTop() + 4) * scaledresolution.getScaleFactor();
    int w = bounds.width * scaledresolution.getScaleFactor();
    int h = bounds.height * scaledresolution.getScaleFactor();

    renderer.drawScreen(mouseX, mouseY, partialTick, new Rectangle(vpx,vpy,w,h), bounds);

  }

  @Override
  public boolean handleMouseInput(int x, int y, int b) {
    if(!isMouseInBounds(x, y)) {
      renderer.handleMouseInput();
      return false;
    }

    renderer.handleMouseInput();
    return true;
  }

  @Override
  public boolean isMouseInBounds(int mouseX, int mouseY) {
    int x = mouseX - screen.getGuiLeft();
    int y = mouseY - screen.getGuiTop();
    if(bounds.contains(x,y)) {
      return true;
    }
    return false;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;

  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public Rectangle getBounds() {
    return bounds;
  }

  public SelectedFace getSelection() {
    return renderer.getSelection();
  }



}
