package crazypants.enderio.machine.power;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.RedstoneModePacketProcessor;
import crazypants.gui.GuiContainerBase;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiCapacitorBank extends GuiContainerBase {

  protected static final int INPUT_BUTTON_ID = 18;
  protected static final int OUTPUT_BUTTON_ID = 37;

  private static final int POWER_X = 8;
  private static final int POWER_Y = 9;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 68;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private int inputX = 78;
  private int inputY = 18;

  private int outputX = 78;
  private int outputY = 36;

  private int rightMargin = 8;

  private final TileCapacitorBank capBank;

  private IconButton inputRedstoneButton;
  private IconButton outputRedstoneButton;

  private GuiTextField maxInputTF;
  private GuiTextField maxOutputTF;

  public GuiCapacitorBank(InventoryPlayer playerInv, TileCapacitorBank te) {
    super(new ContainerCapacitorBank(playerInv, te));
    this.capBank = te;

    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(PowerDisplayUtil.formatPower(capBank.getEnergyStored()) + " " + PowerDisplayUtil.ofStr());
        text.add(PowerDisplayUtil.formatPower(capBank.getMaxEnergyStored()) + " " + PowerDisplayUtil.abrevation());
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.localize("gui.capBank.inputRs"));
        text.add(capBank.getInputControlMode().getTooltip());
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - rightMargin - GuiMachineBase.BUTTON_SIZE, inputY, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.localize("gui.capBank.outputRs"));
        text.add(capBank.getOutputControlMode().getTooltip());
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - rightMargin - GuiMachineBase.BUTTON_SIZE, outputY, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }

    });

  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if(par1GuiButton.id == INPUT_BUTTON_ID) {
      int ordinal = capBank.getInputControlMode().ordinal();
      ordinal++;
      if(ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      capBank.setInputControlMode(RedstoneControlMode.values()[ordinal]);
      inputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(capBank.getInputControlMode()));
      Packet pkt = RedstoneModePacketProcessor.getRedstoneControlPacket(capBank);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if(par1GuiButton.id == OUTPUT_BUTTON_ID) {
      int ordinal = capBank.getOutputControlMode().ordinal();
      ordinal++;
      if(ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      capBank.setOutputControlMode(RedstoneControlMode.values()[ordinal]);
      outputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(capBank.getOutputControlMode()));
      Packet pkt = RedstoneModePacketProcessor.getRedstoneControlPacket(capBank);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    super.initGui();
    int x = guiLeft + xSize - rightMargin - GuiMachineBase.BUTTON_SIZE;
    //int y = guiTop + 5;
    int y = guiTop + inputY;

    inputRedstoneButton = new IconButton(fontRenderer, INPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(capBank.getInputControlMode()),
        RenderUtil.BLOCK_TEX);
    inputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(inputRedstoneButton);

    //y = y + 5 + GuiMachineBase.BUTTON_SIZE;
    y = guiTop + outputY;
    outputRedstoneButton = new IconButton(fontRenderer, OUTPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(capBank.getOutputControlMode()),
        RenderUtil.BLOCK_TEX);
    outputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(outputRedstoneButton);

    x = guiLeft + inputX;
    y = guiTop + inputY;
    maxInputTF = new GuiTextField(fontRenderer, x, y, 72, 16);
    maxInputTF.setCanLoseFocus(true);
    maxInputTF.setMaxStringLength(10);
    maxInputTF.setFocused(false);
    maxInputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxInput()));

    x = guiLeft + outputX;
    y = guiTop + outputY;
    maxOutputTF = new GuiTextField(fontRenderer, x, y, 72, 16);
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
    capBank.setMaxOutput(output);
    if(output != capBank.getMaxOutput()) {
      maxOutputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxOutput()));
    }
    Packet pkt = CapacitorBankPacketHandler.createMaxInputOutputPacket(capBank);
    PacketDispatcher.sendPacketToServer(pkt);
  }

  private void setMaxInput(int input) {
    capBank.setMaxInput(input);
    if(input != capBank.getMaxInput()) {
      maxInputTF.setText(PowerDisplayUtil.formatPower(capBank.getMaxInput()));
    }
    Packet pkt = CapacitorBankPacketHandler.createMaxInputOutputPacket(capBank);
    PacketDispatcher.sendPacketToServer(pkt);
  }

  private int parsePower(GuiTextField tf) {
    String txt = tf.getText();
    try {
      Float power = PowerDisplayUtil.parsePower(txt);
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
    RenderUtil.bindTexture("enderio:textures/gui/capacitorBank.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int i1 = capBank.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 176, 0, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

    maxInputTF.drawTextBox();
    maxOutputTF.drawTextBox();

    int midX = sx + xSize / 2;

    String str = Lang.localize("gui.capBank.maxIo") + " " + PowerDisplayUtil.formatPower(capBank.getMaxIO()) +
        " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
    int swid = fontRenderer.getStringWidth(str);
    int x = midX - swid / 2;
    int y = guiTop + 5;

    drawString(fontRenderer, str, x, y, -1);

    str = Lang.localize("gui.capBank.maxInput") + ":";
    swid = fontRenderer.getStringWidth(str);
    x = guiLeft + inputX - swid - 3;
    y = guiTop + inputY + 2;
    drawString(fontRenderer, str, x, y, -1);

    str = Lang.localize("gui.capBank.maxOutput") + ":";
    swid = fontRenderer.getStringWidth(str);
    x = guiLeft + outputX - swid - 3;
    y = guiTop + outputY + 2;
    drawString(fontRenderer, str, x, y, -1);

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
    return fontRenderer;
  }

}
