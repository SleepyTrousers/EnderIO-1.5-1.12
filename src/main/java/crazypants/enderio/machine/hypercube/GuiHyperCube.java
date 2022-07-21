package crazypants.enderio.machine.hypercube;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.ListSelectionListener;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiScrollableList;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.PlayerUtil;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiHyperCube extends GuiContainerBaseEIO {

    protected static final int POWER_INPUT_BUTTON_ID = 1;
    protected static final int POWER_OUTPUT_BUTTON_ID = 2;
    protected static final int ADD_BUTTON_ID = 3;
    protected static final int PRIVATE_BUTTON_ID = 4;

    protected static final int SELECT_PRIVATE_BUTTON_ID = 5;
    protected static final int SELECT_PUBLIC_BUTTON_ID = 6;

    protected static final int DELETE_PRIVATE_BUTTON_ID = 7;
    protected static final int DELETE_PUBLIC_BUTTON_ID = 8;

    protected static final int POWER_MODE_BUTTON_ID = 9;
    protected static final int FLUID_MODE_BUTTON_ID = 10;
    private static final int ITEM_MODE_BUTTON_ID = 11;

    private static final int POWER_X = 227;
    private static final int POWER_Y = 46;
    private static final int POWER_WIDTH = 10;
    private static final int POWER_HEIGHT = 66;
    protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

    private final TileHyperCube cube;

    private IconButton addButton;
    private ToggleButton privateButton;

    private TextFieldEnder newChannelTF;

    private GuiChannelList publicChannelList;
    private GuiChannelList privateChannelList;
    private ListSelectionListener<Channel> selectionListener;

    private IconButton selectPublicB;
    private IconButton deletePublicB;

    private IconButton selectPrivateB;
    private IconButton deletePrivateB;

    private IconButton powerB;
    private IconButton fluidB;
    private IconButton itemB;

    private RedstoneModeButton rsB;

    public GuiHyperCube(TileHyperCube te) {
        super(new ContainerHyperCube(), "hyperCube");
        this.cube = te;

        xSize = 245;
        ySize = 145;

        addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

            @Override
            protected void updateText() {
                text.clear();
                text.add(PowerDisplayUtil.formatPower(cube.getEnergyStored()) + " / "
                        + PowerDisplayUtil.formatPower(cube.getMaxEnergyStored()) + " "
                        + PowerDisplayUtil.abrevation());
            }
        });

        addButton = new IconButton(this, ADD_BUTTON_ID, 137, 12, IconEIO.PLUS);
        addButton.setToolTip(EnderIO.lang.localize("gui.trans.addChannel"));
        addButton.enabled = false;

        privateButton = new ToggleButton(this, PRIVATE_BUTTON_ID, 118, 12, IconEIO.LOCK_UNLOCKED, IconEIO.LOCK_LOCKED);
        privateButton.setSelectedToolTip(EnderIO.lang.localize("gui.trans.privateChannel"));
        privateButton.setUnselectedToolTip(EnderIO.lang.localize("gui.trans.publicChannel"));

        deletePublicB = new IconButton(this, DELETE_PUBLIC_BUTTON_ID, 74, 117, IconEIO.MINUS);
        deletePublicB.setToolTip(EnderIO.lang.localize("gui.trans.deleteChannel"));
        selectPublicB = new IconButton(this, SELECT_PUBLIC_BUTTON_ID, 95, 117, IconEIO.TICK);
        selectPublicB.setToolTip(EnderIO.lang.localize("gui.trans.activateChannel"));

        deletePrivateB = new IconButton(this, DELETE_PRIVATE_BUTTON_ID, 183, 117, IconEIO.MINUS);
        deletePrivateB.setToolTip(EnderIO.lang.localize("gui.trans.deleteChannel"));
        selectPrivateB = new IconButton(this, SELECT_PRIVATE_BUTTON_ID, 204, 117, IconEIO.TICK);
        selectPrivateB.setToolTip(EnderIO.lang.localize("gui.trans.activateChannel"));

        int x = 163;
        int y = 12;
        itemB = new IconButton(this, ITEM_MODE_BUTTON_ID, x, y, IconEIO.WRENCH_OVERLAY_ITEM);
        itemB.setIconMargin(3, 3);

        x += 18;
        powerB = new IconButton(this, POWER_MODE_BUTTON_ID, x, y, IconEIO.WRENCH_OVERLAY_POWER);
        powerB.setIconMargin(3, 3);

        x += 18;
        fluidB = new IconButton(this, FLUID_MODE_BUTTON_ID, x, y, IconEIO.WRENCH_OVERLAY_FLUID);
        fluidB.setIconMargin(3, 3);

        x += 24;
        rsB = new RedstoneModeButton(this, 99, x, y, te, new BlockCoord(te));

        updateIoButtons();

        int w = 104;
        int h = 68;
        x = 7;
        y = 45;

        Channel activeChannel = cube.getChannel();
        publicChannelList = new GuiChannelList(this, w, h, x, y);
        publicChannelList.setChannels(ClientChannelRegister.instance.getPublicChannels());
        publicChannelList.setShowSelectionBox(true);
        publicChannelList.setScrollButtonIds(100, 101);
        publicChannelList.setActiveChannel(isPublic(activeChannel) ? activeChannel : null);

        x = x + 5 + w;
        privateChannelList = new GuiChannelList(this, w, h, x, y);
        privateChannelList.setChannels(ClientChannelRegister.instance.getPrivateChannels());
        privateChannelList.setShowSelectionBox(true);
        privateChannelList.setScrollButtonIds(102, 103);
        privateChannelList.setActiveChannel(isPrivate(activeChannel) ? activeChannel : null);

        selectionListener = new ListSelectionListener<Channel>() {

            @Override
            public void selectionChanged(GuiScrollableList<Channel> list, int selectedIndex) {
                Channel selected = list.getSelectedElement();
                if (selected != null) {
                    GuiChannelList clear = list == publicChannelList ? privateChannelList : publicChannelList;
                    clear.setSelection(-1);
                }
            }
        };

        publicChannelList.addSelectionListener(selectionListener);
        privateChannelList.addSelectionListener(selectionListener);

        y = 12;
        x = 8;
        newChannelTF = new TextFieldEnder(Minecraft.getMinecraft().fontRenderer, x, y, 103, 16);
        newChannelTF.setCanLoseFocus(false);
        newChannelTF.setMaxStringLength(32);
        newChannelTF.setFocused(true);
        textFields.add(newChannelTF);
    }

    private void updateIoButtons() {
        IoMode mode = cube.getModeForChannel(SubChannel.POWER);
        if (mode.isRecieveEnabled() || mode.isSendEnabled()) {
            powerB.setIcon(IconEIO.WRENCH_OVERLAY_POWER);
        } else {
            powerB.setIcon(IconEIO.WRENCH_OVERLAY_POWER_OFF);
        }
        powerB.setToolTip(EnderIO.lang.localize("gui.trans.powerMode"), mode.getLocalisedName());

        mode = cube.getModeForChannel(SubChannel.FLUID);
        if (mode.isRecieveEnabled() || mode.isSendEnabled()) {
            fluidB.setIcon(IconEIO.WRENCH_OVERLAY_FLUID);
        } else {
            fluidB.setIcon(IconEIO.WRENCH_OVERLAY_FLUID_OFF);
        }
        fluidB.setToolTip(EnderIO.lang.localize("gui.trans.fluidMode"), mode.getLocalisedName());

        mode = cube.getModeForChannel(SubChannel.ITEM);
        if (mode.isRecieveEnabled() || mode.isSendEnabled()) {
            itemB.setIcon(IconEIO.WRENCH_OVERLAY_ITEM);
        } else {
            itemB.setIcon(IconEIO.WRENCH_OVERLAY_ITEM_OFF);
        }
        itemB.setToolTip(EnderIO.lang.localize("gui.trans.itemMode"), mode.getLocalisedName());
    }

    private boolean isPublic(Channel chan) {
        if (chan == null) {
            return false;
        }
        return chan.isPublic();
    }

    private boolean isPrivate(Channel chan) {
        if (chan == null) {
            return false;
        }
        return !chan.isPublic();
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();

        privateButton.onGuiInit();
        addButton.onGuiInit();
        selectPrivateB.onGuiInit();
        selectPublicB.onGuiInit();
        deletePrivateB.onGuiInit();
        deletePublicB.onGuiInit();

        powerB.onGuiInit();
        fluidB.onGuiInit();
        itemB.onGuiInit();

        publicChannelList.onGuiInit(this);
        privateChannelList.onGuiInit(this);

        rsB.onGuiInit();
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {

        if (par1GuiButton.id == FLUID_MODE_BUTTON_ID) {

            IoMode curMode = cube.getModeForChannel(SubChannel.FLUID);
            IoMode nextMode = curMode.next();
            cube.setModeForChannel(SubChannel.FLUID, nextMode);

            updateIoButtons();
            PacketHandler.INSTANCE.sendToServer(new PacketClientState(cube));

        } else if (par1GuiButton.id == POWER_MODE_BUTTON_ID) {

            IoMode curMode = cube.getModeForChannel(SubChannel.POWER);
            IoMode nextMode = curMode.next();
            cube.setModeForChannel(SubChannel.POWER, nextMode);

            updateIoButtons();
            PacketHandler.INSTANCE.sendToServer(new PacketClientState(cube));

        } else if (par1GuiButton.id == ITEM_MODE_BUTTON_ID) {

            IoMode curMode = cube.getModeForChannel(SubChannel.ITEM);
            IoMode nextMode = curMode.next();
            cube.setModeForChannel(SubChannel.ITEM, nextMode);

            updateIoButtons();
            PacketHandler.INSTANCE.sendToServer(new PacketClientState(cube));

        } else if (par1GuiButton.id == ADD_BUTTON_ID) {

            Channel c;
            if (privateButton.isSelected()) {
                c = new Channel(
                        newChannelTF.getText(),
                        PlayerUtil.getPlayerUUID(Minecraft.getMinecraft()
                                .thePlayer
                                .getGameProfile()
                                .getName()));
            } else {
                c = new Channel(newChannelTF.getText(), null);
            }
            ClientChannelRegister.instance.addChannel(c);
            PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveChannel(true, c));
            setActiveChannel(c);

            if (privateButton.isSelected()) {
                privateChannelList.setSelection(c);
            } else {
                publicChannelList.setSelection(c);
            }

        } else if (par1GuiButton.id == SELECT_PUBLIC_BUTTON_ID || par1GuiButton.id == SELECT_PRIVATE_BUTTON_ID) {
            Channel c = (par1GuiButton.id == SELECT_PUBLIC_BUTTON_ID)
                    ? publicChannelList.getSelectedElement()
                    : privateChannelList.getSelectedElement();
            if (c != null) {
                setActiveChannel(c);
            }
        } else if (par1GuiButton.id == DELETE_PRIVATE_BUTTON_ID || par1GuiButton.id == DELETE_PUBLIC_BUTTON_ID) {
            Channel c = (par1GuiButton.id == DELETE_PUBLIC_BUTTON_ID)
                    ? publicChannelList.getSelectedElement()
                    : privateChannelList.getSelectedElement();
            if (c != null) {
                if (c.equals(cube.getChannel())) {
                    setActiveChannel(null);
                }
                ClientChannelRegister.instance.channelRemoved(c);
                PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveChannel(false, c));
            }
        }
    }

    private void setActiveChannel(Channel c) {
        cube.setChannel(c);
        publicChannelList.setActiveChannel(isPublic(c) ? c : null);
        privateChannelList.setActiveChannel(isPrivate(c) ? c : null);

        PacketHandler.INSTANCE.sendToServer(new PacketClientState(cube));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        if (par2 == 1) {
            for (IGuiOverlay overlay : overlays) {
                if (overlay.isVisible()) {
                    overlay.setVisible(false);
                    return;
                }
            }
            this.mc.thePlayer.closeScreen();
        }
        addButton.enabled = newChannelTF.getText().trim().length() > 0;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

        int i1 = cube.getEnergyStoredScaled(POWER_HEIGHT);
        drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 245, 0, POWER_WIDTH, i1);

        boolean chanSel = publicChannelList.getSelectedElement() != null;
        selectPublicB.enabled = chanSel;
        deletePublicB.enabled = chanSel;

        chanSel = privateChannelList.getSelectedElement() != null;
        selectPrivateB.enabled = chanSel;
        deletePrivateB.enabled = chanSel;

        publicChannelList.drawScreen(mouseX, mouseY, partialTick);
        privateChannelList.drawScreen(mouseX, mouseY, partialTick);

        for (int i = 0; i < buttonList.size(); ++i) {
            GuiButton guibutton = (GuiButton) this.buttonList.get(i);
            guibutton.drawButton(this.mc, 0, 0);
        }

        int x = guiLeft + 12;
        int y = guiTop + 35;
        int rgb = ColorUtil.getRGB(Color.white);
        drawString(getFontRenderer(), EnderIO.lang.localize("gui.trans.publicHeading"), x, y, rgb);

        x += 109;
        drawString(getFontRenderer(), EnderIO.lang.localize("gui.trans.privateHeading"), x, y, rgb);

        IoMode fluidMode = cube.getModeForChannel(SubChannel.FLUID);
        IoMode powerMode = cube.getModeForChannel(SubChannel.POWER);
        IoMode itemMode = cube.getModeForChannel(SubChannel.ITEM);

        x = 163;
        if (itemMode.isRecieveEnabled()) {
            IconEIO.map.render(IconEIO.INPUT_OLD, guiLeft + x + 15, guiTop + 4 + 7, -15, -7, 0, true);
        }
        x += 18;
        if (powerMode.isRecieveEnabled()) {
            IconEIO.map.render(IconEIO.INPUT_OLD, guiLeft + x + 15, guiTop + 4 + 7, -15, -7, 0, true);
        }
        x += 18;
        if (fluidMode.isRecieveEnabled()) {
            IconEIO.map.render(IconEIO.INPUT_OLD, guiLeft + x + 15, guiTop + 4 + 7, -15, -7, 0, true);
        }

        x = 163;
        if (itemMode.isSendEnabled()) {
            IconEIO.map.render(IconEIO.OUTPUT_OLD, guiLeft + x, guiTop + 29, 15, 7, 0, true);
        }
        x += 18;
        if (powerMode.isSendEnabled()) {
            IconEIO.map.render(IconEIO.OUTPUT_OLD, guiLeft + x, guiTop + 29, 15, 7, 0, true);
        }
        x += 18;
        if (fluidMode.isSendEnabled()) {
            IconEIO.map.render(IconEIO.OUTPUT_OLD, guiLeft + x, guiTop + 29, 15, 7, 0, true);
        }
    }

    @Override
    public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        super.drawHoveringText(par1List, par2, par3, font);
        GL11.glPopAttrib();
        GL11.glPopAttrib();
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
    public FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public int getOverlayOffsetX() {
        return 0;
    }
}
