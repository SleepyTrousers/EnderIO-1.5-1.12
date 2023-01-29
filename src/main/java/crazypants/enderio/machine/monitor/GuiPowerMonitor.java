package crazypants.enderio.machine.monitor;

import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPower;
import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPowerFloat;

import java.awt.Color;
import java.awt.Rectangle;
import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class GuiPowerMonitor extends GuiContainerBaseEIO {

    private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

    private static final int ICON_SIZE = 16;

    private static final int SPACING = 6;

    private static final int MARGIN = 7;

    private static final int WIDTH = 210;
    private static final int HEIGHT = 146;

    private static final int POWER_X = 185;
    private static final int POWER_Y = 9;
    private static final int POWER_WIDTH = 10;
    private static final int POWER_HEIGHT = 130;
    protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

    private final TilePowerMonitor te;

    private boolean isRedstoneMode = false;

    private CheckBox enabledB;

    private TextFieldEnder startTF;
    private TextFieldEnder endTF;

    private String titleStr;

    private String engineTxt1;
    private String engineTxt2;
    private String engineTxt3;
    private String engineTxt4;
    private String engineTxt5;
    private String engineTxt6;

    private String monHeading1;
    private String monHeading2;
    private String monHeading3;
    private String monHeading4;
    private String monHeading5;

    private String noNetworkError;

    public GuiPowerMonitor(InventoryPlayer playerInv, final TilePowerMonitor te) {
        super(new ContainerNoInv(te), "powerMonitor");
        this.te = te;
        xSize = WIDTH;
        ySize = HEIGHT;

        titleStr = EnderIO.lang.localize("gui.powerMonitor.engineControl");
        engineTxt1 = EnderIO.lang.localize("gui.powerMonitor.engineSection1");
        engineTxt2 = EnderIO.lang.localize("gui.powerMonitor.engineSection2");
        engineTxt3 = EnderIO.lang.localize("gui.powerMonitor.engineSection3");
        engineTxt4 = EnderIO.lang.localize("gui.powerMonitor.engineSection4");
        engineTxt5 = EnderIO.lang.localize("gui.powerMonitor.engineSection5");
        engineTxt6 = EnderIO.lang.localize("gui.powerMonitor.engineSection6");

        monHeading1 = EnderIO.lang.localize("gui.powerMonitor.monHeading1");
        monHeading2 = EnderIO.lang.localize("gui.powerMonitor.monHeading2");
        monHeading3 = EnderIO.lang.localize("gui.powerMonitor.monHeading3");
        monHeading4 = EnderIO.lang.localize("gui.powerMonitor.monHeading4");
        monHeading5 = EnderIO.lang.localize("gui.powerMonitor.monHeading5");

        noNetworkError = EnderIO.lang.localize("gui.powerMonitor.noNetworkError");

        addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

            @Override
            protected void updateText() {
                text.clear();
                text.add(
                        formatPower(te.getEnergyStored()) + "/"
                                + formatPower(te.getMaxEnergyStored())
                                + " "
                                + PowerDisplayUtil.abrevation());
            }
        });

        int x = MARGIN + Minecraft.getMinecraft().fontRenderer.getStringWidth(titleStr) + SPACING;

        enabledB = new CheckBox(this, 21267, x, 8);
        enabledB.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"));
        enabledB.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));
        enabledB.setSelected(te.engineControlEnabled);

        x = MARGIN + getFontRenderer().getStringWidth(engineTxt2) + 4;
        int y = MARGIN + ICON_SIZE + ICON_SIZE + getFontRenderer().FONT_HEIGHT;
        startTF = new TextFieldEnder(getFontRenderer(), x, y, 28, 14);
        startTF.setCanLoseFocus(true);
        startTF.setMaxStringLength(3);
        startTF.setVisible(false);
        startTF.setText(INT_NF.format(te.asPercentInt(te.startLevel)));

        y = y + getFontRenderer().FONT_HEIGHT + ICON_SIZE + ICON_SIZE + 4;
        x = 5 + MARGIN + getFontRenderer().getStringWidth(engineTxt5);
        endTF = new TextFieldEnder(getFontRenderer(), x, y, 28, 14);
        endTF.setCanLoseFocus(true);
        endTF.setMaxStringLength(3);
        endTF.setVisible(false);
        endTF.setText(INT_NF.format(te.asPercentInt(te.stopLevel)));

        textFields.add(startTF);
        textFields.add(endTF);
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();
        if (isRedstoneMode) {
            enabledB.onGuiInit();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public int getOverlayOffsetX() {
        return 0;
    }

    @Override
    protected void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);

        x = (x - guiLeft);
        y = (y - guiTop);
        if (x > 200 && x < 220) {
            if (y >= SPACING && y < 30) {
                isRedstoneMode = false;
                enabledB.detach();
                startTF.setVisible(false);
                endTF.setVisible(false);
            } else if (y >= 30 + SPACING && y < 60) {
                isRedstoneMode = true;
                enabledB.onGuiInit();
                startTF.setVisible(true);
                endTF.setVisible(true);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ptick, int mouseX, int mouseY) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;

        drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

        int i1 = te.getEnergyStoredScaled(POWER_HEIGHT);
        drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 245, 0, POWER_WIDTH, i1);

        renderRedstoneTab(sx, sy);
        renderInfoTab(sx, sy);

        checkForModifications();
        super.drawGuiContainerBackgroundLayer(ptick, mouseX, mouseY);
    }

    private void checkForModifications() {
        if (enabledB.isSelected() != te.engineControlEnabled || getInt(startTF) != te.asPercentInt(te.startLevel)
                || getInt(endTF) != te.asPercentInt(te.stopLevel)) {

            te.engineControlEnabled = enabledB.isSelected();
            int i = getInt(startTF);
            if (i >= 0) {
                te.startLevel = te.asPercentFloat(i);
            }
            i = getInt(endTF);
            if (i >= 0) {
                te.stopLevel = te.asPercentFloat(i);
            }
            PacketHandler.INSTANCE.sendToServer(new PacketPowerMonitor(te));
        }
    }

    private int getInt(GuiTextField tf) {
        String txt = tf.getText();
        if (txt == null) {
            return -1;
        }
        try {
            int val = Integer.parseInt(tf.getText());
            if (val >= 0 && val <= 100) {
                return val;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private void renderRedstoneTab(int sx, int sy) {
        if (isRedstoneMode) {
            IconEIO.map.render(IconEIO.ACTIVE_TAB, sx + 200, sy + 30 + SPACING, true);
            itemRender.renderItemIntoGUI(
                    fontRendererObj,
                    mc.renderEngine,
                    new ItemStack(Items.redstone),
                    sx + 201,
                    sy + 30 + SPACING + 3);

            GL11.glDisable(GL11.GL_LIGHTING);

            int left = guiLeft + MARGIN;
            int rgb;
            int x = left;
            int y = guiTop + MARGIN + SPACING;
            if (!enabledB.isSelected()) {
                rgb = ColorUtil.getRGB(Color.darkGray);
            } else {
                rgb = ColorUtil.getRGB(Color.black);
            }
            FontRenderer fontRenderer = getFontRenderer();
            fontRenderer.drawString(titleStr, x, y, rgb, false);

            x = left + fontRenderer.getStringWidth(titleStr) + SPACING + ICON_SIZE + SPACING;
            y = guiTop + 14;
            if (!enabledB.isSelected()) {
                rgb = ColorUtil.getRGB(Color.darkGray);
                enabledB.drawButton(mc, guiLeft, guiTop);
            } else {
                // rgb = ColorUtil.getRGB(Color.blue);
                // rgb = ColorUtil.getRGB(0, 18, 127);
                rgb = ColorUtil.getRGB(Color.black);
            }

            enabledB.drawButton(mc, guiLeft, guiTop);

            y += SPACING + ICON_SIZE;
            x = left;

            String txt = engineTxt1;
            fontRenderer.drawString(txt, x, y, rgb, false);

            y += SPACING + fontRenderer.FONT_HEIGHT;

            x = left;
            txt = engineTxt2;
            fontRenderer.drawString(txt, x, y, rgb, false);

            x = left + fontRenderer.getStringWidth(txt) + SPACING + startTF.getWidth() + 12;
            txt = engineTxt3;
            fontRenderer.drawString(txt, x, y, rgb, false);

            x = left;
            y += ICON_SIZE + fontRenderer.FONT_HEIGHT + SPACING;
            txt = engineTxt4;
            fontRenderer.drawString(txt, x, y, rgb, false);

            x = left;
            y += SPACING + fontRenderer.FONT_HEIGHT;
            txt = engineTxt5;
            fontRenderer.drawString(txt, x, y, rgb, false);
            x += fontRenderer.getStringWidth(txt);

            txt = engineTxt3;
            x += MARGIN + endTF.getWidth() + 10;
            fontRenderer.drawString(txt, x, y, rgb, false);
        } else {
            IconEIO.map.render(IconEIO.INACTIVE_TAB, sx + 200, sy + 30 + SPACING, true);
            itemRender.renderItemIntoGUI(
                    fontRendererObj,
                    mc.renderEngine,
                    new ItemStack(Items.redstone),
                    sx + 201,
                    sy + 30 + SPACING + 3);
        }
    }

    private void renderInfoTab(int sx, int sy) {
        if (!isRedstoneMode) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            IconEIO.map.render(IconEIO.ACTIVE_TAB, sx + 200, sy + SPACING, true);

            int headingCol = ColorUtil.getRGB(Color.white);
            int valuesCol = ColorUtil.getRGB(Color.black);
            int rgb;
            int x = guiLeft + MARGIN;
            int y = guiTop + MARGIN;

            int sectionGap = SPACING;

            FontRenderer fontRenderer = getFontRenderer();
            if (te.maxPowerInConduits == 0) {
                fontRenderer.drawSplitString(noNetworkError, x, y, 170, ColorUtil.getRGB(Color.red));
                return;
            }

            rgb = headingCol;
            StringBuilder sb = new StringBuilder();
            sb.append(monHeading1);
            fontRenderer.drawString(sb.toString(), x, y, rgb, true);

            rgb = valuesCol;
            y += fontRenderer.FONT_HEIGHT + 2;
            sb = new StringBuilder();
            sb.append(formatPower(te.powerInConduits));
            sb.append(" ");
            sb.append(PowerDisplayUtil.ofStr());
            sb.append(" ");
            sb.append(formatPower(te.maxPowerInConduits));
            sb.append(" ");
            sb.append(PowerDisplayUtil.abrevation());
            fontRenderer.drawString(sb.toString(), x, y, rgb, false);

            rgb = headingCol;
            y += fontRenderer.FONT_HEIGHT + sectionGap;
            sb = new StringBuilder();
            sb.append(monHeading2);
            fontRenderer.drawString(sb.toString(), x, y, rgb, true);

            rgb = valuesCol;
            y += fontRenderer.FONT_HEIGHT + 2;
            sb = new StringBuilder();
            sb.append(formatPower(te.powerInCapBanks));
            sb.append(" ");
            sb.append(PowerDisplayUtil.ofStr());
            sb.append(" ");
            sb.append(formatPower(te.maxPowerInCapBanks));
            sb.append(" ");
            sb.append(PowerDisplayUtil.abrevation());
            fontRenderer.drawString(sb.toString(), x, y, rgb, false);

            rgb = headingCol;
            y += fontRenderer.FONT_HEIGHT + sectionGap;
            sb = new StringBuilder();
            sb.append(monHeading3);
            fontRenderer.drawString(sb.toString(), x, y, rgb, true);

            rgb = valuesCol;
            y += fontRenderer.FONT_HEIGHT + 2;
            sb = new StringBuilder();
            sb.append(formatPower(te.powerInMachines));
            sb.append(" ");
            sb.append(PowerDisplayUtil.ofStr());
            sb.append(" ");
            sb.append(formatPower(te.maxPowerInMachines));
            sb.append(" ");
            sb.append(PowerDisplayUtil.abrevation());
            fontRenderer.drawString(sb.toString(), x, y, rgb, false);

            rgb = headingCol;
            y += fontRenderer.FONT_HEIGHT + sectionGap;
            sb = new StringBuilder();
            sb.append(monHeading4);
            fontRenderer.drawString(sb.toString(), x, y, rgb, true);

            rgb = valuesCol;
            y += fontRenderer.FONT_HEIGHT + 2;
            sb = new StringBuilder();
            sb.append(formatPowerFloat(te.aveRfSent));
            sb.append(" ");
            sb.append(PowerDisplayUtil.abrevation());
            sb.append(PowerDisplayUtil.perTickStr());
            fontRenderer.drawString(sb.toString(), x, y, rgb, false);

            rgb = headingCol;
            y += fontRenderer.FONT_HEIGHT + sectionGap;
            sb = new StringBuilder();
            sb.append(monHeading5);
            fontRenderer.drawString(sb.toString(), x, y, rgb, true);

            rgb = valuesCol;
            y += fontRenderer.FONT_HEIGHT + 2;
            sb = new StringBuilder();
            sb.append(formatPowerFloat(te.aveRfReceived));
            sb.append(" ");
            sb.append(PowerDisplayUtil.abrevation());
            sb.append(PowerDisplayUtil.perTickStr());
            fontRenderer.drawString(sb.toString(), x, y, rgb, false);
        } else {
            IconEIO.map.render(IconEIO.INACTIVE_TAB, sx + 200, sy + SPACING, true);
        }
    }
}
