package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.Util;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class GuiTelePad extends GuiContainerBaseEIO implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 95;
  private static final int ID_TELEPORT_BUTTON = 96;

  ToggleTravelButton switchButton;
  GuiButton teleportButton;

  // private World world;
  private final TileTelePad te;

  private TextFieldEnder xTF, yTF, zTF, dimTF;

  private static final int powerX = 8;
  private static final int powerY = 9;
  private int powerScale = Config.telepadFluidUse > 0 ? 57 : 120;

  private static final int progressX = 26;
  private static final int progressY = 110;
  private static final int progressScale = 124;

  private static final int fluidX = 8;
  private static final int fluidY = 71;
  private static final int fluidScale = 58;
  private static final Rectangle RECTANGLE_TANK = new Rectangle(fluidX, fluidY, 10, fluidScale);

  public static int SWITCH_X = 155, SWITCH_Y = 5;

  public GuiTelePad(InventoryPlayer playerInv, final TileTelePad te) {
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

    if (Config.telepadFluidUse > 0) {
      addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {
        @Override
        protected void updateText() {
          text.clear();
          String heading = EnderIO.lang.localize("tank.tank");
          if(te.getTank().getFluid() != null) {
            heading += ": " + new FluidStack(te.getFluidType(), 1000).getLocalizedName();//te.getTank().getFluid().getLocalizedName();
          }
          text.add(heading);
          text.add(Fluids.toCapactityString(te.getTank()));
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

    xTF.setCanLoseFocus(!Config.telepadLockCoords);
    yTF.setCanLoseFocus(!Config.telepadLockCoords);
    zTF.setCanLoseFocus(!Config.telepadLockCoords);
    dimTF.setCanLoseFocus(!Config.telepadLockDimension);

    textFields.addAll(Lists.newArrayList(xTF, yTF, zTF, dimTF));

    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, SWITCH_X, SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(EnderIO.lang.localize("gui.telepad.configure.travel"));
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return te.getTank().getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  private String getPowerOutputLabel() {
    return I18n.format("enderio.gui.max");
  }

  protected int getPowerOutputValue() {
    return te.getUsage();
  }

  protected void updatePowerBarTooltip(List<String> text) {
    text.add(getPowerOutputLabel() + " " + PowerDisplayUtil.formatPower(getPowerOutputValue()) + " " + PowerDisplayUtil.abrevation()
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
    int y = guiTop + 83;

    teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, width, 20, text);
    addButton(teleportButton);

    ((ContainerTelePad) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  public void updateScreen() {
    super.updateScreen();

    if (te.getStackInSlot(0) != null) {
      te.setTarget(TelepadTarget.readFromNBT(te.getStackInSlot(0)));
      xTF.setText(Integer.toString(te.getX()));
      yTF.setText(Integer.toString(te.getY()));
      zTF.setText(Integer.toString(te.getZ()));
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
    int u = Config.telepadFluidUse > 0 ? 200 : 187;
    int v = 0;
    drawTexturedModalRect(sx + powerX - 1, sy + powerY - 1, u, v, 12, 122);

    if (Config.telepadFluidUse > 0 && te.getFluidAmount() > 0) {
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
      fnt.drawString(text[i], f.xPosition - (fnt.getStringWidth(text[i]) / 2) - 10, f.yPosition + ((f.height - fnt.FONT_HEIGHT) / 2) + 1, 0x000000);
      if (!f.getCanLoseFocus()) {
        IconEIO.map.render(IconEIO.LOCK_LOCKED, f.xPosition + f.width - 2, f.yPosition - 2, true);
      }
    }

    Entity e = te.getCurrentTarget();
    if (e != null) {
      String name = e.getName();
      fnt.drawString(name, sx + xSize / 2 - fnt.getStringWidth(name) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, 0x000000);
    } else if (te.wasBlocked()) {
      String s = EnderIO.lang.localize("gui.telepad.blocked");
      fnt.drawString(s, sx + xSize / 2 - fnt.getStringWidth(s) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, 0xAA0000);
    }

    String name = te.getTarget().getName();
    if (name != null) {
      fnt.drawStringWithShadow(name, sx + xSize / 2 - fnt.getStringWidth(name) / 2, getGuiTop() + 10, 0xffffff);
    }

    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  @Override
  public void switchGui() {
    BlockPos pos = te.getLocation();
    GuiID.GUI_ID_TELEPAD_TRAVEL.openClientGui(te.getWorld(), pos, mc.player, null);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiID.GUI_ID_TELEPAD_TRAVEL));
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    super.actionPerformed(button);

    if (button.id == ID_TELEPORT_BUTTON) {
      te.teleportAll();
    }
  }
}
