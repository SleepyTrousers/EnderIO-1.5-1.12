package crazypants.enderio.conduit.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.conduit.ConduitPacketHandler;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.ColorUtil;
import crazypants.util.Lang;

public class BaseSettingsPanel implements ISettingsPanel {

  static final int PREV_MODE_B = 327;
  static final int NEXT_MODE_B = 328;

  protected final IconEIO icon;
  protected final GuiExternalConnection gui;
  protected final IConduit con;
  protected final String typeName;

  protected IconButtonEIO leftArrow;
  protected IconButtonEIO rightArrow;
  protected String modeLabel;

  protected int left = 0;
  protected int top = 0;
  protected int width = 0;
  protected int height = 0;

  protected int gap = 5;

  protected int customTop = 0;

  protected BaseSettingsPanel(IconEIO icon, String typeName, GuiExternalConnection gui, IConduit con) {
    this.icon = icon;
    this.typeName = typeName;
    this.gui = gui;
    this.con = con;

    modeLabel = Lang.localize("gui.conduit.ioMode");

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    int x = gap * 3 + fr.getStringWidth(modeLabel);
    int y = gap * 3 + fr.FONT_HEIGHT;

    leftArrow = new IconButtonEIO(gui, PREV_MODE_B, x, y, IconEIO.LEFT_ARROW);
    leftArrow.setSize(8, 16);

    x += leftArrow.getWidth() + gap + getLongestModeStringWidth() + gap;
    rightArrow = new IconButtonEIO(gui, NEXT_MODE_B, x, y, IconEIO.RIGHT_ARROW);
    rightArrow.setSize(8, 16);

    customTop = top + gap * 5 + fr.FONT_HEIGHT * 2;

  }

  @Override
  public void onGuiInit(int left, int top, int width, int height) {
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;

    leftArrow.onGuiInit();
    rightArrow.onGuiInit();

    FontRenderer fr = gui.getFontRenderer();
    connectionModeChanged(con.getConectionMode(gui.dir));

    initCustomOptions();
  }

  protected void initCustomOptions() {
  }

  @Override
  public void deactivate() {
  }

  @Override
  public IconEIO getIcon() {
    return icon;
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    if(guiButton.id == PREV_MODE_B) {
      con.setConnectionMode(gui.dir, con.getPreviousConnectionMode(gui.dir));
      Packet pkt = ConduitPacketHandler.createConnectionModePacket(gui.bundle, con, gui.dir);
      PacketDispatcher.sendPacketToServer(pkt);
      connectionModeChanged(con.getConectionMode(gui.dir));

    } else if(guiButton.id == NEXT_MODE_B) {
      con.setConnectionMode(gui.dir, con.getNextConnectionMode(gui.dir));
      Packet pkt = ConduitPacketHandler.createConnectionModePacket(gui.bundle, con, gui.dir);
      PacketDispatcher.sendPacketToServer(pkt);
      connectionModeChanged(con.getConectionMode(gui.dir));
    }
  }

  protected void connectionModeChanged(ConnectionMode conectionMode) {
  }

  @Override
  public void render(float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    int rgb = ColorUtil.getRGB(Color.darkGray);
    int x = left + (width - fr.getStringWidth(getTypeName())) / 2;

    fr.drawString(getTypeName(), x, top, rgb);

    x = left;
    int y = top + gap + fr.FONT_HEIGHT + gap;
    gui.getFontRenderer().drawString(modeLabel, x, y, rgb);

    String modeString = con.getConectionMode(gui.dir).getLocalisedName();
    x += gap + leftArrow.getWidth() + fr.getStringWidth(modeLabel) + gap;

    GL11.glColor3f(1, 1, 1);
    IconEIO icon = new IconEIO(10, 60, 64, 20);
    icon.renderIcon(x - gap, y - (fr.FONT_HEIGHT / 2) - 1, getLongestModeStringWidth() + gap * 2, leftArrow.getHeight(), 0, true);

    int move = (getLongestModeStringWidth() - fr.getStringWidth(modeString)) / 2;
    x += move;
    rgb = ColorUtil.getRGB(Color.white);
    gui.getFontRenderer().drawString(modeString, x, y, rgb);

    renderCustomOptions(y + gap + fr.FONT_HEIGHT + gap, par1, par2, par3);
  }

  protected void renderCustomOptions(int top, float par1, int par2, int par3) {

  }

  private int getLongestModeStringWidth() {
    int maxWidth = 0;
    for (ConnectionMode mode : ConnectionMode.values()) {
      int width = gui.getFontRenderer().getStringWidth(mode.getLocalisedName());
      if(width > maxWidth) {
        maxWidth = width;
      }
    }
    return maxWidth;
  }

  protected String getTypeName() {
    return typeName;
  }

}
