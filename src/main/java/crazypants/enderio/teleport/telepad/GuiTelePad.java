package crazypants.enderio.teleport.telepad;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
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

  ToggleTravelButton switchButton;

  private World world;
  private TileTelePad te;

  private TextFieldEIO x, y, z;

  public GuiTelePad(InventoryPlayer playerInv, TileTelePad te, World world) {
    super(new ContainerTelePad(playerInv));
    this.world = world;
    this.te = te;

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    x = new TextFieldEIO(fr, 20, 10, 120, 16);
    y = new TextFieldEIO(fr, 20, 35, 120, 16);
    z = new TextFieldEIO(fr, 20, 60, 120, 16);
    
    x.setCharFilter(new CoordCharFilter(x)).setText(Integer.toString(te.getX()));
    y.setCharFilter(new CoordCharFilter(y)).setText(Integer.toString(te.getY()));
    z.setCharFilter(new CoordCharFilter(z)).setText(Integer.toString(te.getZ()));

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
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    updateCoords();
  }

  private void updateCoords() {
    Triple<String, String, String> texts = Triple.of(x.getText(), y.getText(), z.getText());
    BlockCoord bc = new BlockCoord(texts.getLeft(), texts.getMiddle(), texts.getRight());
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
      fnt.drawString(text[i], f.xPosition - (fnt.getStringWidth(text[i]) / 2) - 5, f.yPosition + ((f.height - fnt.FONT_HEIGHT) / 2), 0x000000);
    }

    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  @Override
  public void switchGui() {
    mc.thePlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD_TRAVEL, world, te.xCoord, te.yCoord, te.zCoord);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiHandler.GUI_ID_TELEPAD_TRAVEL));
  }
}
