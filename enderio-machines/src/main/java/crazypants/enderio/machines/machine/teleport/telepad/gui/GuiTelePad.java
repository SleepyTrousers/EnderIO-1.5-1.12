package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IToggleableGui;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.ToggleTravelButton;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.machines.config.config.TelePadConfig;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;

public class GuiTelePad extends GuiContainerBaseEIO implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 95;
  private static final int ID_TELEPORT_BUTTON = 96;

  ToggleTravelButton switchButton;
  GuiButton teleportButton;

  private final @Nonnull TileTelePad te;

  private final @Nonnull TextFieldEnder xTF, yTF, zTF, dimTF;

  private static final int powerX = 8;
  private static final int powerY = 9;
  private int powerScale = TelePadConfig.telepadFluidUse.get() > 0 ? 57 : 120; // TODO -> method

  private static final int progressX = 26;
  private static final int progressY = 110;
  private static final int progressScale = 124;

  private static final int fluidX = 8;
  private static final int fluidY = 71;
  private static final int fluidScale = 58;
  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(fluidX, fluidY, 10, fluidScale);

  public static int SWITCH_X = 155, SWITCH_Y = 5;

  public GuiTelePad(@Nonnull InventoryPlayer playerInv, final @Nonnull TileTelePad te) {
    super(new ContainerTelePad(playerInv, te), "tele_pad");
    this.te = te;
    ySize = 220;

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

    if (TelePadConfig.telepadFluidUse.get() > 0) {
      addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {
        @Override
        protected void updateText() {
          text.clear();
          text.add(Lang.GUI_TELEPAD_TANK.get());
          text.add(LangFluid.MB(te.getTank()));
        }
      });
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    int x = 48;
    int y = 24;
    int tfHeight = 12;
    int tfWidth = xSize - x * 2;
    xTF = new TextFieldEnder(fr, x, y, tfWidth, tfHeight, TextFieldEnder.FILTER_NUMERIC);
    yTF = new TextFieldEnder(fr, x, y + xTF.height + 2, tfWidth, tfHeight, TextFieldEnder.FILTER_NUMERIC);
    zTF = new TextFieldEnder(fr, x, y + (xTF.height * 2) + 4, tfWidth, tfHeight, TextFieldEnder.FILTER_NUMERIC);
    dimTF = new TextFieldEnder(fr, x, y + (xTF.height * 3) + 6, tfWidth, tfHeight, TextFieldEnder.FILTER_NUMERIC);

    xTF.setText(Integer.toString(te.getX()));
    yTF.setText(Integer.toString(te.getY()));
    zTF.setText(Integer.toString(te.getZ()));
    dimTF.setText(Integer.toString(te.getTargetDim()));

    xTF.setCanLoseFocus(!TelePadConfig.telepadLockCoords.get());
    yTF.setCanLoseFocus(!TelePadConfig.telepadLockCoords.get());
    zTF.setCanLoseFocus(!TelePadConfig.telepadLockCoords.get());
    dimTF.setCanLoseFocus(!TelePadConfig.telepadLockDimension.get());

    textFields.addAll(Lists.newArrayList(xTF, yTF, zTF, dimTF));

    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, SWITCH_X, SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(Lang.GUI_TELEPAD_TO_TRAVEL.get());
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return te.getTank().getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  protected int getPowerOutputValue() {
    return te.getUsage();
  }

  protected void updatePowerBarTooltip(List<String> text) {
    text.add(Lang.GUI_TELEPAD_MAX.get(LangPower.RFt(getPowerOutputValue())));
    text.add(LangPower.RF(te.getEnergy().getEnergyStored(), te.getEnergy().getMaxEnergyStored()));
  }

  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();

    String text = Lang.GUI_TELEPAD_TELEPORT.get();
    int textWidth = getFontRenderer().getStringWidth(text) + 10;

    int x = guiLeft + (xSize / 2) - (textWidth / 2);
    int y = guiTop + 83;

    teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, textWidth, 20, text);
    addButton(teleportButton);

    ((ContainerTelePad) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  public void updateScreen() {
    super.updateScreen();

    if (!xTF.isFocused()) {
      xTF.setText(Integer.toString(te.getX()));
    }
    if (!yTF.isFocused()) {
      yTF.setText(Integer.toString(te.getY()));
    }
    if (!zTF.isFocused()) {
      zTF.setText(Integer.toString(te.getZ()));
    }
    if (!dimTF.isFocused()) {
      dimTF.setText(Integer.toString(te.getTargetDim()));
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) throws IOException {
    super.keyTyped(par1, par2);
    updateCoords();
  }

  private void updateCoords() {
    BlockPos pos = new BlockPos(getIntFromTextBox(xTF), getIntFromTextBox(yTF), getIntFromTextBox(zTF));
    int targetDim = getIntFromTextBox(dimTF);
    if (!pos.equals(te.getTarget().getLocation()) || targetDim != te.getTargetDim()) {
      te.setCoords(pos);
      te.setTargetDim(targetDim);
      PacketHandler.INSTANCE.sendToServer(new PacketSetTarget(te, te.getTarget()));
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

    // draw power / fluid background
    int u = TelePadConfig.telepadFluidUse.get() > 0 ? 200 : 187;
    int v = 0;
    drawTexturedModalRect(sx + powerX - 1, sy + powerY - 1, u, v, 12, 122);

    if (TelePadConfig.telepadFluidUse.get() > 0 && te.getFluidAmount() > 0) {
      RenderUtil.renderGuiTank(te.getTank(), sx + fluidX, sy + fluidY, 0, 10, fluidScale);
      bindGuiTexture();
      drawTexturedModalRect(sx + fluidX, sy + fluidY, 213, v, 10, fluidScale);
    }

    int powerScaled = te.getPowerScaled(powerScale);
    drawTexturedModalRect(sx + powerX, sy + powerY + powerScale - powerScaled, xSize, 0, 10, powerScaled);
    int progressScaled = Util.getProgressScaled(progressScale, te);
    drawTexturedModalRect(sx + progressX, sy + progressY, 0, ySize, progressScaled, 10);

    FontRenderer fnt = getFontRenderer();

    String[] text = { "X", "Y", "Z", "DIM" };
    for (int i = 0; i < text.length; i++) {
      TextFieldEnder f = textFields.get(i);
      fnt.drawString(NullHelper.first(text[i]), f.x - (fnt.getStringWidth(NullHelper.first(text[i], "")) / 2) - 10,
          f.y + ((f.height - fnt.FONT_HEIGHT) / 2) + 1, 0x000000);
      if (!f.getCanLoseFocus()) {
        IconEIO.map.render(IconEIO.LOCK_LOCKED, f.x + f.width - 2, f.y - 2, true);
      }
    }

    Entity e = te.getCurrentTarget();
    if (e != null) {
      String name = e.getName();
      fnt.drawString(name, sx + xSize / 2 - fnt.getStringWidth(name) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, 0x000000);
    } else if (te.wasBlocked()) {
      String s = Lang.GUI_TELEPAD_ERROR_BLOCKED.get();
      fnt.drawString(s, sx + xSize / 2 - fnt.getStringWidth(s) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, 0xAA0000);
    }

    String name = te.getTarget().getName();
    fnt.drawStringWithShadow(name, sx + xSize / 2 - fnt.getStringWidth(name) / 2, getGuiTop() + 10, 0xffffff);

    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  @Override
  public void switchGui() {
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, BlockTelePad.GUI_ID_TELEPAD_TRAVEL));
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);

    if (button.id == ID_TELEPORT_BUTTON) {
      te.teleportAll();
    }
  }
}
