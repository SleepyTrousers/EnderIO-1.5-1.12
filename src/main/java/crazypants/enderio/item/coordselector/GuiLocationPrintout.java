package crazypants.enderio.item.coordselector;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.GuiScreenBase;
import com.enderio.core.client.gui.button.TooltipButton;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;
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
  private final @Nonnull TelepadTarget target;
  private final EntityEquipmentSlot slot;
  private final int paperSlot;

  private boolean isCancelled = false;

  public GuiLocationPrintout(@Nonnull TelepadTarget target, @Nonnull ItemStack stack, int paperSlot) {
    this(target, stack, null, paperSlot);
  }

  public GuiLocationPrintout(@Nonnull TelepadTarget target, @Nonnull EntityPlayer player, @Nonnull EntityEquipmentSlot slot) {
    this(target, player.getItemStackFromSlot(slot), slot, -1);
  }

  private GuiLocationPrintout(@Nonnull TelepadTarget target, @Nonnull ItemStack stack, EntityEquipmentSlot slot, int paperSlot) {

    this.slot = slot;
    this.stack = stack;
    this.paperSlot = paperSlot;
    this.target = target;

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
    tf.setText(txt);
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
    String newTxt = tf.getText().trim();
    String curText = target.getName().trim();

    if (newTxt.equals(curText)) {
      return;
    }
    target.setName(newTxt);
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
  @Nonnull
  public GhostSlotHandler getGhostSlotHandler() {
    // should be unused here. TODO go over that interface method again...
    return new GhostSlotHandler();
  }

}
