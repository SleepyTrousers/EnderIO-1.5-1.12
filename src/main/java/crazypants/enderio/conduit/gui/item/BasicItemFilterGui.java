package crazypants.enderio.conduit.gui.item;

import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.filter.FuzzyMode;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;

public class BasicItemFilterGui implements IItemFilterGui {

    private static final int ID_WHITELIST = GuiExternalConnection.nextButtonId();
    private static final int ID_NBT = GuiExternalConnection.nextButtonId();
    private static final int ID_META = GuiExternalConnection.nextButtonId();
    private static final int ID_ORE_DICT = GuiExternalConnection.nextButtonId();
    private static final int ID_STICKY = GuiExternalConnection.nextButtonId();
    private static final int ID_FUZZY = GuiExternalConnection.nextButtonId();

    private final GuiContainerBaseEIO gui;

    private final ToggleButton useMetaB;
    private final ToggleButton useNbtB;
    private final IconButton whiteListB;
    private final ToggleButton useOreDictB;
    private final ToggleButton stickyB;
    private final CycleButton<FuzzyMode> fuzzyB;

    final boolean isAdvanced;
    final boolean isStickyModeAvailable;

    private final IItemFilterContainer filterContainer;
    private final ItemFilter filter;

    private int buttonIdOffset;
    private int xOffset;
    private int yOffset;

    private boolean isInput;

    public BasicItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer,
            boolean isStickyModeAvailable, boolean isInput) {
        this(gui, filterContainer, isStickyModeAvailable, isInput, isInput ? 104 : 6, 96, isInput ? 0 : 256);
    }

    public BasicItemFilterGui(GuiContainerBaseEIO gui, IItemFilterContainer filterContainer,
            boolean isStickyModeAvailable, boolean isInput, int xOffset, int yOffset, int buttonIdOffset) {
        this.gui = gui;
        this.isStickyModeAvailable = isStickyModeAvailable;
        this.filterContainer = filterContainer;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.buttonIdOffset = buttonIdOffset;
        this.isInput = isInput;

        filter = filterContainer.getItemFilter();

        isAdvanced = filter.isAdvanced();

        int butLeft = xOffset;
        int x = butLeft;
        int y = yOffset + (isAdvanced ? 40 : 20);
        whiteListB = new IconButton(gui, ID_WHITELIST + buttonIdOffset, x, y, IconEIO.FILTER_WHITELIST);
        whiteListB.setToolTip(getWhitelistTooltips(false));

        x += 16;
        useMetaB = new ToggleButton(gui, ID_META + buttonIdOffset, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
        useMetaB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchMetaData"));
        useMetaB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreMetaData"));
        useMetaB.setPaintSelectedBorder(false);

        x += 16;
        stickyB = new ToggleButton(
                gui,
                ID_STICKY + buttonIdOffset,
                x,
                y,
                IconEIO.FILTER_STICKY_OFF,
                IconEIO.FILTER_STICKY);
        stickyB.setSelectedToolTip(EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled"));
        stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
        stickyB.setPaintSelectedBorder(false);

        if (isStickyModeAvailable) x += 16;
        useOreDictB = new ToggleButton(
                gui,
                ID_ORE_DICT + buttonIdOffset,
                x,
                y,
                IconEIO.FILTER_ORE_DICT_OFF,
                IconEIO.FILTER_ORE_DICT);
        useOreDictB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicEnabled"));
        useOreDictB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.oreDicDisabled"));
        useOreDictB.setPaintSelectedBorder(false);

        x += 16;
        useNbtB = new ToggleButton(gui, ID_NBT + buttonIdOffset, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
        useNbtB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.matchNBT"));
        useNbtB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.ignoreNBT"));
        useNbtB.setPaintSelectedBorder(false);

        x += 16;
        fuzzyB = new CycleButton(gui, ID_FUZZY + buttonIdOffset, x, y, FuzzyMode.class);
    }

    public void createFilterSlots() {
        filter.createGhostSlots(gui.getGhostSlots(), xOffset + 1, yOffset + 1, new Runnable() {

            @Override
            public void run() {
                sendFilterChange();
            }
        });
    }

    @Override
    public void mouseClicked(int x, int y, int par3) {}

    @Override
    public void updateButtons() {
        ItemFilter activeFilter = filter;

        if (isAdvanced) {
            useNbtB.onGuiInit();
            useNbtB.setSelected(activeFilter.isMatchNBT());

            useOreDictB.onGuiInit();
            useOreDictB.setSelected(activeFilter.isUseOreDict());

            if (isStickyModeAvailable) {
                stickyB.onGuiInit();
                stickyB.setSelected(activeFilter.isSticky());
            }

            fuzzyB.onGuiInit();
            fuzzyB.setMode(activeFilter.getFuzzyMode());
        }

        useMetaB.onGuiInit();
        useMetaB.setSelected(activeFilter.isMatchMeta());

        whiteListB.onGuiInit();
        whiteListB.setToolTip(getWhitelistTooltips(activeFilter.isBlacklist()));
        if (activeFilter.isBlacklist()) {
            whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
        } else {
            whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {

        if (guiButton.id == ID_META + buttonIdOffset) {
            filter.setMatchMeta(useMetaB.isSelected());
            sendFilterChange();
        } else if (guiButton.id == ID_NBT + buttonIdOffset) {
            filter.setMatchNBT(useNbtB.isSelected());
            sendFilterChange();
        } else if (guiButton.id == ID_STICKY + buttonIdOffset) {
            filter.setSticky(stickyB.isSelected());
            sendFilterChange();
        } else if (guiButton.id == ID_ORE_DICT + buttonIdOffset) {
            filter.setUseOreDict(useOreDictB.isSelected());
            sendFilterChange();
        } else if (guiButton.id == ID_FUZZY + buttonIdOffset) {
            filter.setFuzzyMode(fuzzyB.getMode());
            sendFilterChange();
        } else if (guiButton.id == ID_WHITELIST + buttonIdOffset) {
            filter.setBlacklist(!filter.isBlacklist());
            sendFilterChange();
        }
    }

    private void sendFilterChange() {
        updateButtons();
        filterContainer.onFilterChanged();
    }

    @Override
    public void deactivate() {
        useNbtB.detach();
        useMetaB.detach();
        useOreDictB.detach();
        whiteListB.detach();
        stickyB.detach();
        fuzzyB.detach();
    }

    @Override
    public void renderCustomOptions(int top, float par1, int par2, int par3) {
        GL11.glColor3f(1, 1, 1);
        gui.bindGuiTexture(1);
        gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset, 0, 238, 18 * 5, 18);
        if (filter.isAdvanced()) {
            gui.drawTexturedModalRect(gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset + 20, 0, 238, 18 * 5, 18);
        }
    }

    private String[] getWhitelistTooltips(boolean isBlacklist) {
        if (isBlacklist) {
            return new String[] { EnderIO.lang.localize("gui.conduit.item.blacklist"),
                    EnderIO.lang.localize("gui.conduit.item.blacklist.tooltip.0"),
                    EnderIO.lang.localize("gui.conduit.item.blacklist.tooltip.1") };
        } else {
            return new String[] { EnderIO.lang.localize("gui.conduit.item.whitelist"),
                    EnderIO.lang.localize("gui.conduit.item.whitelist.tooltip.0"),
                    EnderIO.lang.localize("gui.conduit.item.whitelist.tooltip.1") };
        }
    }
}
