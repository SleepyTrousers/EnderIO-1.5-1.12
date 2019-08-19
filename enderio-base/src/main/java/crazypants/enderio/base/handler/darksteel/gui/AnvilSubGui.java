package crazypants.enderio.base.handler.darksteel.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class AnvilSubGui implements IContainerListener {

  private static final @Nonnull ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");

  private final @Nonnull DSUContainer anvil;
  private final @Nonnull DSUGui parent;

  private GuiTextField nameField;

  private static final int ANVIL_HEIGHT = 100;
  private static final int ANVIL_Y_OFFSET = 10;
  private static final int ANVIL_BORDER = 5;

  AnvilSubGui(@Nonnull DSUContainer container, @Nonnull DSUGui parent) {
    this.anvil = container;
    this.parent = parent;
  }

  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    int i = (parent.width - parent.getXSize()) / 2;
    int j = (parent.height - parent.getYSize()) / 2;
    nameField = new GuiTextField(0, parent.getFontRenderer(), i + 62, j + 24 + ANVIL_Y_OFFSET, 103, 12);
    nameField.setTextColor(-1);
    nameField.setDisabledTextColour(-1);
    nameField.setEnableBackgroundDrawing(false);
    nameField.setMaxStringLength(35);
    anvil.removeListener(this);
    anvil.addListener(this);
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    anvil.removeListener(this);
  }

  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    parent.getFontRenderer().drawString(I18n.format("container.repair"), 60, 6 + ANVIL_Y_OFFSET, 4210752);

    if (anvil.anvil.maximumCost > 0) {
      int i = 8453920;
      boolean flag = true;
      String s = I18n.format("container.repair.cost", anvil.anvil.maximumCost);

      if (anvil.anvil.maximumCost >= 40 && !parent.mc.player.capabilities.isCreativeMode) {
        s = I18n.format("container.repair.expensive");
        i = 16736352;
      } else if (!this.anvil.getSlot(2).getHasStack()) {
        flag = false;
      } else if (!this.anvil.getSlot(2).canTakeStack(anvil.anvil.player)) {
        i = 16736352;
      }

      if (flag) {
        int j = -16777216 | (i & 16579836) >> 2 | i & -16777216;
        int k = parent.getXSize() - 8 - parent.getFontRenderer().getStringWidth(s);

        if (parent.getFontRenderer().getUnicodeFlag()) {
          Gui.drawRect(k - 3, 65, parent.getXSize() - 7, 77 + ANVIL_Y_OFFSET, -16777216);
          Gui.drawRect(k - 2, 66, parent.getXSize() - 8, 76 + ANVIL_Y_OFFSET, -12895429);
        } else {
          parent.getFontRenderer().drawString(s, k, 68 + ANVIL_Y_OFFSET, j);
          parent.getFontRenderer().drawString(s, k + 1, 67 + ANVIL_Y_OFFSET, j);
          parent.getFontRenderer().drawString(s, k + 1, 68 + ANVIL_Y_OFFSET, j);
        }

        parent.getFontRenderer().drawString(s, k, 67 + ANVIL_Y_OFFSET, i);
      }
    }

    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    nameField.drawTextBox();
    GlStateManager.enableBlend();
    GlStateManager.enableLighting();
  }

  protected boolean keyTyped(char typedChar, int keyCode) throws IOException {
    if (nameField.textboxKeyTyped(typedChar, keyCode)) {
      renameItem();
      return true;
    }
    return false;
  }

  private void renameItem() {
    String s = nameField.getText();
    Slot slot = anvil.getSlot(0);

    if (slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName())) {
      s = "";
    }

    anvil.updateItemName(s);
    parent.updateItemName(s);
  }

  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    nameField.mouseClicked(mouseX, mouseY, mouseButton);
  }

  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    parent.mc.getTextureManager().bindTexture(ANVIL_RESOURCE);
    int i = (parent.width - parent.getXSize()) / 2;
    int j = (parent.height - parent.getYSize()) / 2;
    parent.drawTexturedModalRect(i + ANVIL_BORDER, j + ANVIL_Y_OFFSET + ANVIL_BORDER, ANVIL_BORDER, ANVIL_BORDER, parent.getXSize() - 2 * ANVIL_BORDER,
        ANVIL_HEIGHT - ANVIL_BORDER);
    parent.drawTexturedModalRect(i + 59, j + 20 + ANVIL_Y_OFFSET, 0, parent.getYSize() + (anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

    if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
      parent.drawTexturedModalRect(i + 99, j + 45 + ANVIL_Y_OFFSET, parent.getXSize(), 0, 28, 21);
    }
    parent.bindGuiTexture();
  }

  @Override
  public void sendAllContents(@Nonnull Container containerToSend, @Nonnull NonNullList<ItemStack> itemsList) {
    sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
  }

  @Override
  public void sendSlotContents(@Nonnull Container containerToSend, int slotInd, @Nonnull ItemStack stack) {
    if (slotInd == 0) {
      nameField.setText(stack.isEmpty() ? "" : stack.getDisplayName());
      nameField.setEnabled(!stack.isEmpty());

      if (!stack.isEmpty()) {
        renameItem();
      }
    }
  }

  @Override
  public void sendWindowProperty(@Nonnull Container containerIn, int varToUpdate, int newValue) {
  }

  @Override
  public void sendAllWindowProperties(@Nonnull Container containerIn, @Nonnull IInventory inventory) {
  }

}
