package crazypants.enderio.teleport.telepad;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiContainerBase;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiTelePad extends GuiContainerBase implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 99;

  ToggleTravelButton switchButton;

  private World world;
  private TileTelePad te;

  private GuiTextField x, y, z;

  public GuiTelePad(InventoryPlayer playerInv, TileTelePad te, World world) {
    super(new ContainerTelePad(playerInv));
    this.world = world;
    this.te = te;

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    x = new GuiTextField(fr, 7, 12, 90, 16);
    y = new GuiTextField(fr, 7, 32, 90, 16);
    z = new GuiTextField(fr, 7, 52, 90, 16);

    textFields.addAll(Lists.newArrayList(x, y, z));

    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, 150, 10, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(Lang.localize("gui.telepad.configure.travel"));

    this.ySize += 19;
  }

  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();

  }

  @Override
  public void updateScreen() {
    super.updateScreen();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/telePad.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    
    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  @Override
  public void switchGui() {
    mc.thePlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD_TRAVEL, world, te.xCoord, te.yCoord, te.zCoord);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiHandler.GUI_ID_TELEPAD_TRAVEL));
  }
}
