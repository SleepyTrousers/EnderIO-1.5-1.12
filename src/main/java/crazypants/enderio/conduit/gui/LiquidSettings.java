package crazypants.enderio.conduit.gui;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.AbstractEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.FluidFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketRoundRobinMode;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class LiquidSettings extends BaseSettingsPanel {

    static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

    private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
    private static final int ID_ROUND_ROBIN_BUTTON = GuiExternalConnection.nextButtonId();
    private static final int ID_INSERT_WHITELIST = GuiExternalConnection.nextButtonId();
    private static final int ID_EXTRACT_WHITELIST = GuiExternalConnection.nextButtonId();

    private static final int NEXT_FILTER_ID = 989322;

    private static final int ID_INSERT_CHANNEL = GuiExternalConnection.nextButtonId();
    private static final int ID_EXTRACT_CHANNEL = GuiExternalConnection.nextButtonId();

    private final RedstoneModeButton rsB;
    private final ToggleButton roundRobinB;
    private final ColorButton colorB;
    private ColorButton insertChannelB;
    private ColorButton extractChannelB;

    private static final String autoExtractStr = EnderIO.lang.localize("gui.conduit.fluid.autoExtract");
    private static final String filterStr = EnderIO.lang.localize("gui.conduit.fluid.filter");

    private final ILiquidConduit conduit;

    private AbstractEnderLiquidConduit eConduit;
    private boolean isEnder;
    private static final int filterIX = 4;
    private static final int filterEX = 104;
    private static final int filterY = 63;
    private static final Rectangle insertFilterBounds = new Rectangle(filterIX, filterY, 90, 18);
    private static final Rectangle extractFilterBounds = new Rectangle(filterEX, filterY, 90, 18);
    private GuiToolTip[] filterToolTips;

    private boolean inOutShowIn = true;
    private IconButton insertWhiteListB;
    private IconButton extractWhiteListB;

    protected LiquidSettings(final GuiExternalConnection gui, IConduit con) {
        super(
                IconEIO.WRENCH_OVERLAY_FLUID,
                EnderIO.lang.localize("itemLiquidConduit.name"),
                gui,
                con,
                "in_out_settings");
        this.textureHeight += 48;

        conduit = (ILiquidConduit) con;
        if (con instanceof AbstractEnderLiquidConduit) {
            eConduit = (AbstractEnderLiquidConduit) con;
            isEnder = true;

            int x = leftColumn;
            int y = customTop;
            insertWhiteListB = new IconButton(gui, ID_INSERT_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
            insertWhiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.whitelist"));

            x = rightColumn;
            extractWhiteListB = new IconButton(gui, ID_EXTRACT_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
            extractWhiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.whitelist"));
        } else {
            isEnder = false;
            gui.getContainer().setInventorySlotsVisible(false);
        }

        int x = leftColumn + 21;
        int y = customTop;

        if (isEnder) {
            insertChannelB = new ColorButton(gui, ID_INSERT_CHANNEL, x, y);
            insertChannelB.setColorIndex(0);
            insertChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));
            extractChannelB = new ColorButton(gui, ID_EXTRACT_CHANNEL, x + rightColumn - leftColumn, y);
            extractChannelB.setColorIndex(0);
            extractChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));
            x += insertChannelB.getWidth();
        }

        x += rightColumn - leftColumn;
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

        x += rsB.getWidth();
        colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
        colorB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.redstone.signalColor"));
        colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

        if (isEnder) {
            x += rsB.getWidth();
            roundRobinB =
                    new ToggleButton(gui, ID_ROUND_ROBIN_BUTTON, x, y, IconEIO.ROUND_ROBIN_OFF, IconEIO.ROUND_ROBIN);
            roundRobinB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinEnabled"));
            roundRobinB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinDisabled"));
            roundRobinB.setPaintSelectedBorder(false);
        } else {
            roundRobinB = null;
        }
    }

    private void addFilterTooltips() {
        filterToolTips = new GuiToolTip[5];
        for (int i = 0; i < 5; i++) {
            Rectangle bound = new Rectangle(filterIX + (i * 18), filterY, 18, 18);
            filterToolTips[i] = new FilterToolTip(bound, i, false);
            gui.addToolTip(filterToolTips[i]);
        }
        for (int i = 0; i < 5; i++) {
            Rectangle bound = new Rectangle(filterEX + (i * 18), filterY, 18, 18);
            filterToolTips[i] = new FilterToolTip(bound, i, true);
            gui.addToolTip(filterToolTips[i]);
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == ID_COLOR_BUTTON) {
            conduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
            PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
        } else if (guiButton.id == ID_INSERT_WHITELIST || guiButton.id == ID_EXTRACT_WHITELIST) {
            toggleBlacklist(guiButton.id == ID_EXTRACT_WHITELIST);
        } else if (guiButton.id == ID_INSERT_CHANNEL || guiButton.id == ID_EXTRACT_CHANNEL) {
            ColorButton btn = (ColorButton) guiButton;
            if (isEnder) {

                DyeColor col = DyeColor.values()[btn.getColorIndex()];
                boolean isInput = guiButton.id == ID_EXTRACT_CHANNEL;

                if (isInput) {
                    eConduit.setInputColor(gui.getDir(), col);
                } else {
                    eConduit.setOutputColor(gui.getDir(), col);
                }
                setConduitChannel(isInput, col);
            }
        } else if (guiButton.id == ID_ROUND_ROBIN_BUTTON) {
            if (isEnder) {
                final boolean selected = roundRobinB.isSelected();
                eConduit.setRoundRobin(gui.getDir(), selected);
                PacketHandler.INSTANCE.sendToServer(new PacketRoundRobinMode(eConduit, gui.getDir()));
            }
        }
    }

    @Override
    protected void connectionModeChanged(ConnectionMode conectionMode) {
        super.connectionModeChanged(conectionMode);
        updateGuiVisibility();
    }

    private void toggleBlacklist(boolean isInput) {
        if (!isFilterVisible()) {
            return;
        }
        FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);
        if (filter == null) {
            filter = new FluidFilter();
        }
        filter.setBlacklist(!filter.isBlacklist());
        setConduitFilter(isInput, filter);
        updateWhiteListButton(filter, isInput);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (!isFilterVisible()) {
            return;
        }
        ItemStack st = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
        setFilterFromItem(x, y, st);
    }

    public boolean setFilterFromItem(int x, int y, ItemStack st) {
        if (insertFilterBounds.contains(x, y)) {
            FluidFilter filter = eConduit.getFilter(gui.getDir(), false);
            if (filter == null && st == null) {
                return false;
            }
            if (filter == null) {
                filter = new FluidFilter();
            }
            int slot = (x - filterIX) / 18;
            filter.setFluid(slot, st);
            setConduitFilter(false, filter);
            return true;
        }
        if (extractFilterBounds.contains(x, y)) {
            FluidFilter filter = eConduit.getFilter(gui.getDir(), true);
            if (filter == null && st == null) {
                return false;
            }
            if (filter == null) {
                filter = new FluidFilter();
            }
            int slot = (x - filterEX) / 18;
            filter.setFluid(slot, st);
            setConduitFilter(true, filter);
            return true;
        }
        return false;
    }

    protected void setConduitFilter(boolean isInput, FluidFilter filter) {
        eConduit.setFilter(gui.getDir(), filter, isInput);

        PacketHandler.INSTANCE.sendToServer(new PacketFluidFilter(eConduit, gui.getDir(), filter, isInput));
    }

    protected void setConduitChannel(boolean isInput, DyeColor channel) {
        if (isInput) {
            eConduit.setInputColor(gui.getDir(), channel);
        } else {
            eConduit.setOutputColor(gui.getDir(), channel);
        }

        PacketHandler.INSTANCE.sendToServer(new PacketFluidChannel(eConduit, gui.getDir(), isInput, channel));
    }

    @Override
    protected void initCustomOptions() {
        updateGuiVisibility();
    }

    private void updateGuiVisibility() {
        deactivate();

        rsB.onGuiInit();
        colorB.onGuiInit();

        if (!isEnder) {
            return;
        }

        insertChannelB.onGuiInit();
        insertChannelB.setColorIndex(eConduit.getOutputColor(gui.getDir()).ordinal());
        extractChannelB.onGuiInit();
        extractChannelB.setColorIndex(eConduit.getInputColor(gui.getDir()).ordinal());
        roundRobinB.onGuiInit();
        roundRobinB.setSelected(eConduit.isRoundRobin(gui.getDir()));

        gui.getContainer().setInventorySlotsVisible(true);
        if (isFilterVisible()) {
            addFilterTooltips();

            insertWhiteListB.onGuiInit();
            extractWhiteListB.onGuiInit();
            updateWhiteListButtons();
        }
    }

    private void updateWhiteListButtons() {
        updateWhiteListButton(eConduit.getFilter(gui.getDir(), false), false);
        updateWhiteListButton(eConduit.getFilter(gui.getDir(), true), true);
    }

    private void updateWhiteListButton(FluidFilter filter, boolean isInput) {
        IconButton whiteListB = isInput ? extractWhiteListB : insertWhiteListB;
        if (filter != null && filter.isBlacklist()) {
            whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
            whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.blacklist"));
        } else {
            whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
            whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.whitelist"));
        }
    }

    @Override
    public void deactivate() {

        rsB.detach();
        colorB.detach();
        gui.getContainer().setInventorySlotsVisible(false);
        if (isEnder) {
            roundRobinB.detach();
            if (filterToolTips != null) {
                for (GuiToolTip tt : filterToolTips) {
                    if (tt != null) {
                        gui.removeToolTip(tt);
                    }
                }
            }
            insertWhiteListB.detach();
            extractWhiteListB.detach();
            insertChannelB.detach();
            extractChannelB.detach();
        }
    }

    @Override
    protected void renderCustomOptions(int top, float par1, int par2, int par3) {
        if (isEnder && isFilterVisible()) {
            int x = left;
            int y = top;

            GL11.glColor3f(1, 1, 1);
            gui.bindGuiTexture(1);
            // gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);

            FontRenderer fr = gui.getFontRenderer();
            int sw = fr.getStringWidth(filterStr);
            x = left + 50 - sw / 2;
            y = top + 20;
            fr.drawString(filterStr, x, y, ColorUtil.getRGB(Color.DARK_GRAY));
            x = left + 150 - sw / 2;
            fr.drawString(filterStr, x, y, ColorUtil.getRGB(Color.DARK_GRAY));

            x = gui.getGuiLeft() + filterIX;
            y = gui.getGuiTop() + filterY;
            GL11.glColor3f(1, 1, 1);
            gui.bindGuiTexture();
            gui.drawTexturedModalRect(x, y, 24, 238, 90, 18);

            FluidFilter filter = eConduit.getFilter(gui.getDir(), false);
            if (filter != null && !filter.isEmpty()) {
                for (int i = 0; i < filter.size(); i++) {
                    FluidStack f = filter.getFluidStackAt(i);
                    if (f != null) {
                        renderFluid(f, x + (i * 18), y);
                    }
                }
            }

            x = gui.getGuiLeft() + filterEX;
            GL11.glColor3f(1, 1, 1);
            gui.bindGuiTexture();
            gui.drawTexturedModalRect(x, y, 24, 238, 90, 18);

            filter = eConduit.getFilter(gui.getDir(), true);
            if (filter != null && !filter.isEmpty()) {
                for (int i = 0; i < filter.size(); i++) {
                    FluidStack f = filter.getFluidStackAt(i);
                    if (f != null) {
                        renderFluid(f, x + (i * 18), y);
                    }
                }
            }
        }
    }

    private void renderFluid(FluidStack f, int x, int y) {
        IIcon icon = f.getFluid().getIcon();
        if (icon != null) {
            RenderUtil.bindBlockTexture();
            int color = f.getFluid().getColor(f);
            GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
            gui.drawTexturedModelRectFromIcon(x + 1, y + 1, icon, 16, 16);
            GL11.glColor3f(1, 1, 1);
        }
    }

    private boolean isFilterVisible() {
        return isEnder;
    }

    private class FilterToolTip extends GuiToolTip {

        int index;
        boolean isInput;

        public FilterToolTip(Rectangle bounds, int index, boolean isInput) {
            super(bounds, (String[]) null);
            this.index = index;
            this.isInput = isInput;
        }

        @Override
        public List<String> getToolTipText() {
            if (!isFilterVisible()) {
                return null;
            }
            FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);
            if (filter == null) {
                return null;
            }
            if (filter.getFluidStackAt(index) == null) {
                return null;
            }
            return Collections.singletonList(filter.getFluidStackAt(index).getLocalizedName());
        }
    }
}
