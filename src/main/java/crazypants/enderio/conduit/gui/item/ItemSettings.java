package crazypants.enderio.conduit.gui.item;

import java.awt.*;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.FilterChangeListener;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;

public class ItemSettings extends BaseSettingsPanel {

    private static final int ID_REDSTONE_BUTTON = 12614;
    private static final int ID_COLOR_BUTTON = 179816;
    private static final int ID_LOOP = 22;
    private static final int ID_ROUND_ROBIN = 24;
    private static final int ID_PRIORITY_UP = 25;
    private static final int ID_PRIORITY_DOWN = 26;
    private static final int ID_INSERT_CHANNEL = 23;
    private static final int ID_EXTRACT_CHANNEL = 27;

    private @Nonnull IItemConduit itemConduit;

    private final ToggleButton loopB;
    private final ToggleButton roundRobinB;

    private final MultiIconButton priUpB;
    private final MultiIconButton priDownB;

    private final RedstoneModeButton rsB;
    private final @Nonnull ColorButton colorB;

    private ColorButton insertChannelB;
    private ColorButton extractChannelB;

    private int priLeft = 46;
    private int priWidth = 32;

    private IItemFilterGui insertFilterGui;
    private IItemFilterGui extractFilterGui;

    public ItemSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IConduit con) {
        super(
                IconEIO.WRENCH_OVERLAY_ITEM,
                EnderIO.lang.localize("itemItemConduit.name"),
                gui,
                con,
                "filter_upgrade_settings");
        itemConduit = (IItemConduit) con;
        this.textureHeight += 48;

        int x = leftColumn;
        int y = customTop;

        insertChannelB = new ColorButton(gui, ID_INSERT_CHANNEL, x, y);
        insertChannelB.setColorIndex(0);
        insertChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));

        x = rightColumn;
        extractChannelB = new ColorButton(gui, ID_EXTRACT_CHANNEL, x, y);
        extractChannelB.setColorIndex(0);
        extractChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));

        x += 4 + extractChannelB.getWidth();
        roundRobinB = new ToggleButton(gui, ID_ROUND_ROBIN, x, y, IconEIO.ROUND_ROBIN_OFF, IconEIO.ROUND_ROBIN);
        roundRobinB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinEnabled"));
        roundRobinB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinDisabled"));
        roundRobinB.setPaintSelectedBorder(false);

        x += 4 + roundRobinB.getWidth();
        loopB = new ToggleButton(gui, ID_LOOP, x, y, IconEIO.LOOP_OFF, IconEIO.LOOP);
        loopB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.selfFeedEnabled"));
        loopB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.selfFeedDisabled"));
        loopB.setPaintSelectedBorder(false);

        y += insertChannelB.getHeight() + 6;
        x = rightColumn;

        int x0 = x + 20;
        colorB = new ColorButton(gui, ID_COLOR_BUTTON, x0, y);
        colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
        colorB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));

        rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

            @Override
            public void setRedstoneControlMode(RedstoneControlMode mode) {
                RedstoneControlMode curMode = getRedstoneControlMode();
                itemConduit.setExtractionRedstoneMode(mode, gui.getDir());
                if (curMode != mode) {
                    PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
                }
            }

            @Override
            public RedstoneControlMode getRedstoneControlMode() {
                return itemConduit.getExtractionRedstoneMode(gui.getDir());
            }
        });

        x = priLeft + priWidth + 9;
        priUpB = MultiIconButton.createAddButton(gui, ID_PRIORITY_UP, x, y);
        priDownB = MultiIconButton.createMinusButton(gui, ID_PRIORITY_DOWN, x, y + 8);

        FilterChangeListener fcl = new FilterChangeListener() {

            @Override
            public void onFilterChanged() {
                if (insertFilterGui != null) {
                    insertFilterGui.deactivate();
                    insertFilterGui = null;
                }
                if (extractFilterGui != null) {
                    extractFilterGui.deactivate();
                    extractFilterGui = null;
                }
                gui.clearGhostSlots();
                IItemFilter filt = gui.getContainer().getFilter(false);
                if (filt != null) {
                    insertFilterGui = filt.getGui(gui, gui.getContainer().getInv().itemConduit, false);
                }
                filt = gui.getContainer().getFilter(true);
                if (filt != null) {
                    extractFilterGui = filt.getGui(gui, gui.getContainer().getInv().itemConduit, true);
                }
                if (insertFilterGui != null) {
                    insertFilterGui.updateButtons();
                }
                if (extractFilterGui != null) {
                    extractFilterGui.updateButtons();
                }
            }
        };
        fcl.onFilterChanged();
        gui.getContainer().addFilterListener(fcl);
    }

    @Override
    protected void initCustomOptions() {
        gui.getContainer().setInoutSlotsVisible(true, true);
        gui.getContainer().createGhostSlots(gui.getGhostSlots());
        gui.getContainer().setInventorySlotsVisible(true);
        updateGuiVisibility();
    }

    private void updateGuiVisibility() {
        updateButtons();
    }

    private void updateButtons() {
        rsB.onGuiInit();
        rsB.setMode(itemConduit.getExtractionRedstoneMode(gui.getDir()));

        loopB.onGuiInit();
        loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.getDir()));
        roundRobinB.onGuiInit();
        roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.getDir()));

        priUpB.onGuiInit();
        priDownB.onGuiInit();

        insertChannelB.onGuiInit();
        insertChannelB.setColorIndex(itemConduit.getOutputColor(gui.getDir()).ordinal());
        extractChannelB.onGuiInit();
        extractChannelB.setColorIndex(itemConduit.getInputColor(gui.getDir()).ordinal());

        if (insertFilterGui != null) insertFilterGui.updateButtons();
        if (extractFilterGui != null) extractFilterGui.updateButtons();
    }

    @Override
    public void actionPerformed(@Nonnull GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == ID_COLOR_BUTTON) {
            itemConduit.setExtractionSignalColor(gui.getDir(), DyeColor.fromIndex(colorB.getColorIndex()));
            PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
            return;
        } else if (guiButton.id == ID_LOOP) {
            itemConduit.setSelfFeedEnabled(gui.getDir(), !itemConduit.isSelfFeedEnabled(gui.getDir()));
        } else if (guiButton.id == ID_ROUND_ROBIN) {
            itemConduit.setRoundRobinEnabled(gui.getDir(), !itemConduit.isRoundRobinEnabled(gui.getDir()));
        } else if (guiButton.id == ID_PRIORITY_UP) {
            itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) + 1);
        } else if (guiButton.id == ID_PRIORITY_DOWN) {
            itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) - 1);
        } else if (guiButton.id == ID_INSERT_CHANNEL) {
            DyeColor col = DyeColor.fromIndex(insertChannelB.getColorIndex());
            itemConduit.setOutputColor(gui.getDir(), col);
        } else if (guiButton.id == ID_EXTRACT_CHANNEL) {
            DyeColor col = DyeColor.fromIndex(extractChannelB.getColorIndex());
            itemConduit.setInputColor(gui.getDir(), col);
        } else {
            if (insertFilterGui != null) insertFilterGui.actionPerformed(guiButton);
            if (extractFilterGui != null) extractFilterGui.actionPerformed(guiButton);
            return;
        }
        PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    }

    @Override
    protected void connectionModeChanged(@Nonnull ConnectionMode mode) {
        super.connectionModeChanged(mode);
        PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
        updateGuiVisibility();
    }

    @Override
    protected void renderCustomOptions(int top1, float par1, int par2, int par3) {
        FontRenderer fr = gui.getFontRenderer();

        GL11.glColor3f(1, 1, 1);
        IconEIO.map.render(EnderWidget.BUTTON_DOWN, left + priLeft, top1 - 5, priWidth, 16, 0, true);
        String str = itemConduit.getOutputPriority(gui.getDir()) + "";
        int sw = fr.getStringWidth(str);

        String priority = EnderIO.lang.localize("gui.conduit.item.priority");
        fr.drawString(priority, left + 12, top1 + 25, ColorUtil.getRGB(Color.black));
        fr.drawString(str, left + priLeft + priWidth - sw - gap, top1 + 25, ColorUtil.getRGB(Color.black));

        if (insertFilterGui != null) insertFilterGui.renderCustomOptions(top1, par1, par2, par3);
        if (extractFilterGui != null) extractFilterGui.renderCustomOptions(top1, par1, par2, par3);
    }

    @Override
    public void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);
        if (insertFilterGui != null) insertFilterGui.mouseClicked(x, y, par3);
        if (extractFilterGui != null) extractFilterGui.mouseClicked(x, y, par3);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        gui.getContainer().setInoutSlotsVisible(false, false);
        rsB.detach();
        colorB.detach();
        roundRobinB.detach();
        loopB.detach();
        priUpB.detach();
        priDownB.detach();
        insertChannelB.detach();
        extractChannelB.detach();
        gui.getContainer().setInventorySlotsVisible(false);
        if (insertFilterGui != null) insertFilterGui.deactivate();
        if (extractFilterGui != null) extractFilterGui.deactivate();
    }

    @Override
    protected boolean hasFilters() {
        return true;
    }

    @Override
    protected boolean hasUpgrades() {
        return true;
    }
}
