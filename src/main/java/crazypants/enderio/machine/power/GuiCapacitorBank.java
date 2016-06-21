package crazypants.enderio.machine.power;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;

public class GuiCapacitorBank extends GuiContainerBaseEIO {

  protected static final int INPUT_BUTTON_ID = 18;
  protected static final int OUTPUT_BUTTON_ID = 37;

  protected static final int CONFIG_ID = 377996104;

  private static final int POWER_X = 11 + 18;
  private static final int POWER_Y = 9;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 68;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private int inputX = 78 + 24;
  private int inputY = 18;

  private int outputX = 78 + 24;
  private int outputY = 36;

  private int rightMargin = 8 + 24;

  private final TileCapacitorBank capBank;

  private RedstoneModeButton inputRsButton;
  private RedstoneModeButton outputRsButton;

  private GuiTextField maxInputTF;
  private GuiTextField maxOutputTF;

  private GuiOverlayIoConfig configOverlay;
  private IconButton configB;

  public GuiCapacitorBank(Entity player, InventoryPlayer playerInv, TileCapacitorBank te) {
    super(new ContainerCapacitorBank(player, playerInv, te), "capacitorBank");
    this.capBank = te;

    xSize = 176 + 42;

    addToolTip(new GuiToolTip(new Rectangle(5, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(PowerDisplayUtil.formatPower(capBank.getEnergyStored()) + " " + PowerDisplayUtil.ofStr());
        text.add(PowerDisplayUtil.formatPower(capBank.getMaxEnergyStored()) + " " + PowerDisplayUtil.abrevation());
      }

    });

    int x = xSize - rightMargin - GuiPoweredMachineBase.BUTTON_SIZE - 21;
    int y = inputY;
    inputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        capBank.setInputControlMode(mode);
        PacketHandler.INSTANCE.sendToServer(new PacketClientState(capBank));
      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return capBank.getInputControlMode();
      }
    });
    inputRsButton.setTooltipKey("enderio.gui.capBank.inputRs");

    y += 18;
    outputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        capBank.setOutputControlMode(mode);
        PacketHandler.INSTANCE.sendToServer(new PacketClientState(capBank));
      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return capBank.getOutputControlMode();
      }
    });
    outputRsButton.setTooltipKey("enderio.gui.capBank.outputRs");

    y += 20;
    configB = new IconButton(this, CONFIG_ID, x, y, IconEIO.IO_CONFIG_UP);
    configB.setToolTip(EnderIO.lang.localize("gui.machine.ioMode.overlay.tooltip"));

    List<BlockCoord> coords = new ArrayList<BlockCoord>();
    if(te.isMultiblock()) {
      BlockCoord[] mb = te.getController().multiblock;
      if(mb != null) {
        coords.addAll(Arrays.asList(mb));
      }
    }

    if(coords.isEmpty()) {
      coords.add(te.getLocation());
    }

    configOverlay = new GuiOverlayIoConfig(coords) {

      @Override
      public void setVisible(boolean visible) {
        super.setVisible(visible);
        configB.setIcon(visible ? IconEIO.IO_CONFIG_DOWN : IconEIO.IO_CONFIG_UP);
      }

      @Override
      protected String getLabelForMode(IoMode mode) {
        if(mode == IoMode.PUSH) {
          return EnderIO.lang.localize("gui.capBank.outputMode");
        } else if(mode == IoMode.PULL) {
          return EnderIO.lang.localize("gui.capBank.inputMode");
        }
        return super.getLabelForMode(mode);
      }

    };
    addOverlay(configOverlay);

  }

  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == CONFIG_ID) {
      boolean vis = !configOverlay.isVisible();
      configOverlay.setVisible(vis);
    }
  }

  @Override
  public void initGui() {
    super.initGui();

    configB.onGuiInit();

    int x = guiLeft + xSize - rightMargin - GuiPoweredMachineBase.BUTTON_SIZE;
    int y = guiTop + inputY;

    FontRenderer fontRenderer = getFontRenderer();

    inputRsButton.onGuiInit();
    outputRsButton.onGuiInit();

    x = guiLeft + inputX;
    y = guiTop + inputY;
    maxInputTF = new GuiTextField(fontRenderer, x, y, 68, 16);
    maxInputTF.setCanLoseFocus(true);
    maxInputTF.setMaxStringLength(10);
    maxInputTF.setFocused(false);
    maxInputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxInput()));

    x = guiLeft + outputX;
    y = guiTop + outputY;
    maxOutputTF = new GuiTextField(fontRenderer, x, y, 68, 16);
    maxOutputTF.setCanLoseFocus(true);
    maxOutputTF.setMaxStringLength(10);
    maxOutputTF.setFocused(true);
    maxOutputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxOutput()));
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    if(par1 == 'e') {
      super.keyTyped(par1, 1);
    }
    maxInputTF.textboxKeyTyped(par1, par2);
    maxOutputTF.textboxKeyTyped(par1, par2);
    updateInputOutput();
  }

  private void updateInputOutput() {
    int input = parsePower(maxInputTF);
    if(input >= 0 && capBank.getMaxInput() != input) {
      setMaxInput(input);
    }
    int output = parsePower(maxOutputTF);
    if(output >= 0 && capBank.getMaxOutput() != output) {
      setMaxOutput(output);
    }
  }

  private void setMaxOutput(int output) {
    if(output != capBank.getMaxOutput()) {
      capBank.setMaxOutput(output);
      maxOutputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxOutput()));
      PacketHandler.INSTANCE.sendToServer(new PacketClientState(capBank));
    }
  }

  private void setMaxInput(int input) {
    if(input != capBank.getMaxInput()) {
      capBank.setMaxInput(input);
      maxInputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxInput()));
      PacketHandler.INSTANCE.sendToServer(new PacketClientState(capBank));
    }
  }

  private int parsePower(GuiTextField tf) {
    String txt = tf.getText();
    try {
      Integer power = PowerDisplayUtil.parsePower(txt);
      if(power == null) {
        return -1;
      }
      return power.intValue();
    } catch (Exception e) {
      return -1;
    }
  }

  @Override
  protected void mouseClicked(int par1, int par2, int par3) {
    super.mouseClicked(par1, par2, par3);
    maxInputTF.mouseClicked(par1, par2, par3);
    maxOutputTF.mouseClicked(par1, par2, par3);
  }

  @Override
  public void updateScreen() {
    maxInputTF.updateCursorCounter();
    maxOutputTF.updateCursorCounter();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize - 21, this.ySize);

    int i1 = capBank.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 176 + 21, 0, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

    maxInputTF.drawTextBox();
    maxOutputTF.drawTextBox();

    int midX = sx + xSize / 2;

    String str = EnderIO.lang.localize("gui.capBank.maxIo") + " " + PowerDisplayUtil.formatPower(capBank.getMaxIO()) +
        " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
    FontRenderer fontRenderer = getFontRenderer();
    int swid = fontRenderer.getStringWidth(str);
    int x = midX - swid / 2;
    int y = guiTop + 5;

    drawString(fontRenderer, str, x, y, -1);

    str = EnderIO.lang.localize("gui.capBank.maxInput") + ":";
    swid = fontRenderer.getStringWidth(str);
    x = guiLeft + inputX - swid - 3;
    y = guiTop + inputY + 2;
    drawString(fontRenderer, str, x, y, -1);

    str = EnderIO.lang.localize("gui.capBank.maxOutput") + ":";
    swid = fontRenderer.getStringWidth(str);
    x = guiLeft + outputX - swid - 3;
    y = guiTop + outputY + 2;
    drawString(fontRenderer, str, x, y, -1);

  }

  @Override
  public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    super.drawHoveringText(par1List, par2 + 24, par3, font);
    GL11.glPopAttrib();
    GL11.glPopAttrib();
  }

  @Override
  public int getGuiLeft() {
    return guiLeft + 24;
  }

  @Override
  public int getGuiTop() {
    return guiTop;
  }

  @Override
  public int getXSize() {
    return xSize - 42;
  }

  @Override
  public int getOverlayOffsetX() {
    return 21;
  }

  @Override
  public FontRenderer getFontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

}
