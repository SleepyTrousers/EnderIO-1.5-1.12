package crazypants.enderio.base.handler.darksteel.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AnvilSubGui implements IInventoryChangedListener {

  private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");

  private final DSUContainer anvil;
  private final DSUGui parent;

  private final GuiTextField nameField;
  private String lastName = "";

  /**
   * Height of the anvil's gui texture without inventory slots
   */
  private static final int ANVIL_HEIGHT = 80;
  /**
   * How far to shift the anvil GUI down from its original position. It's smaller then our GUI so it'd look funny if it stayed at the top.
   */
  private static final int ANVIL_Y_OFFSET = AnvilSubContainer.ANVIL_Y_OFFSET;
  /**
   * How many pixels to remove at the left, right and top of the anvil texture. This should just cut off the GUI border.
   */
  private static final int ANVIL_BORDER = 5;

  AnvilSubGui(DSUContainer container, DSUGui parent) {
    this.anvil = container;
    this.parent = parent;
    anvil.anvil.inputSlots.addInventoryChangeListener(this);
    nameField = new GuiTextField(0, parent.getFontRenderer(), 62, 24 + ANVIL_Y_OFFSET, 103, 12);
    nameField.setTextColor(-1);
    nameField.setDisabledTextColour(-1);
    nameField.setEnableBackgroundDrawing(false);
    nameField.setMaxStringLength(35);
  }

  public void initGui() {
    Keyboard.enableRepeatEvents(true);
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    anvil.anvil.inputSlots.removeInventoryChangeListener(this);
  }

  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    final FontRenderer fontRenderer = parent.getFontRenderer();
    fontRenderer.drawString(I18n.format("container.repair"), 60, 6 + ANVIL_Y_OFFSET, 4210752);

    if (anvil.anvil.getMaximumCost() > 0) {
      int i = 8453920;
      boolean flag = true;
      String s = I18n.format("container.repair.cost", anvil.anvil.getMaximumCost());

      if (anvil.anvil.getMaximumCost() >= AnvilSubContainer.getMaxCost() && !parent.mc.player.capabilities.isCreativeMode) {
        s = I18n.format("container.repair.expensive");
        i = 16736352;
      } else if (!this.anvil.getSlot(2).getHasStack()) {
        flag = false;
      } else if (!this.anvil.getSlot(2).canTakeStack(anvil.anvil.player)) {
        i = 16736352;
      }

      if (flag) {
        int j = -16777216 | (i & 16579836) >> 2 | i & -16777216;
        int k = parent.getXSize() - 8 - fontRenderer.getStringWidth(s);

        if (fontRenderer.getUnicodeFlag()) {
          Gui.drawRect(k - 3, 65, parent.getXSize() - 7, 77 + ANVIL_Y_OFFSET, -16777216);
          Gui.drawRect(k - 2, 66, parent.getXSize() - 8, 76 + ANVIL_Y_OFFSET, -12895429);
        } else {
          fontRenderer.drawString(s, k, 68 + ANVIL_Y_OFFSET, j);
          fontRenderer.drawString(s, k + 1, 67 + ANVIL_Y_OFFSET, j);
          fontRenderer.drawString(s, k + 1, 68 + ANVIL_Y_OFFSET, j);
        }

        fontRenderer.drawString(s, k, 67 + ANVIL_Y_OFFSET, i);
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
    int i = (parent.width - parent.getXSize()) / 2;
    int j = (parent.height - parent.getYSize()) / 2;
    nameField.mouseClicked(mouseX - i, mouseY - j, mouseButton);
  }

  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    parent.mc.getTextureManager().bindTexture(ANVIL_RESOURCE);
    int i = (parent.width - parent.getXSize()) / 2;
    int j = (parent.height - parent.getYSize()) / 2;
    parent.drawTexturedModalRect(i + ANVIL_BORDER, j + ANVIL_Y_OFFSET + ANVIL_BORDER, ANVIL_BORDER, ANVIL_BORDER, parent.getXSize() - 2 * ANVIL_BORDER,
        ANVIL_HEIGHT - ANVIL_BORDER);
    parent.drawTexturedModalRect(i + 59, j + 20 + ANVIL_Y_OFFSET, 0, 166 /* ysize */ + (anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

    if ((anvil.getSlot(0).getHasStack() || anvil.getSlot(1).getHasStack()) && !anvil.getSlot(2).getHasStack()) {
      parent.drawTexturedModalRect(i + 99, j + 45 + ANVIL_Y_OFFSET, parent.getXSize(), 0, 28, 21);
    }
    parent.bindGuiTexture();
  }

  @Override
  public void onInventoryChanged(@Nonnull IInventory invBasic) {
    ItemStack stack = invBasic.getStackInSlot(0);
    String newName = stack.isEmpty() ? "" : stack.getDisplayName();
    if (!newName.equals(lastName)) {
      nameField.setText(newName);
      lastName = newName;
    }
    nameField.setEnabled(!stack.isEmpty());

    if (!stack.isEmpty()) {
      renameItem();
    }
  }

}
