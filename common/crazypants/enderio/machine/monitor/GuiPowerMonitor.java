package crazypants.enderio.machine.monitor;

import java.awt.Color;
import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.conduit.redstone.SignalColor;
import crazypants.enderio.gui.CheckBoxEIO;
import crazypants.enderio.gui.ColorButton;
import crazypants.gui.GuiScreenBase;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class GuiPowerMonitor extends GuiScreenBase {

  private static final int ICON_SIZE = 16;

  private static final int SPACING = 6;

  private static final int MARGIN = 7;

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  private static final NumberFormat FLOAT_NF = NumberFormat.getInstance();
  static {
    FLOAT_NF.setMinimumFractionDigits(1);
    FLOAT_NF.setMaximumFractionDigits(1);
  }

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

  private ColorButton colorB;

  private GuiTextField startTF;

  private GuiTextField endTF;

  private String titleStr;

  private String emmitStr;

  public GuiPowerMonitor(TilePowerMonitor te) {
    super(WIDTH, HEIGHT);
    this.te = te;
    drawButtons = false;

    titleStr = "Engine Control";
    emmitStr = "  Emmit";

    fontRenderer = Minecraft.getMinecraft().fontRenderer;
    int x = MARGIN + fontRenderer.getStringWidth(titleStr) + SPACING;

    enabledB = new CheckBoxEIO(this, 21267, x, 8);
    enabledB.setSelectedToolTip("Enabled");
    enabledB.setUnselectedToolTip("Disabled");
    enabledB.setSelected(te.engineControlEnabled);

    x = MARGIN + fontRenderer.getStringWidth(emmitStr) + SPACING;
    int y = MARGIN + 26;
    colorB = new ColorButton(this, 22, x, y);
    colorB.setColorIndex(te.signalColor.ordinal());
    colorB.setSize(12, 12);

  }

  @Override
  public void initGui() {
    super.initGui();

    buttonList.clear();
    enabledB.onGuiInit();
    colorB.onGuiInit();

    int x = guiLeft + MARGIN + fontRenderer.getStringWidth("than") + 4;
    int y = guiTop + MARGIN + ICON_SIZE + ICON_SIZE + fontRenderer.FONT_HEIGHT;
    startTF = new GuiTextField(fontRenderer, x, y, 28, 14);
    startTF.setCanLoseFocus(true);
    startTF.setMaxStringLength(3);
    startTF.setFocused(true);
    startTF.setText(INT_NF.format(te.asPercentInt(te.startLevel)));

    y = y + fontRenderer.FONT_HEIGHT + ICON_SIZE + ICON_SIZE + 4;
    x = guiLeft + MARGIN + fontRenderer.getStringWidth("or equal to ");
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
        getInt(endTF) != te.asPercentInt(te.stopLevel) ||
        te.signalColor != SignalColor.fromIndex(colorB.getColorIndex())) {

      te.engineControlEnabled = enabledB.isSelected();
      te.startLevel = te.asPercentFloat(getInt(startTF));
      te.stopLevel = te.asPercentFloat(getInt(endTF));
      te.signalColor = SignalColor.fromIndex(colorB.getColorIndex());

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
      rgb = ColorUtil.getRGB(Color.black);
    } else {
      rgb = ColorUtil.getRGB(Color.blue);
    }
    fontRenderer.drawString(titleStr, x, y, rgb, false);

    x = left + fontRenderer.getStringWidth(titleStr) + SPACING + ICON_SIZE + SPACING;
    y = guiTop + 14;
    if(!enabledB.isSelected()) {
      rgb = ColorUtil.getRGB(Color.darkGray);
      enabledB.drawButton(mc, guiLeft, guiTop);
    } else {
      rgb = ColorUtil.getRGB(Color.blue);
      rgb = ColorUtil.getRGB(0, 18, 127);
    }

    enabledB.drawButton(mc, guiLeft, guiTop);
    //colorB.drawButton(mc, guiLeft, guiTop);

    y += SPACING + ICON_SIZE;
    x = left;

    String txt = emmitStr + " signal when storage less";
    fontRenderer.drawString(txt, x, y, rgb, false);
    //x += fontRenderer.getStringWidth(txt);
    //    x += 4 + ICON_SIZE + 4;
    //
    //    txt = "when storage less than";
    //    fontRenderer.drawString(txt, x, y, rgb, false);

    y += SPACING + fontRenderer.FONT_HEIGHT;

    x = left;
    txt = "than";
    fontRenderer.drawString(txt, x, y, rgb, false);

    x = left + fontRenderer.getStringWidth(txt) + SPACING + startTF.getWidth() + 12;
    txt = "% full.";
    fontRenderer.drawString(txt, x, y, rgb, false);

    x = left;
    y += ICON_SIZE + fontRenderer.FONT_HEIGHT + SPACING;
    txt = "  Stop when storage greater than";
    fontRenderer.drawString(txt, x, y, rgb, false);

    x = left;
    y += SPACING + fontRenderer.FONT_HEIGHT;
    txt = "or equal to";
    fontRenderer.drawString(txt, x, y, rgb, false);
    x += fontRenderer.getStringWidth(txt);

    txt = "% full.";
    x += MARGIN + endTF.getWidth() + 10;
    fontRenderer.drawString(txt, x, y, rgb, false);

    startTF.drawTextBox();
    endTF.drawTextBox();

  }

  private void renderInfoTab(int sx, int sy) {
    drawTexturedModalRect(sx + 200, sy + SPACING, 225, 53, 20, 48);

    int headingCol = ColorUtil.getRGB(Color.blue);
    int valuesCol = ColorUtil.getRGB(Color.white);
    int rgb;
    int x = guiLeft + MARGIN;
    int y = guiTop + MARGIN;

    int sectionGap = SPACING;

    rgb = headingCol;
    StringBuilder sb = new StringBuilder();
    sb.append("Conduit Storage");
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(INT_NF.format(te.powerInConduits));
    sb.append(" of ");
    sb.append(INT_NF.format(te.maxPowerInCoduits));
    sb.append(" MJ");
    drawString(fontRenderer, sb.toString(), x, y, rgb);

    rgb = headingCol;
    y += fontRenderer.FONT_HEIGHT + sectionGap;
    sb = new StringBuilder();
    sb.append("Capacitor Bank Storage");
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(INT_NF.format(te.powerInCapBanks));
    sb.append(" of ");
    sb.append(INT_NF.format(te.maxPowerInCapBanks));
    sb.append(" MJ");
    drawString(fontRenderer, sb.toString(), x, y, rgb);

    rgb = headingCol;
    y += fontRenderer.FONT_HEIGHT + sectionGap;
    sb = new StringBuilder();
    sb.append("Machine Buffers");
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(INT_NF.format(te.powerInMachines));
    sb.append(" of ");
    sb.append(INT_NF.format(te.maxPowerInMachines));
    sb.append(" MJ");
    drawString(fontRenderer, sb.toString(), x, y, rgb);

    rgb = headingCol;
    y += fontRenderer.FONT_HEIGHT + sectionGap;
    sb = new StringBuilder();
    sb.append("Average output over 5 seconds");
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(FLOAT_NF.format(te.aveMjSent));
    drawString(fontRenderer, sb.toString(), x, y, rgb);

    rgb = headingCol;
    y += fontRenderer.FONT_HEIGHT + sectionGap;
    sb = new StringBuilder();
    sb.append("Average input over 5 seconds");
    fontRenderer.drawString(sb.toString(), x, y, rgb, false);

    rgb = valuesCol;
    y += fontRenderer.FONT_HEIGHT + 2;
    sb = new StringBuilder();
    sb.append(FLOAT_NF.format(te.aveMjRecieved));
    drawString(fontRenderer, sb.toString(), x, y, rgb);
  }

}
