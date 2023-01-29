package crazypants.enderio.machine.transceiver.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.TileTransceiver;

public class GuiTransceiver extends GuiPoweredMachineBase<TileTransceiver> {

    private static final int TAB_HEIGHT = 24;

    private int activeTab = 0;
    private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
    private final int tabYOffset = 4;
    GeneralTab generalTab;

    public GuiTransceiver(InventoryPlayer par1InventoryPlayer, TileTransceiver te) {
        super(te, new ContainerTransceiver(par1InventoryPlayer, te), "transceiver", "itemFilter");

        generalTab = new GeneralTab(this);
        tabs.add(generalTab);
        FilterTab filterTab = new FilterTab(this);
        tabs.add(filterTab);
        tabs.add(new ChannelTab(this, ChannelType.POWER));
        tabs.add(new ChannelTab(this, ChannelType.ITEM));
        tabs.add(new ChannelTab(this, ChannelType.FLUID));
        if (Config.enderRailEnabled) {
            tabs.add(new ChannelTab(this, ChannelType.RAIL));
        }
    }

    @Override
    protected void updatePowerBarTooltip(List<String> text) {
        generalTab.updatePowerBarTooltip(text);
    }

    @Override
    protected boolean showRecipeButton() {
        return false;
    }

    @Override
    public int getXSize() {
        return ContainerTransceiver.GUI_WIDTH;
    }

    @Override
    public void updateScreen() {
        for (int i = 0; i < tabs.size(); i++) {
            if (i == activeTab) {
                tabs.get(i).updateScreen();
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == 1) {
            for (IGuiOverlay overlay : overlays) {
                if (overlay.isVisible()) {
                    overlay.setVisible(false);
                    return;
                }
            }
            mc.thePlayer.closeScreen();
        }

        for (int i = 0; i < tabs.size(); i++) {
            if (i == activeTab) {
                tabs.get(i).keyTyped(par1, par2);
                return;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        for (int i = 0; i < tabs.size(); i++) {
            if (i != activeTab) {
                tabs.get(i).deactivate();
            }
        }
        ghostSlots.clear();
        for (int i = 0; i < tabs.size(); i++) {
            if (i == activeTab) {
                tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
            }
        }
        configB.visible = activeTab == 0;
        redstoneButton.visible = activeTab == 0;
    }

    @Override
    public void renderPowerBar(int k, int l) {
        // super.renderPowerBar(k, l);
    }

    @Override
    protected boolean renderPowerBar() {
        return activeTab == 0;
    }

    @Override
    public int getPowerX() {
        return super.getPowerX() - 4;
    }

    @Override
    public int getPowerHeight() {
        return 58;
    }

    @Override
    public int getPowerY() {
        return super.getPowerY();
    }

    @Override
    public int getPowerWidth() {
        return POWER_WIDTH;
    }

    @Override
    public int getPowerV() {
        return 196;
    }

    @Override
    public int getPowerU() {
        return 246;
    }

    @Override
    public String getPowerOutputLabel() {
        return super.getPowerOutputLabel();
    }

    @Override
    public int getPowerOutputValue() {
        return super.getPowerOutputValue();
    }

    @Override
    protected void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);

        int tabLeftX = xSize;
        int tabRightX = tabLeftX + 22;

        int minY = tabYOffset;
        int maxY = minY + (tabs.size() * TAB_HEIGHT);

        x = (x - guiLeft);
        y = (y - guiTop);

        if (x > tabLeftX && x < tabRightX + 24) {
            if (y > minY && y < maxY) {
                activeTab = (y - minY) / 24;
                hideOverlays();
                initGui();
                return;
            }
        }
        tabs.get(activeTab).mouseClicked(x, y, par3);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        tabs.get(activeTab).actionPerformed(guiButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;
        int tabX = sx + xSize - 3;

        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        for (int i = 0; i < tabs.size(); i++) {
            if (i != activeTab) {
                RenderUtil.bindTexture(IconEIO.TEXTURE);
                IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, sy + tabYOffset + (i * 24));
                IWidgetIcon icon = tabs.get(i).getIcon();
                icon.getMap().render(icon, tabX + 4, sy + tabYOffset + (i * TAB_HEIGHT) + 6, 11, 11, 0, false);
            }
        }

        tes.draw();

        bindGuiTexture();
        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.bindTexture(IconEIO.TEXTURE);
        tes.startDrawingQuads();
        IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, sy + tabYOffset + (activeTab * TAB_HEIGHT));

        if (tabs.size() > 0) {
            IWidgetIcon icon = tabs.get(activeTab).getIcon();
            icon.getMap().render(icon, tabX - 1, sy + tabYOffset + (activeTab * TAB_HEIGHT) + 4);
            tes.draw();
            tabs.get(activeTab).render(par1, par2, par3);
        } else {
            tes.draw();
        }
    }

    public TileTransceiver getTransciever() {
        return getTileEntity();
    }

    public ContainerTransceiver getContainer() {
        return (ContainerTransceiver) inventorySlots;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
        if (tabs.size() > 0) {
            int sx = (width - xSize) / 2;
            int sy = (height - ySize) / 2;
            int tabX = sx + xSize - 3;
            int tabY = sy + tabYOffset;

            return (x + w) >= tabX && x < (tabX + 14) && (y + h) >= tabY && y < (tabY + tabs.size() * TAB_HEIGHT);
        }
        return false;
    }
}
