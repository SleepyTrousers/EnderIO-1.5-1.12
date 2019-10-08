package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextFormatting;

import static crazypants.enderio.base.machine.gui.GuiMachineBase.BUTTON_SIZE;

public class GuiDialingDeviceNoTelepad extends GuiContainerBaseEIO<TileDialingDevice> {

  private final int progressY = 110;
  private final @Nonnull ToggleButton showRangeB;

  public GuiDialingDeviceNoTelepad(@Nonnull InventoryPlayer playerInv, @Nonnull TileDialingDevice te) {
    super(te, new ContainerDialingDevice(playerInv, te), "dialing_device");
    this.ySize = 220;

    int x = getXSize() - 5 - BUTTON_SIZE - 2;
    showRangeB = new ToggleButton(this, -1, x, 8, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
      }
    });
  }

  protected int getPowerOutputValue() {
    return getOwner().getEnergy().getMaxUsage();
  }

  protected void updatePowerBarTooltip(List<String> text) {
    text.add(Lang.GUI_TELEPAD_MAX.get(LangPower.RFt(getPowerOutputValue())));
    text.add(LangPower.RF(getOwner().getEnergy().getEnergyStored(), getOwner().getEnergy().getMaxEnergyStored()));
  }

  @Override
  public void initGui() {
    super.initGui();
    showRangeB.onGuiInit();
    showRangeB.setSelected(getOwner().isShowingRange());
    ((ContainerDialingDevice) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b == showRangeB) {
      getOwner().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

    String txt = TextFormatting.DARK_RED + "No Telepad"; // FIXME I18N
    renderInfoMessage(sx, sy, txt, 0x000000);
  }

  private void renderInfoMessage(int sx, int sy, @Nonnull String txt, int color) {
    FontRenderer fnt = Minecraft.getMinecraft().fontRenderer;
    fnt.drawString(txt, sx + xSize / 2 - fnt.getStringWidth(txt) / 2, sy + progressY + fnt.FONT_HEIGHT + 6, color);
  }

}
