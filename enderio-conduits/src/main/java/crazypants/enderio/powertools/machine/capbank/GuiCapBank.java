package crazypants.enderio.powertools.machine.capbank;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiButtonIoConfig;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.powertools.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.powertools.machine.capbank.packet.PacketGuiChange;
import crazypants.enderio.powertools.machine.capbank.packet.PacketNetworkStateRequest;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class GuiCapBank extends GuiContainerBaseEIO {

  public static final @Nonnull ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");

  private static final @Nonnull CapBankClientNetwork NULL_NETWORK = new CapBankClientNetwork(-1);

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

  private final @Nonnull ContainerCapBank container;

  @SuppressWarnings("rawtypes")
  public GuiCapBank(Entity player, InventoryPlayer playerInv, @Nonnull TileCapBank te, @Nonnull ContainerCapBank container) {
    super(container, "capacitor_bank");
    capBank = te;
    this.container = (ContainerCapBank) inventorySlots;

    updateState();

    xSize = 176 + 42;

    addToolTip(new GuiToolTip(new Rectangle(7, POWER_Y + 1, POWER_WIDTH, POWER_HEIGHT - 1), "") {

      @Override
      protected void updateText() {
        text.clear();
        // FIXME how can we put a newline between these two without hardcoding word order?
        text.add(LangPower.format(network.getEnergyStoredL()) + " " + LangPower.ofStr());
        text.add(TextFormatting.WHITE + LangPower.format(network.getMaxEnergyStoredL()) + " " + TextFormatting.GRAY + LangPower.RF());

        float change = network.getAverageChangePerTick();
        String color = TextFormatting.WHITE.toString();
        if (change > 0) {
          color = TextFormatting.GREEN.toString() + "+";
        } else if (change < 0) {
          color = TextFormatting.RED.toString();
        }
        text.add(Lang.POWER_PERTICK.get(color + LangPower.format(Math.round(change)) + TextFormatting.GRAY.toString()));
      }

    });

    int x = xSize - rightMargin - GuiMachineBase.BUTTON_SIZE - 21;
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
          return EnderIO.lang.localize("gui.cap_bank.output_mode");
        } else if (mode == IoMode.PULL) {
          return EnderIO.lang.localize("gui.cap_bank.input_mode");
        }
        return super.getLabelForMode(mode);
      }
    };
    addOverlay(configOverlay);

    y += 20;
    configB = new GuiButtonIoConfig<TileCapBank>(this, CONFIG_ID, x, y, te, configOverlay);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    x = inputX - 24;
    y = inputY;
    maxInputTF = new TextFieldEnder(fr, x, y, 68, 16);
    maxInputTF.setMaxStringLength(10);
    maxInputTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);

    x = outputX - 24;
    y = outputY;
    maxOutputTF = new TextFieldEnder(fr, x, y, 68, 16);
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

    drawTexturedModalRect(sx, sy, 0, 0, xSize - 21, ySize);

    if (container.hasBaublesSlots()) {
      drawTexturedModalRect(sx + 194, sy + 6, 221, 78, 24, 39);
      for (int i = 1; i < container.baubles.getSizeInventory(); i++) {
        drawTexturedModalRect(sx + 194, sy + 11 + i * 18, 221, 137, 24, 23);
      }
      RenderUtil.bindTexture(baublesBackground);
      for (int i = 0; i < container.baubles.getSizeInventory(); i++) {
        if (Prep.isInvalid(container.baubles.getStackInSlot(i))) {
          final int textureX = 77 + (i / 4) * 19;
          final int textureY = 8 + (i % 4) * 18;
          drawTexturedModalRect(sx + 196, sy + 12 + i * 18, textureX, textureY, 16, 16);
        }
      }
      bindGuiTexture();
    }

    int i1 = getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 176 + 21, 0, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = buttonList.get(i);
      guibutton.drawButton(mc, 0, 0, par1);
    }

    int midX = sx + xSize / 2;

    String str = EnderIO.lang.localize("gui.cap_bank.max_io") + " " + LangPower.RFt(network.getMaxIO());
    FontRenderer fr = getFontRenderer();
    int swid = fr.getStringWidth(str);
    int x = midX - swid / 2;
    int y = guiTop + 5;

    drawString(fr, str, x, y, -1);

    str = EnderIO.lang.localize("gui.cap_bank.max_input") + ":";
    swid = fr.getStringWidth(str);
    x = guiLeft + inputX - swid - 3;
    y = guiTop + inputY + 2;
    drawString(fr, str, x, y, -1);

    str = EnderIO.lang.localize("gui.cap_bank.max_output") + ":";
    swid = fr.getStringWidth(str);
    x = guiLeft + outputX - swid - 3;
    y = guiTop + outputY + 2;
    drawString(fr, str, x, y, -1);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  public void drawHoveringText(@Nonnull List<String> par1List, int par2, int par3, @Nonnull FontRenderer font) {
    super.drawHoveringText(par1List, par2, par3, font);
  }

  @Override
  public int getGuiLeft() {
    return guiLeft + getOverlayOffsetX();
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
    return 22;
  }

  @Override
  public @Nonnull FontRenderer getFontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  private int getEnergyStoredScaled(int scale) {
    return (int) VecmathUtil.clamp(Math.round(scale * network.getEnergyStoredRatio()), 0, scale);
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
      container.updateInventory();
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
