package crazypants.enderio.machines.machine.teleport;

import java.awt.Color;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiTravelAuth extends GuiContainerBaseEIO {

  private final @Nonnull String title;
  private final @Nonnull ITravelAccessable ta;

  private boolean failed = false;
  private final @Nonnull EntityPlayer player;

  public GuiTravelAuth(@Nonnull EntityPlayer player, @Nonnull ITravelAccessable te, @Nonnull World world) {
    super(new ContainerTravelAuth(player.inventory), "travel_auth");
    this.ta = te;
    title = Lang.GUI_AUTH_PROMPT.get();
    this.player = player;
  }

  @Override
  public void initGui() {
    super.initGui();
    int sy = (height - ySize) / 2;
    String str = Lang.GUI_AUTH_PROMPT_BUTTON.get();
    int strLen = getFontRenderer().getStringWidth(str);
    GuiButton okB = new GuiButton(0, width / 2 - (strLen / 2) - 5, sy + 50, strLen + 10, 20, str);
    buttonList.clear();
    buttonList.add(okB);
    ((ContainerTravelAuth) inventorySlots).addGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton par1GuiButton) {
    ContainerTravelAuth poo = (ContainerTravelAuth) inventorySlots;
    if (ta.authoriseUser(player, poo.getInv().getInventory())) {
      this.mc.displayGuiScreen((GuiScreen) null);
      this.mc.setIngameFocus();
    } else {
      // System.out.print("GuiTravelAuth.actionPerformed: Password is: ");
      // for (ItemStack is : ta.getPassword()) {
      // System.out.print((is == null ? is : is.getDisplayName()) + ",");
      // }
      // System.out.println();
      // System.out.print("GuiTravelAuth.actionPerformed: I offered: ");
      // for (ItemStack is : poo.enteredPassword) {
      // System.out.print((is == null ? is : is.getDisplayName()) + ",");
      // }
      // System.out.println();
      // System.out.println();
      failed = true;
      poo.dirty = false;
    }
  }

  @Override
  public void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int sw = getFontRenderer().getStringWidth(title);
    getFontRenderer().drawString(title, width / 2 - sw / 2, sy + 12, ColorUtil.getRGB(Color.red));

    ContainerTravelAuth poo = (ContainerTravelAuth) inventorySlots;
    if (poo.dirty) {
      poo.dirty = false;
      failed = false;
    }

    if (failed) {
      drawRect(sx + 43, sy + 27, sx + 43 + 90, sy + 27 + 18, ColorUtil.getARGB(new Color(1f, 0f, 0f, 0.5f)));
    }

    super.drawGuiContainerBackgroundLayer(f, mx, my);
  }

}
