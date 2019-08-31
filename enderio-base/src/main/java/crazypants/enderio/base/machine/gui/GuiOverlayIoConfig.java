package crazypants.enderio.base.machine.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.gui.IoConfigRenderer;
import crazypants.enderio.base.gui.IoConfigRenderer.SelectedFace;
import crazypants.enderio.base.machine.interfaces.IIoConfigurable;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class GuiOverlayIoConfig<E extends TileEntity & IIoConfigurable> implements IGuiOverlay {

  private boolean visible = false;
  private ToggleButton configB;

  private IGuiScreen screen;

  private @Nonnull Rectangle bounds = new Rectangle();
  int height = 80;

  private IoConfigRenderer<E> renderer;

  private @Nonnull NNList<BlockPos> coords = new NNList<BlockPos>();

  public GuiOverlayIoConfig(IIoConfigurable ioConf) {
    coords.add(ioConf.getLocation());
  }

  public GuiOverlayIoConfig(Collection<BlockPos> bc) {
    coords.addAll(bc);
  }

  public void setConfigB(ToggleButton configB) {
    this.configB = configB;
  }

  @Override
  public void init(@Nonnull IGuiScreen screenIn) {
    this.screen = screenIn;
    renderer = new IoConfigRenderer<E>(coords) {

      @Override
      protected @Nonnull String getLabelForMode(@Nonnull IoMode mode) {
        return GuiOverlayIoConfig.this.getLabelForMode(mode);
      }

    };
    renderer.init();
    bounds = new Rectangle(screenIn.getOverlayOffsetXLeft() + 5, screenIn.getGuiYSize() - height - 5,
        screenIn.getGuiXSize() - screenIn.getOverlayOffsetXRight() - 10, height);
  }

  protected @Nonnull String getLabelForMode(IoMode mode) {
    return mode.getLocalisedName();
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTick) {

    RenderUtil.renderQuad2D(bounds.x, bounds.y, 0, bounds.width, bounds.height, ColorUtil.getRGB(Color.black));

    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution scaledresolution = new ScaledResolution(mc);

    int vpx = ((screen.getGuiRootLeft() + screen.getOverlayOffsetXLeft() + 5) * scaledresolution.getScaleFactor());
    int vpy = (screen.getGuiRootTop() + 4) * scaledresolution.getScaleFactor();
    int w = bounds.width * scaledresolution.getScaleFactor();
    int h = bounds.height * scaledresolution.getScaleFactor();

    renderer.drawScreen(mouseX, mouseY, partialTick, new Rectangle(vpx, vpy, w, h), bounds);
  }

  @Override
  public boolean handleMouseInput(int x, int y, int b) {
    if (!isMouseInBounds(x, y)) {
      renderer.handleMouseInput();
      return false;
    }

    renderer.handleMouseInput();
    return true;
  }

  @Override
  public boolean isMouseInBounds(int mouseX, int mouseY) {
    int x = mouseX - screen.getGuiRootLeft();
    int y = mouseY - screen.getGuiRootTop();
    if (bounds.contains(x, y)) {
      return true;
    }
    return false;
  }

  @Override
  public void setIsVisible(boolean visible) {
    this.visible = visible;
    if (configB != null) {
      configB.setSelected(visible);
    }
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public @Nonnull Rectangle getBounds() {
    return bounds;
  }

  public SelectedFace<E> getSelection() {
    return visible ? renderer.getSelection() : null;
  }

  @Override
  public void guiClosed() {
  }

}
