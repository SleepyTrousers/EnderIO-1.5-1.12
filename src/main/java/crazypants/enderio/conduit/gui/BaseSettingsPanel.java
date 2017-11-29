package crazypants.enderio.conduit.gui;

import java.awt.Color;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.packet.PacketConnectionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class BaseSettingsPanel implements ITabPanel {

  static final int PREV_MODE_B = 327;
  static final int NEXT_MODE_B = 328;

  protected final IconEIO icon;
  protected final GuiExternalConnection gui;
  protected final IConduit con;
  protected final String typeName;

  protected MultiIconButton leftArrow;
  protected MultiIconButton rightArrow;
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

    modeLabel = EnderIO.lang.localize("gui.conduit.ioMode");

    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    int x = gap * 3 + fr.getStringWidth(modeLabel);
    int y = 8;// + fr.FONT_HEIGHT;

    leftArrow = MultiIconButton.createLeftArrowButton(gui, PREV_MODE_B, x, y);

    x += leftArrow.getWidth() + gap + getLongestModeStringWidth() + gap;
    rightArrow = MultiIconButton.createRightArrowButton(gui, NEXT_MODE_B, x, y);

    customTop = top + gap * 5 + fr.FONT_HEIGHT * 2;
    customTop -= 16;
    //customTop = top;

  }

  @Override
  public void onGuiInit(int leftIn, int topIn, int widthIn, int heightIn) {
    this.left = leftIn;
    this.top = topIn;
    this.width = widthIn;
    this.height = heightIn;

    leftArrow.onGuiInit();
    rightArrow.onGuiInit();

    connectionModeChanged(con.getConnectionMode(gui.getDir()));

    initCustomOptions();
  }

  protected void initCustomOptions() {
  }

  @Override
  public void deactivate() {
  }
  
  @Override
  public void mouseClicked(int x, int y, int par3) {    
  }

  @Override
  public void keyTyped(char par1, int par2) {
  }

  @Override
  public void updateScreen() {    
  }

  @Override
  public IWidgetIcon getIcon() {
    return icon;
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    if(guiButton.id == PREV_MODE_B) {
      con.setConnectionMode(gui.getDir(), con.getPreviousConnectionMode(gui.getDir()));
      PacketHandler.INSTANCE.sendToServer(new PacketConnectionMode(con, gui.getDir()));
      connectionModeChanged(con.getConnectionMode(gui.getDir()));

    } else if(guiButton.id == NEXT_MODE_B) {
      con.setConnectionMode(gui.getDir(), con.getNextConnectionMode(gui.getDir()));
      PacketHandler.INSTANCE.sendToServer(new PacketConnectionMode(con, gui.getDir()));
      connectionModeChanged(con.getConnectionMode(gui.getDir()));
    }
  }

  protected void connectionModeChanged(ConnectionMode conectionMode) {
  }

  @Override
  public void render(float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    int rgb = ColorUtil.getRGB(Color.darkGray);
    int x = left;    
    int y = gui.getGuiTop() + 13;
    gui.getFontRenderer().drawString(modeLabel, x, y, rgb);

    String modeString = con.getConnectionMode(gui.getDir()).getLocalisedName();
    x += gap + leftArrow.getWidth() + fr.getStringWidth(modeLabel) + gap;

    GlStateManager.color(1, 1, 1);    
    IconEIO.MODE_BACKGROUND.getMap().render(IconEIO.MODE_BACKGROUND, x - gap, y - (fr.FONT_HEIGHT / 2) - 1, getLongestModeStringWidth() + gap * 2, leftArrow.getHeight(), 0, true);

    int move = (getLongestModeStringWidth() - fr.getStringWidth(modeString)) / 2;
    x += move;
    rgb = ColorUtil.getRGB(Color.white);
    gui.getFontRenderer().drawString(modeString, x, y, rgb);

    renderCustomOptions(y + gap + fr.FONT_HEIGHT + gap, par1, par2, par3);
  }

  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {

  }

  private int getLongestModeStringWidth() {
    int maxWidth = 0;
    for (ConnectionMode mode : ConnectionMode.values()) {
      int stringWidth = gui.getFontRenderer().getStringWidth(mode.getLocalisedName());
      if(stringWidth > maxWidth) {
        maxWidth = stringWidth;
      }
    }
    return maxWidth;
  }

  protected String getTypeName() {
    return typeName;
  }

}
