package crazypants.enderio.conduit.gui.item;

import com.enderio.core.client.gui.button.ToggleButton;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.PowerItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class PowerItemFilterGui implements IItemFilterGui {

    private static final int ID_STICKY = GuiExternalConnection.nextButtonId();

    private static final int ID_MORE = GuiExternalConnection.nextButtonId();
    private static final int ID_LEVEL = GuiExternalConnection.nextButtonId();

    private final IItemConduit itemConduit;
    private final GuiExternalConnection gui;

    private final ToggleButton stickyB;

    private final GuiButton modeB;
    private final GuiButton levelB;

    private final boolean isInput;

    private final PowerItemFilter filter;

    public PowerItemFilterGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
        this.gui = gui;
        this.itemConduit = itemConduit;
        this.isInput = isInput;

        if (isInput) {
            filter = (PowerItemFilter) itemConduit.getInputFilter(gui.getDir());
        } else {
            filter = (PowerItemFilter) itemConduit.getOutputFilter(gui.getDir());
        }

        int butLeft = isInput ? 106 : 5;
        int x = butLeft;
        int y = 96 + 24;

        stickyB = new ToggleButton(gui, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
        String[] lines = EnderIO.lang.localizeList("gui.conduit.item.stickyEnabled");
        stickyB.setSelectedToolTip(lines);
        stickyB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.stickyDisbaled"));
        stickyB.setPaintSelectedBorder(false);

        modeB = new GuiButton(ID_MORE, 0, 0, 40, 20, "");
        levelB = new GuiButton(ID_LEVEL, 0, 0, 40, 20, "");
    }

    @Override
    public void mouseClicked(int x, int y, int par3) {
        x += gui.getGuiLeft();
        y += gui.getGuiTop();
        if (par3 == 1) {
            Minecraft mc = Minecraft.getMinecraft();
            if (modeB.mousePressed(mc, x, y)) {
                modeB.func_146113_a(mc.getSoundHandler());
                filter.setMode(filter.getMode().prev());
                sendFilterChange();
            } else if (levelB.mousePressed(mc, x, y)) {
                levelB.func_146113_a(mc.getSoundHandler());
                filter.setLevel((filter.getLevel() + PowerItemFilter.MAX_LEVEL) % (PowerItemFilter.MAX_LEVEL + 1));
                sendFilterChange();
            }
        }
    }

    @Override
    public void updateButtons() {
        if (!isInput) {
            stickyB.onGuiInit();
            stickyB.setSelected(filter.isSticky());
        }

        int x0 = gui.getGuiLeft() + (isInput ? 106 : 5);
        int y0 = gui.getGuiTop() + 96;
        int x1 = x0 + 45;

        modeB.xPosition = x0;
        modeB.yPosition = y0;

        levelB.xPosition = x1;
        levelB.yPosition = y0;

        switch (filter.getMode()) {
            case LESS:
                modeB.displayString = "<";
                break;
            case LESS_EQUAL:
                modeB.displayString = "<=";
                break;
            case EQUAL:
                modeB.displayString = "=";
                break;
            case MORE_EQUAL:
                modeB.displayString = ">=";
                break;
            case MORE:
                modeB.displayString = ">";
                break;
            default:
                modeB.displayString = "??";
                break;
        }

        levelB.displayString = String.format("%d%%", filter.getLevel() * 100 / PowerItemFilter.MAX_LEVEL);

        gui.addButton(modeB);
        gui.addButton(levelB);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if (guiButton == stickyB) {
            filter.setSticky(stickyB.isSelected());
            sendFilterChange();
        } else if (guiButton == modeB) {
            filter.setMode(filter.getMode().next());
            sendFilterChange();
        } else if (guiButton == levelB) {
            filter.setLevel((filter.getLevel() + 1) % (PowerItemFilter.MAX_LEVEL + 1));
            sendFilterChange();
        }
    }

    private void sendFilterChange() {
        updateButtons();
        PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    }

    @Override
    public void deactivate() {
        stickyB.detach();
        gui.removeButton(modeB);
        gui.removeButton(levelB);
    }

    @Override
    public void renderCustomOptions(int top, float par1, int par2, int par3) {
        //    GL11.glColor3f(1, 1, 1);
        //    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
        //    gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 68, 0, 238, 18 * 5, 18);
        //    if(filter.isAdvanced()) {
        //      gui.drawTexturedModalRect(gui.getGuiLeft() + 32, gui.getGuiTop() + 86, 0, 238, 18 * 5, 18);
        //    }
    }
}
