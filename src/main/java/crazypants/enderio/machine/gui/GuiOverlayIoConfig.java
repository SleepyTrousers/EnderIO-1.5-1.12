package crazypants.enderio.machine.gui;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.gui.IoConfigRenderer;
import crazypants.enderio.gui.IoConfigRenderer.SelectedFace;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class GuiOverlayIoConfig implements IGuiOverlay {

    private boolean visible = false;
    private ToggleButton configB;

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

    public void setConfigB(ToggleButton configB) {
        this.configB = configB;
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
        bounds = new Rectangle(
                screen.getOverlayOffsetX() + 5, screen.getYSize() - height - 5, screen.getXSize() - 10, height);
    }

    protected String getLabelForMode(IoMode mode) {
        return mode.getLocalisedName();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {

        RenderUtil.renderQuad2D(bounds.x, bounds.y, 0, bounds.width, bounds.height, ColorUtil.getRGB(Color.black));
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int vpx = ((screen.getGuiLeft() + bounds.x - screen.getOverlayOffsetX()) * scaledresolution.getScaleFactor());
        int vpy = (screen.getGuiTop() + 4) * scaledresolution.getScaleFactor();
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
        int x = mouseX - screen.getGuiLeft();
        int y = mouseY - screen.getGuiTop();
        if (bounds.contains(x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
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
    public Rectangle getBounds() {
        return bounds;
    }

    public SelectedFace getSelection() {
        return visible ? renderer.getSelection() : null;
    }
}
