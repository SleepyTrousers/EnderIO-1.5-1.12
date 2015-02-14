package crazypants.enderio.teleport.telepad;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.TextFieldEIO;
import crazypants.enderio.gui.TextFieldEIO.ICharFilter;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiContainerBase;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class GuiTelePad extends GuiContainerBase implements IToggleableGui {

  private class CoordCharFilter implements ICharFilter {
    private final TextFieldEIO f;
    public CoordCharFilter(TextFieldEIO f) {
      this.f = f;
    }
    
    @Override
    public boolean passesFilter(char c) {
      return (c== '-' && Strings.isNullOrEmpty(f.getText())) || TextFieldEIO.FILTER_NUMERIC.passesFilter(c);
    }
  }
  
  private static final int ID_SWITCH_BUTTON = 99;
  private static final int ID_TELEPORT_BUTTON = 100;

  ToggleTravelButton switchButton;
  GuiButton teleportButton;

  private World world;
  private TileTelePad te;

  private TextFieldEIO xTF, yTF, zTF;
  
  public static int SWITCH_X = 155, SWITCH_Y = 5;

  public GuiTelePad(InventoryPlayer playerInv, TileTelePad te, World world) {
    super(new ContainerTelePad(playerInv));
    this.world = world;
    this.te = te;
    ySize += 19;

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    int x = 24;
    int y = 10;
    xTF = new TextFieldEIO(fr, x, y, xSize - x * 2, 16);
    yTF = new TextFieldEIO(fr, x, y + xTF.height + 2, xSize - x * 2, 16);
    zTF = new TextFieldEIO(fr, x, y + (xTF.height * 2) + 4, xSize - x * 2, 16);

    xTF.setCharFilter(new CoordCharFilter(xTF)).setText(Integer.toString(te.getX()));
    yTF.setCharFilter(new CoordCharFilter(yTF)).setText(Integer.toString(te.getY()));
    zTF.setCharFilter(new CoordCharFilter(zTF)).setText(Integer.toString(te.getZ()));

    textFields.addAll(Lists.newArrayList(xTF, yTF, zTF));

    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, SWITCH_X, SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(Lang.localize("gui.telepad.configure.travel"));
  }

  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();

    String text = Lang.localize("gui.telepad.teleport");
    int width = getFontRenderer().getStringWidth(text) + 10;

    int x = guiLeft + (xSize / 2) - (width / 2);
    int y = guiTop + 70;
    
    teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, width, 20, text);
    addButton(teleportButton);
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
  }
  
  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    updateCoords();
  }

  private void updateCoords() {
    BlockCoord bc = new BlockCoord(xTF.getText(), yTF.getText(), zTF.getText());
    if(bc.x != te.getX() || bc.y != te.getY() || bc.z != te.getZ()) {
      te.setX(bc.x);
      te.setY(bc.y);
      te.setZ(bc.z);
      PacketHandler.INSTANCE.sendToServer(new PacketUpdateCoords(te, bc));
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/telePad.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    
    FontRenderer fnt = getFontRenderer();

    String[] text = { "X", "Y", "Z" };
    for (int i = 0; i < text.length; i++) {
      GuiTextField f = textFields.get(i);
      fnt.drawString(text[i], f.xPosition - (fnt.getStringWidth(text[i]) / 2) - 6, f.yPosition + ((f.height - fnt.FONT_HEIGHT) / 2), 0x000000);
    }

    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  @Override
  public void switchGui() {
    mc.thePlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD_TRAVEL, world, te.xCoord, te.yCoord, te.zCoord);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiHandler.GUI_ID_TELEPAD_TRAVEL));
  }
  
  @Override
  protected void actionPerformed(GuiButton button) {
    super.actionPerformed(button);
    
    if (button.id == ID_TELEPORT_BUTTON) {
      te.teleportSpecific(mc.thePlayer);
    }
  }
}
