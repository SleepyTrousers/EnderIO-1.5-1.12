package crazypants.enderio.powertools.machine.capbank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.common.util.NullHelper;
import com.google.common.base.Predicate;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiButtonIoConfig;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.gui.PowerBar.Op;
import crazypants.enderio.base.machine.gui.PowerBar.What;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.powertools.lang.Lang;
import crazypants.enderio.powertools.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.powertools.machine.capbank.packet.PacketGuiChange;
import crazypants.enderio.powertools.machine.capbank.packet.PacketNetworkStateRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class GuiCapBank extends GuiContainerBaseEIO {

  private static final @Nonnull CapBankClientNetwork NULL_NETWORK = new CapBankClientNetwork(-1);

  protected static final int INPUT_BUTTON_ID = 18;
  protected static final int OUTPUT_BUTTON_ID = 37;

  protected static final int CONFIG_ID = 377996104;

  private static final int POWER_X = 8;
  private static final int POWER_Y = 9;
  private static final int POWER_HEIGHT = 68;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private int inputX = 104;
  private int inputY = 18;

  private int outputX = 104;
  private int outputY = 36;

  private int rightMargin = 8;

  private final @Nonnull TileCapBank capBank;

  private final @Nonnull RedstoneModeButton<?> inputRsButton;
  private final @Nonnull RedstoneModeButton<?> outputRsButton;

  private final @Nonnull TextFieldEnder maxInputTF;
  private final @Nonnull TextFieldEnder maxOutputTF;

  private final @Nonnull GuiOverlayIoConfig<TileCapBank> configOverlay;
  private final @Nonnull GuiButtonIoConfig<TileCapBank> configB;

  private @Nonnull CapBankClientNetwork network = NULL_NETWORK;

  private int initialStateCount = -1;
  private boolean initState = true;
  private boolean textFieldsHaveRealData = false;

  private @Nonnull PowerBar powerBar;

  @SuppressWarnings("rawtypes")
  public GuiCapBank(Entity player, InventoryPlayer playerInv, @Nonnull TileCapBank te, @Nonnull ContainerCapBank container) {
    super(container, "capacitor_bank");
    capBank = te;

    updateState();

    xSize = 176;

    int x = xSize - rightMargin - GuiMachineBase.BUTTON_SIZE;
    int y = inputY;
    inputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        network.setInputControlMode(mode);
        sendUpdateToServer();
      }

      @Override
      public @Nonnull RedstoneControlMode getRedstoneControlMode() {
        return network.getInputControlMode();
      }

      @Override
      public boolean getRedstoneControlStatus() {
        return false;
      }
    });
    inputRsButton.setToolTip("enderio.gui.cap_bank.inputRs");

    y += 18;
    outputRsButton = new RedstoneModeButton(this, -1, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        network.setOutputControlMode(mode);
        sendUpdateToServer();
      }

      @Override
      public @Nonnull RedstoneControlMode getRedstoneControlMode() {
        return network.getOutputControlMode();
      }

      @Override
      public boolean getRedstoneControlStatus() {
        return false;
      }
    });
    outputRsButton.setToolTip("enderio.gui.cap_bank.output_rs");

    List<BlockPos> coords = new ArrayList<BlockPos>();
    if (network.getMembers().size() < 200) {
      for (TileCapBank cb : network.getMembers()) {
        coords.add(cb.getLocation());
      }
    }
    if (coords.isEmpty()) {
      coords.add(te.getLocation());
    }

    configOverlay = new GuiOverlayIoConfig<TileCapBank>(coords) {
      @Override
      protected @Nonnull String getLabelForMode(IoMode mode) {
        if (mode == IoMode.PUSH) {
          return Lang.GUI_CAPBANK_OUTPUT_MODE.get();
        } else if (mode == IoMode.PULL) {
          return Lang.GUI_CAPBANK_INPUT_MODE.get();
        }
        return super.getLabelForMode(mode);
      }
    };
    addOverlay(configOverlay);

    y += 20;
    configB = new GuiButtonIoConfig<TileCapBank>(this, CONFIG_ID, x, y, te, configOverlay);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    // Validate that the next character input won't overflow the max IO, and disallow starting numbers with 0
    final Predicate<String> inputValidator = s -> s == null || s.isEmpty() || (!s.startsWith("0") && PowerDisplayUtil.parsePower(s) <= network.getMaxIO());

    x = inputX - 24;
    y = inputY;
    maxInputTF = new TextFieldEnder(fr, x, y, 68, 16);
    maxInputTF.setValidator(inputValidator);
    maxInputTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);

    x = outputX - 24;
    y = outputY;
    maxOutputTF = new TextFieldEnder(fr, x, y, 68, 16);
    maxOutputTF.setValidator(inputValidator);
    maxOutputTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);

    textFields.add(maxInputTF);
    textFields.add(maxOutputTF);

    powerBar = new PowerBar(te, this, POWER_X, POWER_Y, POWER_HEIGHT);
    addDrawingElement(powerBar);
    powerBar.addTooltip(Op.REPLACE, What.ALL, //
        () -> LangPower.format(network.getEnergyStoredL()) + " " + LangPower.ofStr(), //
        () -> TextFormatting.WHITE + LangPower.format(network.getMaxEnergyStoredL()) + " " + TextFormatting.GRAY + LangPower.RF(), //
        () -> crazypants.enderio.base.lang.Lang.POWER_PERTICK
            .get(getIOColor() + LangPower.format(Math.round(network.getAverageChangePerTick())) + TextFormatting.GRAY) //
    );
  }

  private @Nonnull String getIOColor() {
    float change = network.getAverageChangePerTick();
    if (change > 0) {
      return TextFormatting.GREEN + "+";
    } else if (change < 0) {
      return TextFormatting.RED + "";
    } else {
      return TextFormatting.WHITE + "";
    }
  }

  @Override
  public void initGui() {
    super.initGui();

    configB.onGuiInit();

    inputRsButton.onGuiInit();
    outputRsButton.onGuiInit();
  }

  @Override
  protected void keyTyped(char par1, int par2) throws IOException {
    super.keyTyped(par1, par2);
    updateInputOutput();
  }

  private void updateInputOutput() {
    if (!textFieldsHaveRealData) {
      return;
    }
    int input = PowerDisplayUtil.parsePower(maxInputTF);
    if (input >= 0 && network.getMaxInput() != input) {
      setMaxInput(input);
    }
    int output = PowerDisplayUtil.parsePower(maxOutputTF);
    if (output >= 0 && network.getMaxOutput() != output) {
      setMaxOutput(output);
    }
  }

  private void setMaxOutput(int output) {
    if (output != network.getMaxOutput()) {
      network.setMaxOutput(output);
      maxOutputTF.setText(LangPower.format(network.getMaxOutput()));
      sendUpdateToServer();
    }
  }

  private void setMaxInput(int input) {
    if (input != network.getMaxInput()) {
      network.setMaxInput(input);
      maxInputTF.setText(LangPower.format(network.getMaxInput()));
      sendUpdateToServer();
    }
  }

  protected void sendUpdateToServer() {
    if (network != NULL_NETWORK) {
      PacketHandler.INSTANCE.sendToServer(new PacketGuiChange(capBank));
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    requestStateUpdate();

    GlStateManager.color(1, 1, 1);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = buttonList.get(i);
      guibutton.drawButton(mc, 0, 0, par1);
    }

    int midX = sx + xSize / 2;

    String str = Lang.GUI_CAPBANK_MAX_IO.get() + " " + LangPower.RFt(network.getMaxIO());
    FontRenderer fr = getFontRenderer();
    int swid = fr.getStringWidth(str);
    int x = midX - swid / 2;
    int y = guiTop + 5;

    drawString(fr, str, x, y, -1);

    str = Lang.GUI_CAPBANK_MAX_INPUT.get() + ":";
    swid = fr.getStringWidth(str);
    x = guiLeft + inputX - swid - 26;
    y = guiTop + inputY + 2;
    drawString(fr, str, x, y, -1);

    str = Lang.GUI_CAPBANK_MAX_OUTPUT.get() + ":";
    swid = fr.getStringWidth(str);
    x = guiLeft + outputX - swid - 26;
    y = guiTop + outputY + 2;
    drawString(fr, str, x, y, -1);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  public void drawHoveringText(@Nonnull List<String> par1List, int par2, int par3, @Nonnull FontRenderer font) {
    super.drawHoveringText(par1List, par2, par3, font);
  }

  @Override
  public @Nonnull FontRenderer getFontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  private void requestStateUpdate() {
    if (EnderIO.proxy.getTickCount() % 2 == 0) {
      if (!updateState()) {
        network.requestPowerUpdate(capBank, 2);
      }
    }
  }

  private boolean updateState() {
    if (!initState) {
      return false;
    }

    if (capBank.getNetwork() == null) {
      network = NULL_NETWORK;
      return true;
    }
    if (network == NULL_NETWORK) {
      network = NullHelper.first((CapBankClientNetwork) capBank.getNetwork(), NULL_NETWORK);
      initialStateCount = network.getStateUpdateCount();
      PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
      return true;
    }
    if (network.getStateUpdateCount() == initialStateCount) {
      PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
      return true;
    }
    if (network.getStateUpdateCount() > initialStateCount) {
      updateFieldsFromState();
      initState = false;
      return true;
    }
    return false;
  }

  private void updateFieldsFromState() {
    maxInputTF.setText(LangPower.format(network.getMaxInput()));
    maxOutputTF.setText(LangPower.format(network.getMaxOutput()));
    textFieldsHaveRealData = true;
    inputRsButton.setModeRaw(RedstoneControlMode.IconHolder.getFromMode(network.getInputControlMode()));
    outputRsButton.setModeRaw(RedstoneControlMode.IconHolder.getFromMode(network.getOutputControlMode()));
  }

}
