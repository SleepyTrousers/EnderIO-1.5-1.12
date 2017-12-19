package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.GuiScreenBase;
import com.enderio.core.client.gui.button.TooltipButton;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.item.coordselector.PacketUpdateLocationPrintout;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiLocationPrintout extends GuiScreenBase {

  private static final @Nonnull ResourceLocation GUI_BACKGROUND = EnderIO.proxy.getGuiTexture("location_printout");

  private final TextFieldEnder tf;
  private TooltipButton okB;

  private final @Nonnull ItemStack stack;
  private final TelepadTarget target;
  private final @Nullable EntityEquipmentSlot slot;
  private final int paperSlot;

  private boolean isCancelled = false;

  private final @Nonnull GhostSlotHandler ghostSlots = new GhostSlotHandler();

  public GuiLocationPrintout(@Nonnull ItemStack stack, int paperSlot) {
    this(stack, null, paperSlot);
  }

  public GuiLocationPrintout(@Nonnull EntityPlayer player, @Nonnull EntityEquipmentSlot slot) {
    this(player.getItemStackFromSlot(slot), slot, -1);
  }

  public GuiLocationPrintout(@Nonnull ItemStack stack, @Nullable EntityEquipmentSlot slot, int paperSlot) {

    this.slot = slot;
    this.stack = stack;
    this.paperSlot = paperSlot;
    target = TelepadTarget.readFromNBT(stack);

    xSize = 176;
    ySize = 116;

    int tfWidth = 90;
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    tf = new TextFieldEnder(fr, xSize / 2 - tfWidth / 2, 20, tfWidth, 16);
    tf.setMaxStringLength(32);

    okB = new TooltipButton(this, 0, xSize - 30, ySize - 30, 20, 20, "Ok");
  }

  @Override
  public void initGui() {
    super.initGui();

    tf.setFocused(true);
    String txt = target.getName();
    if (txt.length() > 0) {
      tf.setText(txt);
    }
    tf.init(this);

    okB.onGuiInit();

  }

  @Override
  public void updateScreen() {
    tf.updateCursorCounter();
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
    mc.player.closeScreen();
  }

  @Override
  protected void keyTyped(char c, int key) throws IOException {
    if (key == 1 || key == 28) { // esc + enter
      isCancelled = key == 1;
      mc.player.closeScreen();
      return;
    }
    tf.textboxKeyTyped(c, key);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    tf.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  protected void drawBackgroundLayer(float par3, int par1, int par2) {

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_BACKGROUND);
    drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, this.xSize, this.ySize);

    checkLabelForChange();
    tf.drawTextBox();

    FontRenderer fontRenderer = getFontRenderer();
    int col = ColorUtil.getRGB(Color.white);
    String txt;
    int midX = getGuiLeft() + xSize / 2;
    int y = getGuiTop() + 48;

    BlockPos loc = target.getLocation();
    txt = loc.getX() + " " + loc.getY() + " " + loc.getZ();
    int x = midX - fontRenderer.getStringWidth(txt) / 2;

    fontRenderer.drawStringWithShadow(txt, x, y, col);

    txt = TelepadTarget.getDimenionName(target.getDimension());
    y += fontRenderer.FONT_HEIGHT + 4;
    x = midX - fontRenderer.getStringWidth(txt) / 2;
    fontRenderer.drawStringWithShadow(txt, x, y, col);

  }

  private void checkLabelForChange() {
    String newTxt = tf.getText();
    if (newTxt.length() == 0) {
      newTxt = null;
    }

    String curText = target.getName();
    if (curText.length() == 0) {
      curText = null;
    }

    boolean changed = false;
    if (newTxt == null) {
      if (curText == null) {
        changed = false;
      } else {
        changed = true;
      }
    } else {
      changed = !newTxt.equals(curText);
    }
    if (!changed) {
      return;
    }
    target.setName(NullHelper.first(newTxt, ""));
    target.writeToNBT(stack);
    if (slot != null) { // update as we go if the stack exists already
      PacketUpdateLocationPrintout p = new PacketUpdateLocationPrintout(stack, slot, paperSlot);
      PacketHandler.INSTANCE.sendToServer(p);
    }
  }

  @Override
  public void onGuiClosed() {
    if (slot == null && !isCancelled) {
      PacketUpdateLocationPrintout p = new PacketUpdateLocationPrintout(stack, slot, paperSlot);
      PacketHandler.INSTANCE.sendToServer(p);
    }
  }

  @Override
  public void clearToolTips() {
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public @Nonnull GhostSlotHandler getGhostSlotHandler() {
    return ghostSlots;
  }

}
