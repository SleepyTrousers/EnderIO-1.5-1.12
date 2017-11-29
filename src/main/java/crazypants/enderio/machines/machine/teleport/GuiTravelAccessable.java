package crazypants.enderio.machines.machine.teleport;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.ITravelAccessable.AccessMode;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.network.GuiPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiTravelAccessable<T extends TileEntity & ITravelAccessable> extends GuiContainerBaseEIO {

  private static final int ID_PUBLIC = 0;
  private static final int ID_PRIVATE = 1;
  private static final int ID_PROTECTED = 2;

  private CheckBox publicCB;
  private CheckBox privateCB;
  private CheckBox protectedCB;

  private TextFieldEnder tf;

  private String publicStr;
  private String privateStr;
  private String protectedStr;

  protected T te;
  private int col0x;
  private int col1x;
  private int col2x;

  protected World world;

  public GuiTravelAccessable(InventoryPlayer playerInv, T te, World world) {
    this(te, new ContainerTravelAccessable(playerInv, te, world));
  }

  public GuiTravelAccessable(T te, ContainerTravelAccessable container) {
    super(container, "travel_accessable");
    this.te = te;
    this.world = container.world;

    publicStr = EnderIO.lang.localize("gui.travelAccessable.public");
    privateStr = EnderIO.lang.localize("gui.travelAccessable.private");
    protectedStr = EnderIO.lang.localize("gui.travelAccessable.protected");

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    tf = new TextFieldEnder(fr, 28, 10, 90, 16);

    col1x = 88;
    col0x = (col1x - fr.getStringWidth(protectedStr) / 2) / 2;
    col2x = (col1x + fr.getStringWidth(protectedStr) / 2);
    col2x += (176 - col2x) / 2;

    int x = 0;
    int y = 50;

    x = col0x - 8;
    privateCB = new CheckBox(this, ID_PRIVATE, x, y);
    privateCB.setSelected(te.getAccessMode() == AccessMode.PRIVATE);

    x = col1x - 8;
    protectedCB = new CheckBox(this, ID_PROTECTED, x, y);
    protectedCB.setSelected(te.getAccessMode() == AccessMode.PROTECTED);

    x = col2x - 8;
    publicCB = new CheckBox(this, ID_PUBLIC, x, y);
    publicCB.setSelected(te.getAccessMode() == AccessMode.PUBLIC);

    ySize = 185;
    
    textFields.add(tf);
  }

  @Override
  protected void actionPerformed(GuiButton b) {
    privateCB.setSelected(b.id == ID_PRIVATE);
    protectedCB.setSelected(b.id == ID_PROTECTED);
    publicCB.setSelected(b.id == ID_PUBLIC);

    AccessMode curMode = b.id == ID_PRIVATE ? AccessMode.PRIVATE : b.id == ID_PROTECTED ? AccessMode.PROTECTED : AccessMode.PUBLIC;
    te.setAccessMode(curMode);

    GuiPacket.send(this, ContainerTravelAccessable.EXEC_ACCESS_MODE, curMode);
  }

  @Override
  public void initGui() {
    super.initGui();
    buttonList.clear();

    publicCB.setPaintSelectedBorder(false);
    publicCB.onGuiInit();
    privateCB.onGuiInit();
    protectedCB.onGuiInit();

    tf.setMaxStringLength(32);
    tf.setFocused(true);
    String txt = te.getLabel();
    if(txt != null && txt.length() > 0) {
      tf.setText(txt);
    }

    ((ContainerTravelAccessable) inventorySlots).addGhostSlots(getGhostSlotHandler().getGhostSlots());

  }
  
  @Override
  public void updateScreen() {
    super.updateScreen();
  }

  @Override
  public void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);
  }

  @Override
  public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int col = ColorUtil.getRGB(Color.white);
    int x = sx;
    int y = sy + 38;

    FontRenderer fontRenderer = getFontRenderer();
    x = sx + col0x - fontRenderer.getStringWidth(privateStr) / 2;
    fontRenderer.drawStringWithShadow(privateStr, x, y, col);

    x = sx + col1x - fontRenderer.getStringWidth(protectedStr) / 2;
    fontRenderer.drawStringWithShadow(protectedStr, x, y, col);

    x = sx + col2x - fontRenderer.getStringWidth(publicStr) / 2;
    fontRenderer.drawStringWithShadow(publicStr, x, y, col);
    checkLabelForChange();

    super.drawGuiContainerBackgroundLayer(f, i, j);
  }

  private void checkLabelForChange() {
    String newTxt = tf.getText();
    if(newTxt != null && newTxt.length() == 0) {
      newTxt = null;
    }

    String curText = te.getLabel();
    if(curText != null && curText.length() == 0) {
      curText = null;
    }

    boolean changed = false;
    if(newTxt == null) {
      if(curText == null) {
        changed = false;
      } else {
        changed = true;
      }
    } else {
      changed = !newTxt.equals(curText);
    }
    if(!changed) {
      return;
    }
    te.setLabel(newTxt);
    GuiPacket.send(this, ContainerTravelAccessable.EXEC_LABEL, newTxt);
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);

    if(te.getAccessMode() != AccessMode.PROTECTED) {
      bindGuiTexture();
      GL11.glColor4f(1, 1, 1, 0.75f);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      drawTexturedModalRect(43, 72, 5, 35, 90, 18);
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
