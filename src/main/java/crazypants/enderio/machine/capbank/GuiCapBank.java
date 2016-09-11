package crazypants.enderio.machine.capbank;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.packet.PacketGuiChange;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateRequest;
import crazypants.enderio.machine.gui.GuiButtonIoConfig;
import crazypants.enderio.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class GuiCapBank extends GuiContainerBaseEIO {

  private static final CapBankClientNetwork NULL_NETWORK = new CapBankClientNetwork(-1);

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

  private final TileCapBank capBank;

  private RedstoneModeButton inputRsButton;
  private RedstoneModeButton outputRsButton;

  private TextFieldEnder maxInputTF;
  private TextFieldEnder maxOutputTF;

  private final GuiOverlayIoConfig configOverlay;
  private final GuiButtonIoConfig configB;

  private CapBankClientNetwork network;

  private int initialStateCount = -1;
  private boolean initState = true;

  private final ContainerCapBank container;

  public GuiCapBank(Entity player, InventoryPlayer playerInv, TileCapBank te) {
    super(new ContainerCapBank(playerInv, te), "capacitorBank");
    capBank = te;
    container = (ContainerCapBank) inventorySlots;

    updateState();

    xSize = 176 + 42;

    addToolTip(new GuiToolTip(new Rectangle(5, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(PowerDisplayUtil.formatPower(network.getEnergyStoredL()) + " " + PowerDisplayUtil.ofStr());
        text.add(EnumChatFormatting.WHITE + PowerDisplayUtil.formatPower(network.getMaxEnergyStoredL()) + " " + EnumChatFormatting.GRAY
            + PowerDisplayUtil.abrevation());

        float change = network.getAverageChangePerTick();
        String color = EnumChatFormatting.WHITE.toString();
        if(change > 0) {
          color = EnumChatFormatting.GREEN.toString() + "+";
        } else if(change < 0) {
          color = EnumChatFormatting.RED.toString();
        }
        text.add(String.format("%s%s%s" + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr(), color,
            PowerDisplayUtil.formatPower(Math.round(change)), " "
                + EnumChatFormatting.GRAY.toString()));
      }

    });

    int x = xSize - rightMargin - GuiPoweredMachineBase.BUTTON_SIZE - 21;
    int y = inputY;
    inputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        network.setInputControlMode(mode);
        sendUpdateToServer();
      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return network.getInputControlMode();
      }
    });
    inputRsButton.setTooltipKey("enderio.gui.capBank.inputRs");

    y += 18;
    outputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        network.setOutputControlMode(mode);
        sendUpdateToServer();
      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return network.getOutputControlMode();
      }
    });
    outputRsButton.setTooltipKey("enderio.gui.capBank.outputRs");

    List<BlockCoord> coords = new ArrayList<BlockCoord>();
    if(network != null && network.getMembers().size() < 200) {
      for (TileCapBank cb : network.getMembers()) {
        coords.add(cb.getLocation());
      }
    }
    if(coords.isEmpty()) {
      coords.add(te.getLocation());
    }


    configOverlay = new GuiOverlayIoConfig(coords) {
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

    y += 20;
    configB = new GuiButtonIoConfig(this, CONFIG_ID, x, y, te, configOverlay);
    
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    
    x = inputX - 24;
    y = inputY;
    maxInputTF = new TextFieldEnder(fontRenderer, x, y, 68, 16);
    maxInputTF.setMaxStringLength(10);
    maxInputTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);
    
    x = outputX - 24;
    y = outputY;
    maxOutputTF = new TextFieldEnder(fontRenderer, x, y, 68, 16);
    maxOutputTF.setMaxStringLength(10);
    maxOutputTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);

    textFields.add(maxInputTF);
    textFields.add(maxOutputTF);
  }

  @Override
  public void initGui() {
    super.initGui();

    configB.onGuiInit();

    inputRsButton.onGuiInit();
    outputRsButton.onGuiInit();
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    updateInputOutput();
  }

  private void updateInputOutput() {
    int input = PowerDisplayUtil.parsePower(maxInputTF);
    if(input >= 0 && network.getMaxInput() != input) {
      setMaxInput(input);
    }
    int output = PowerDisplayUtil.parsePower(maxOutputTF);
    if(output >= 0 && network.getMaxOutput() != output) {
      setMaxOutput(output);
    }
  }

  private void setMaxOutput(int output) {
    if(output != network.getMaxOutput()) {
      network.setMaxOutput(output);
      maxOutputTF.setText(PowerDisplayUtil.formatPower(network.getMaxOutput()));
      sendUpdateToServer();
    }
  }

  private void setMaxInput(int input) {
    if(input != network.getMaxInput()) {
      network.setMaxInput(input);
      maxInputTF.setText(PowerDisplayUtil.formatPower(network.getMaxInput()));
      sendUpdateToServer();
    }
  }

  protected void sendUpdateToServer() {
    if(network != NULL_NETWORK) {
      PacketHandler.INSTANCE.sendToServer(new PacketGuiChange(capBank));
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    
    requestStateUpdate();

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize - 21, ySize);

    if (container.hasBaublesSlots()) {
      drawTexturedModalRect(sx, sy + 83, 197, 83, 24, 77);
    }
    
    int i1 = getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 176 + 21, 0, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) buttonList.get(i);
      guibutton.drawButton(mc, 0, 0);
    }

    int midX = sx + xSize / 2;

    String str = EnderIO.lang.localize("gui.capBank.maxIo") + " " + PowerDisplayUtil.formatPower(network.getMaxIO()) +
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
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @SuppressWarnings("rawtypes")
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

  private int getEnergyStoredScaled(int scale) {
    return (int) VecmathUtil.clamp(Math.round(scale * network.getEnergyStoredRatio()), 0, scale);
  }

  private void requestStateUpdate() {
    if(EnderIO.proxy.getTickCount() % 2 == 0) {
      if(!updateState()) {
        network.requestPowerUpdate(capBank, 2);
      }
    }
  }

  private boolean updateState() {
    if(!initState) {
      return false;
    }

    if(capBank.getNetwork() == null) {
      network = NULL_NETWORK;
      return true;
    }
    if(network == null || network == NULL_NETWORK) {
      network = (CapBankClientNetwork) capBank.getNetwork();
      initialStateCount = network.getStateUpdateCount();
      PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
      return true;
    }
    if(network.getStateUpdateCount() == initialStateCount) {
      PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
      return true;
    }
    if(network.getStateUpdateCount() > initialStateCount) {
      container.updateInventory();
      updateFieldsFromState();
      initState = false;
      return true;
    }
    return false;
  }

  private void updateFieldsFromState() {
    maxInputTF.setText(PowerDisplayUtil.formatPower(network.getMaxInput()));
    maxOutputTF.setText(PowerDisplayUtil.formatPower(network.getMaxOutput()));
    inputRsButton.setMode(network.getInputControlMode());
    outputRsButton.setMode(network.getOutputControlMode());
  }


}
