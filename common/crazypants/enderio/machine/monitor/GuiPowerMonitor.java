package crazypants.enderio.machine.monitor;

import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPower;
import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPowerFloat;

import java.awt.Color;
import java.awt.Rectangle;
import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.gui.CheckBoxEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiScreenBase;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiPowerMonitor extends GuiScreenBase {

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  private static final int ICON_SIZE = 16;

  private static final int SPACING = 6;

  private static final int MARGIN = 7;

  private static final int WIDTH = 203;
  private static final int HEIGHT = 146;

  private static final int POWER_X = 185;
  private static final int POWER_Y = 9;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 130;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private final TilePowerMonitor te;

  private boolean isRedstoneMode = false;

  private CheckBoxEIO enabledB;

  private GuiTextField startTF;

  private GuiTextField endTF;

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

  public GuiPowerMonitor(final TilePowerMonitor te) {
    super(WIDTH, HEIGHT);
    this.te = te;
    drawButtons = false;

    titleStr = Lang.localize("gui.powerMonitor.engineControl");
    engineTxt1 = Lang.localize("gui.powerMonitor.engineSection1");
    engineTxt2 = Lang.localize("gui.powerMonitor.engineSection2");
    engineTxt3 = Lang.localize("gui.powerMonitor.engineSection3");
    engineTxt4 = Lang.localize("gui.powerMonitor.engineSection4");
    engineTxt5 = Lang.localize("gui.powerMonitor.engineSection5");
    engineTxt6 = Lang.localize("gui.powerMonitor.engineSection6");

    monHeading1 = Lang.localize("gui.powerMonitor.monHeading1");
    monHeading2 = Lang.localize("gui.powerMonitor.monHeading2");
    monHeading3 = Lang.localize("gui.powerMonitor.monHeading3");
    monHeading4 = Lang.localize("gui.powerMonitor.monHeading4");
    monHeading5 = Lang.localize("gui.powerMonitor.monHeading5");

    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(formatPower(te.getEnergyStored()) + "/" + formatPower(te.getPowerHandler().getMaxEnergyStored()) + " "
            + PowerDisplayUtil.abrevation());
      }

    });

    fontRenderer = Minecraft.getMinecraft().fontRenderer;
    int x = MARGIN + fontRenderer.getStringWidth(titleStr) + SPACING;

    enabledB = new CheckBoxEIO(this, 21267, x, 8);
    enabledB.setSelectedToolTip(Lang.localize("enderio.gui.enabled"));
    enabledB.setUnselectedToolTip(Lang.localize("enderio.gui.disabled"));
    enabledB.setSelected(te.engineControlEnabled);

  }

  @Override
  public void initGui() {
    super.initGui();

    buttonList.clear();
    enabledB.onGuiInit();

    int x = guiLeft + MARGIN + fontRenderer.getStringWidth(engineTxt2) + 4;
    int y = guiTop + MARGIN + ICON_SIZE + ICON_SIZE + fontRenderer.FONT_HEIGHT;
    startTF = new GuiTextField(fontRenderer, x, y, 28, 14);
    startTF.setCanLoseFocus(true);
    startTF.setMaxStringLength(3);
    startTF.setFocused(true);
    startTF.setText(INT_NF.format(te.asPercentInt(te.startLevel)));

    y = y + fontRenderer.FONT_HEIGHT + ICON_SIZE + ICON_SIZE + 4;
    x = guiLeft + MARGIN + fontRenderer.getStringWidth(engineTxt5);
    endTF = new GuiTextField(fontRenderer, x, y, 28, 14);
    endTF.setCanLoseFocus(true);
    endTF.setMaxStringLength(3);
    endTF.setFocused(false);
    endTF.setText(INT_NF.format(te.asPercentInt(te.stopLevel)));

  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    startTF.textboxKeyTyped(par1, par2);
    endTF.textboxKeyTyped(par1, par2);

  }

  @Override
  public void updateScreen() {
    startTF.updateCursorCounter();
    endTF.updateCursorCounter();
  }

  @Override
  protected void mouseClicked(int x, int y, int par3) {
    super.mouseClicked(x, y, par3);

    startTF.mouseClicked(x, y, par3);
    endTF.mouseClicked(x, y, par3);

    x = (x - guiLeft);
    y = (y - guiTop);
    if(x > 200 && x < 220) {
      if(y > 9 && y < 27) {
        isRedstoneMode = false;
      } else if(y > 34 && y < 53) {
        isRedstoneMode = true;
      }
    }

  }

  @Override
  protected void drawBackgroundLayer(float par3, int par1, int par2) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/powerMonitor.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int i1 = te.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 245, 0, POWER_WIDTH, i1);

    if(isRedstoneMode) {
      renderRedstoneTab(sx, sy);
    } else {
      renderInfoTab(sx, sy);
    }

    checkForModifications();

  }

  private void checkForModifications() {
    if(enabledB.isSelected() != te.engineControlEnabled ||
        getInt(startTF) != te.asPercentInt(te.startLevel) ||
        getInt(endTF) != te.asPercentInt(te.stopLevel)) {

      te.engineControlEnabled = enabledB.isSelected();
      int i = getInt(startTF);
      if(i >= 0) {
        te.startLevel = te.asPercentFloat(i);
      }
      i = getInt(endTF);
      if(i >= 0) {
        te.stopLevel = te.asPercentFloat(i);
      }
      Packet pkt = PowerMonitorPacketHandler.createPowerMonitotPacket(te);
      PacketDispatcher.sendPacketToServer(pkt);
    }

  }

  private int getInt(GuiTextField tf) {
    String txt = tf.getText();
    if(txt == null) {
      return -1;
    }
    try {
      int val = Integer.parseInt(tf.getText());
      if(val >= 0 && val <= 100) {
        return val;
      }
      return -1;
    } catch (Exception e) {
      return -1;
    }
  }

  private void renderRedstoneTab(int sx, int sy) {
    drawTexturedModalRect(sx + 200, sy + SPACING, 225, 0, 20, 48);
    int left = guiLeft + MARGIN;
    int rgb;
    int x = left;
    int y = guiTop + MARGIN + SPACING;
    if(!enabledB.isSelected()) {
      rgb = ColorUtil.getRGB(Color.darkGray);
    } else {
      rgb = ColorUtil.getRGB(Color.black);
    }
    fontRenderer.drawString(titleStr, x, y, rgb, false);

    x = left + fontRenderer.getStringWidth(titleStr) + SPACING + ICON_SIZE + SPACING;
    y = guiTop + 14;
    if(!enabledB.isSelected()) {
      rgb = ColorUtil.getRGB(Color.darkGray);
      enabledB.drawButton(mc, guiLeft, guiTop);
    } else {
      //      rgb = ColorUtil.getRGB(Color.blue);
      //      rgb = ColorUtil.getRGB(0, 18, 127);
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

    startTF.drawTextBox();
    endTF.drawTextBox();

  }

  private void renderInfoTab(int sx, int sy) {
    drawTexturedModalRect(sx + 200, sy + SPACING, 225, 53, 20, 48);

    //    int headingCol = ColorUtil.getRGB(Color.black);
    //    int valuesCol = ColorUtil.getRGB(Color.white);
    int headingCol = ColorUtil.getRGB(Color.white);
    int valuesCol = ColorUtil.getRGB(Color.black);
    int rgb;
    int x = guiLeft + MARGIN;
    int y = guiTop + MARGIN;

    int sectionGap = SPACING;

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
    sb.append(formatPower(te.maxPowerInCoduits));
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
    sb.append(formatPowerFloat(te.aveMjSent));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append(PowerDisplayUtil.ofStr());
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = headingCol;
    y += fontRenderer.FONT_HEIGHT + sectionGap;
    sb = new StringBuilder();
    sb.append(monHeading5);
    fontRenderer.drawString(sb.toString(), x, y, rgb, true);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(formatPowerFloat(te.aveMjRecieved));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append(PowerDisplayUtil.perTickStr());
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);
  }

}
