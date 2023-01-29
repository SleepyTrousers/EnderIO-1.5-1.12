package crazypants.enderio.teleport.telepad;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class GuiTelePad extends GuiContainerBaseEIO implements IToggleableGui {

    private static final int ID_SWITCH_BUTTON = 95;
    private static final int ID_TELEPORT_BUTTON = 96;

    ToggleTravelButton switchButton;
    GuiButton teleportButton;

    private World world;
    private TileTelePad te;

    private TextFieldEnder xTF, yTF, zTF, dimTF;

    private int powerX = 8;
    private int powerY = 9;
    private int powerScale = 100;

    private int progressX = 26;
    private int progressY = 90;
    private int progressScale = 124;

    public static int SWITCH_X = 155, SWITCH_Y = 5;

    public GuiTelePad(InventoryPlayer playerInv, TileTelePad te, World world) {
        super(new ContainerTelePad(playerInv), "telePad");
        this.world = world;
        this.te = te;
        ySize += 34;

        addToolTip(new GuiToolTip(new Rectangle(powerX, powerY, 10, powerScale), "") {

            @Override
            protected void updateText() {
                text.clear();
                updatePowerBarTooltip(text);
            }
        });

        addToolTip(new GuiToolTip(new Rectangle(progressX, progressY, progressScale, 10), "") {

            @Override
            protected void updateText() {
                text.clear();
                text.add(Math.round(GuiTelePad.this.te.getProgress() * 100) + "%");
            }
        });

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        int x = 42;
        int y = 8;
        xTF = new TextFieldEnder(fr, x, y, xSize - x * 2, 12, TextFieldEnder.FILTER_NUMERIC);
        yTF = new TextFieldEnder(fr, x, y + xTF.height + 2, xSize - x * 2, 12, TextFieldEnder.FILTER_NUMERIC);
        zTF = new TextFieldEnder(fr, x, y + (xTF.height * 2) + 4, xSize - x * 2, 12, TextFieldEnder.FILTER_NUMERIC);
        dimTF = new TextFieldEnder(fr, x, y + (xTF.height * 3) + 6, xSize - x * 2, 12, TextFieldEnder.FILTER_NUMERIC);

        xTF.setText(Integer.toString(te.getX()));
        yTF.setText(Integer.toString(te.getY()));
        zTF.setText(Integer.toString(te.getZ()));
        dimTF.setText(Integer.toString(te.getTargetDim()));

        xTF.setCanLoseFocus(!Config.telepadLockCoords);
        yTF.setCanLoseFocus(!Config.telepadLockCoords);
        zTF.setCanLoseFocus(!Config.telepadLockCoords);
        dimTF.setCanLoseFocus(!Config.telepadLockDimension);

        textFields.addAll(Lists.newArrayList(xTF, yTF, zTF, dimTF));

        switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, SWITCH_X, SWITCH_Y, IconEIO.IO_WHATSIT);
        switchButton.setToolTip(EnderIO.lang.localize("gui.telepad.configure.travel"));
    }

    private String getPowerOutputLabel() {
        return StatCollector.translateToLocal("enderio.gui.max");
    }

    protected int getPowerOutputValue() {
        return te.getUsage();
    }

    protected void updatePowerBarTooltip(List<String> text) {
        text.add(
                getPowerOutputLabel() + " "
                        + PowerDisplayUtil.formatPower(getPowerOutputValue())
                        + " "
                        + PowerDisplayUtil.abrevation()
                        + PowerDisplayUtil.perTickStr());
        text.add(PowerDisplayUtil.formatStoredPower(te.getEnergyStored(), te.getMaxEnergyStored()));
    }

    @Override
    public void initGui() {
        super.initGui();
        switchButton.onGuiInit();

        String text = EnderIO.lang.localize("gui.telepad.teleport");
        int width = getFontRenderer().getStringWidth(text) + 10;

        int x = guiLeft + (xSize / 2) - (width / 2);
        int y = guiTop + 65;

        teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, width, 20, text);
        addButton(teleportButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        updateCoords();
    }

    private void updateCoords() {
        BlockCoord bc = new BlockCoord(getIntFromTextBox(xTF), getIntFromTextBox(yTF), getIntFromTextBox(zTF));
        int targetDim = getIntFromTextBox(dimTF);
        if (bc.x != te.getX() || bc.y != te.getY() || bc.z != te.getZ() || targetDim != te.getTargetDim()) {
            te.setX(bc.x);
            te.setY(bc.y);
            te.setZ(bc.z);
            te.setTargetDim(targetDim);
            PacketHandler.INSTANCE.sendToServer(new PacketUpdateCoords(te, bc, targetDim));
        }
    }

    private int getIntFromTextBox(TextFieldEnder tf) {
        String text = tf.getText();
        if ("".equals(text) || "-".equals(text)) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

        int powerScaled = te.getPowerScaled(powerScale);
        drawTexturedModalRect(sx + powerX, sy + powerY + powerScale - powerScaled, xSize, 0, 10, powerScaled);
        int progressScaled = Util.getProgressScaled(progressScale, te);
        drawTexturedModalRect(sx + progressX, sy + progressY, 0, ySize, progressScaled, 10);

        FontRenderer fnt = getFontRenderer();

        String[] text = { "X", "Y", "Z", "DIM" };
        for (int i = 0; i < text.length; i++) {
            TextFieldEnder f = textFields.get(i);
            fnt.drawString(
                    text[i],
                    f.xPosition - (fnt.getStringWidth(text[i]) / 2) - 10,
                    f.yPosition + ((f.height - fnt.FONT_HEIGHT) / 2) + 1,
                    0x000000);
            if (!f.getCanLoseFocus()) {
                IconEIO.map.render(IconEIO.LOCK_LOCKED, f.xPosition + f.width - 2, f.yPosition - 2, true);
            }
        }

        Entity e = te.getCurrentTarget();
        if (e != null) {
            String name = e.getCommandSenderName();
            fnt.drawString(
                    name,
                    sx + xSize / 2 - fnt.getStringWidth(name) / 2,
                    sy + progressY + fnt.FONT_HEIGHT + 6,
                    0x000000);
        } else if (te.wasBlocked) {
            String s = EnderIO.lang.localize("gui.telepad.blocked");
            fnt.drawString(
                    s,
                    sx + xSize / 2 - fnt.getStringWidth(s) / 2,
                    sy + progressY + fnt.FONT_HEIGHT + 6,
                    0xAA0000);
        }

        super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
    }

    @Override
    public void switchGui() {
        mc.thePlayer
                .openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD_TRAVEL, world, te.xCoord, te.yCoord, te.zCoord);
        PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiHandler.GUI_ID_TELEPAD_TRAVEL));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        if (button.id == ID_TELEPORT_BUTTON) {
            te.teleportAll();
        }
    }
}
