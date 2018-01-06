package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiDialingDevice extends GuiContainerBaseEIO implements IDialingDeviceRemoteExec.GUI {

  private static final int ID_TELEPORT_BUTTON = 96;

  GuiButton teleportButton;

  private final @Nonnull TileDialingDevice dialingDevice;
  private final @Nonnull TileTelePad telepad;

  private final static int powerX = 8;
  private final static int powerY = 9;
  private final static int powerScale = 120;

  private final static int progressX = 26;
  private final static int progressY = 110;
  private final static int progressScale = 124;

  private final GuiTargetList targetList;

  public GuiDialingDevice(@Nonnull InventoryPlayer playerInv, @Nonnull TileDialingDevice te, @Nonnull TileTelePad telepad) {
    super(new ContainerDialingDevice(playerInv, te), "dialing_device");
    this.dialingDevice = te;
    this.telepad = telepad;
    this.ySize = 220;

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
        text.add(Math.round(telepad.getProgress() * 100) + "%");
      }
    });

    int w = 115;
    int h = 71;
    int x = 30;
    int y = 10;
    targetList = new GuiTargetList(w, h, x, y, te);
    targetList.setShowSelectionBox(true);
    targetList.setScrollButtonIds(100, 101);

    targetList.setSelection(telepad.getTarget());

    addToolTip(new GuiToolTip(new Rectangle(x, y, w, h), "") {
      @Override
      protected void updateText() {
        text.clear();
        TelepadTarget el = targetList.getElementAt(getLastMouseX() + getGuiLeft(), getLastMouseY());
        if (el != null) {
          Rectangle iconBnds = targetList.getIconBounds(0);
          if (iconBnds.contains(getLastMouseX() + getGuiLeft(), 1)) {
            text.add(TextFormatting.RED + "Delete");
          } else {
            text.add(TextFormatting.WHITE + el.getName());
            text.add(BlockCoord.chatString(el.getLocation(), TextFormatting.WHITE));
            text.add(el.getDimenionName());
          }
        }
      }
    });

  }

  protected int getPowerOutputValue() {
    return dialingDevice.getEnergy().getMaxUsage();
  }

  protected void updatePowerBarTooltip(List<String> text) {
    text.add(Lang.GUI_TELEPAD_MAX.get(LangPower.RFt(getPowerOutputValue())));
    text.add(LangPower.RF(dialingDevice.getEnergy().getEnergyStored(), dialingDevice.getEnergy().getMaxEnergyStored()));
  }

  @Override
  public void initGui() {
    super.initGui();

    String text = Lang.GUI_TELEPAD_TELEPORT.get();
    int textWidth = getFontRenderer().getStringWidth(text) + 10;

    int x = guiLeft + (xSize / 2) - (textWidth / 2);
    int y = guiTop + 85;

    teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, textWidth, 20, text);
    addButton(teleportButton);

    ((ContainerDialingDevice) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());

    targetList.onGuiInit(this);
  }

  private int getPowerScaled(int scale) {
    return (int) ((((float) dialingDevice.getEnergy().getEnergyStored()) / (dialingDevice.getEnergy().getMaxEnergyStored())) * scale);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int powerScaled = getPowerScaled(powerScale);
    drawTexturedModalRect(sx + powerX, sy + powerY + powerScale - powerScaled, xSize, 0, 10, powerScaled);

    targetList.drawScreen(mouseX, mouseY, partialTick);

    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

    if (dialingDevice.getEnergy().getEnergyStored() < dialingDevice.getEnergy().getMaxUsage(CapacitorKey.DIALING_DEVICE_POWER_USE_TELEPORT)) {
      String txt = TextFormatting.DARK_RED + "No Power"; // FIXME I18N
      renderInfoMessage(sx, sy, txt, 0x000000);
      return;
    }
    if (telepad.getEnergy().getEnergyStored() <= 0) {
      String txt = TextFormatting.DARK_RED + "Telepad not powered"; // FIXME I18N
      renderInfoMessage(sx, sy, txt, 0x000000);
      return;
    }
    if (targetList.getSelectedElement() == null) {
      String txt = TextFormatting.DARK_RED + "Enter Target"; // FIXME I18N
      renderInfoMessage(sx, sy, txt, 0x000000);
      return;
    }

    bindGuiTexture();
    int progressScaled = Util.getProgressScaled(progressScale, telepad);
    drawTexturedModalRect(sx + progressX, sy + progressY, 0, ySize, progressScaled, 10);

    Entity e = telepad.getCurrentTarget();
    if (e != null) {
      String name = e.getName();
      renderInfoMessage(sx, sy, name, 0x000000);
    } else if (telepad.wasBlocked()) {
      String s = Lang.GUI_TELEPAD_ERROR_BLOCKED.get();
      renderInfoMessage(sx, sy, s, 0xAA0000);
    }

  }

  private void renderInfoMessage(int sx, int sy, @Nonnull String txt, int color) {
    FontRenderer fnt = Minecraft.getMinecraft().fontRenderer;
    fnt.drawString(txt, sx + xSize / 2 - fnt.getStringWidth(txt) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, color);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    super.actionPerformed(button);

    if (button.id == ID_TELEPORT_BUTTON) {
      TelepadTarget target = targetList.getSelectedElement();
      if (target != null) {
        int targetID = dialingDevice.getTargets().indexOf(target);
        if (targetID >= 0) {
          doTeleport(telepad.getPos(), targetID, true);
        }
      }
    }
  }

}
