package crazypants.enderio.conduit.gui;

import java.awt.Color;

import mekanism.api.gas.Gas;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.IIcon;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;

public class GasSettings extends BaseSettingsPanel {

    static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

    private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();

    private RedstoneModeButton rsB;

    private ColorButton colorB;

    private String autoExtractStr = EnderIO.lang.localize("gui.conduit.gas.autoExtract");

    private IGasConduit conduit;

    protected GasSettings(final GuiExternalConnection gui, IConduit con) {
        super(IconEIO.WRENCH_OVERLAY_GAS, EnderIO.lang.localize("itemGasConduit.name"), gui, con);

        conduit = (IGasConduit) con;
        gui.getContainer().setInventorySlotsVisible(false);

        int x = gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap * 2;
        int y = customTop;

        rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

            @Override
            public void setRedstoneControlMode(RedstoneControlMode mode) {
                RedstoneControlMode curMode = getRedstoneControlMode();
                conduit.setExtractionRedstoneMode(mode, gui.getDir());
                if (curMode != mode) {
                    PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
                }
            }

            @Override
            public RedstoneControlMode getRedstoneControlMode() {
                return conduit.getExtractionRedstoneMode(gui.getDir());
            }
        });

        x += rsB.getWidth() + gap;
        colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
        colorB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.redstone.signalColor"));
        colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == ID_COLOR_BUTTON) {
            conduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
            PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
        }
    }

    @Override
    protected void connectionModeChanged(ConnectionMode conectionMode) {
        super.connectionModeChanged(conectionMode);
        if (conectionMode == ConnectionMode.INPUT) {
            rsB.onGuiInit();
            colorB.onGuiInit();
        } else {
            rsB.detach();
            colorB.detach();
        }
    }

    @Override
    public void mouseClicked(int x, int y, int par3) {}

    @Override
    public void deactivate() {
        super.deactivate();
        rsB.setToolTip((String[]) null);
        colorB.setToolTip((String[]) null);
    }

    @Override
    protected void renderCustomOptions(int top, float par1, int par2, int par3) {
        boolean isInput = isInput();
        if (isInput) {
            int x = gui.getGuiLeft() + gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap + 2;
            int y = customTop;
            gui.getFontRenderer().drawString(autoExtractStr, left, top, ColorUtil.getRGB(Color.DARK_GRAY));
        }
    }

    private void renderGas(Gas f, int x, int y) {
        IIcon icon = f.getIcon();
        if (icon != null) {
            RenderUtil.bindBlockTexture();
            gui.drawTexturedModelRectFromIcon(x + 1, y + 1, icon, 16, 16);
        }
    }

    private boolean isInput() {
        return conduit.getConnectionMode(gui.getDir()) == ConnectionMode.INPUT;
    }
}
