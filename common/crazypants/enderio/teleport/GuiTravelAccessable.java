package crazypants.enderio.teleport;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.gui.CheckBoxEIO;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;
import crazypants.gui.GuiContainerBase;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class GuiTravelAccessable extends GuiContainerBase {

  private static final int ID_PUBLIC = 0;
  private static final int ID_PRIVATE = 1;
  private static final int ID_PROTECTED = 2;

  private CheckBoxEIO publicCB;
  private CheckBoxEIO privateCB;
  private CheckBoxEIO protectedCB;

  private String publicStr;
  private String privateStr;
  private String protectedStr;

  private ITravelAccessable te;
  private int col0x;
  private int col1x;
  private int col2x;

  private World world;

  public GuiTravelAccessable(InventoryPlayer playerInv, ITravelAccessable te, World world) {
    super(new ContainerTravelAccessable(playerInv, te, world));
    this.te = te;
    this.world = world;

    publicStr = Lang.localize("gui.travelAccessable.public");
    privateStr = Lang.localize("gui.travelAccessable.private");
    protectedStr = Lang.localize("gui.travelAccessable.protected");

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    fontRenderer = fr;

    col1x = 88;
    col0x = (col1x - fontRenderer.getStringWidth(protectedStr) / 2) / 2;
    col2x = (col1x + fontRenderer.getStringWidth(protectedStr) / 2);
    col2x += (176 - col2x) / 2;

    int x = 0;
    int y = 20;

    x = col0x - 8;
    privateCB = new CheckBoxEIO(this, ID_PRIVATE, x, y);
    privateCB.setSelected(te.getAccessMode() == AccessMode.PRIVATE);

    x = col1x - 8;
    protectedCB = new CheckBoxEIO(this, ID_PROTECTED, x, y);
    protectedCB.setSelected(te.getAccessMode() == AccessMode.PROTECTED);

    x = col2x - 8;
    publicCB = new CheckBoxEIO(this, ID_PUBLIC, x, y);
    publicCB.setSelected(te.getAccessMode() == AccessMode.PUBLIC);

  }

  @Override
  protected void actionPerformed(GuiButton b) {
    privateCB.setSelected(b.id == ID_PRIVATE);
    protectedCB.setSelected(b.id == ID_PROTECTED);
    publicCB.setSelected(b.id == ID_PUBLIC);

    AccessMode curMode = b.id == ID_PRIVATE ? AccessMode.PRIVATE : b.id == ID_PROTECTED ? AccessMode.PROTECTED : AccessMode.PUBLIC;
    te.setAccessMode(curMode);

    BlockCoord bc = te.getLocation();
    Packet packet = TravelPacketHandler.createAccessModePacket(bc.x, bc.y, bc.z, curMode);
    PacketDispatcher.sendPacketToServer(packet);
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.clear();

    publicCB.setPaintSelectedBorder(false);
    publicCB.onGuiInit();
    privateCB.onGuiInit();
    protectedCB.onGuiInit();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/travelAccessable.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int col = ColorUtil.getRGB(Color.white);
    int x = sx;
    int y = sy + 8;

    x = sx + col0x - fontRenderer.getStringWidth(privateStr) / 2;
    fontRenderer.drawStringWithShadow(privateStr, x, y, col);

    x = sx + col1x - fontRenderer.getStringWidth(protectedStr) / 2;
    fontRenderer.drawStringWithShadow(protectedStr, x, y, col);

    x = sx + col2x - fontRenderer.getStringWidth(publicStr) / 2;
    fontRenderer.drawStringWithShadow(publicStr, x, y, col);

  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);

    if(te.getAccessMode() != AccessMode.PROTECTED) {
      int sx = (width - xSize) / 2;
      int sy = (height - ySize) / 2;
      RenderUtil.bindTexture("enderio:textures/gui/travelAccessable.png");
      GL11.glColor4f(1, 1, 1, 0.75f);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      drawTexturedModalRect(43, 42, 5, 5, 90, 18);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glColor4f(1, 1, 1, 1);
    }
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {
    super.drawScreen(par1, par2, par3);
  }

}
