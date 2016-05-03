package crazypants.enderio.conduit.gui;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.network.PacketHandler;

public class GuiExternalConnectionSelector extends GuiScreen {

  Set<EnumFacing> cons;
  IConduitBundle cb;
  String adjacentBlockNames[] = new String[6];
  Point textPositions[] = new Point[6];

  public GuiExternalConnectionSelector(IConduitBundle cb) {
    this.cb = cb;
    cons = new HashSet<EnumFacing>();
    for (IConduit con : cb.getConduits()) {
      if(con instanceof IInsulatedRedstoneConduit) {
        Set<EnumFacing> conCons = con.getConduitConnections();
        for(EnumFacing dir : EnumFacing.VALUES) {
          if(!conCons.contains(dir)) {
            cons.add(dir);
          }
        }
        
      } else {        
        cons.addAll(con.getExternalConnections());
      }
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (keyCode == 1 || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
      mc.thePlayer.closeScreen();
    }

    if (won && keyCode == mc.gameSettings.keyBindForward.getKeyCode()) {
      go(W);
    } else if (son && keyCode == mc.gameSettings.keyBindBack.getKeyCode()) {
      go(S);
    } else if (aon && keyCode == mc.gameSettings.keyBindLeft.getKeyCode()) {
      go(A);
    } else if (don && keyCode == mc.gameSettings.keyBindRight.getKeyCode()) {
      go(D);
    } else if (jon && keyCode == mc.gameSettings.keyBindJump.getKeyCode()) {
      go(EnumFacing.UP);
    } else if (con && keyCode == mc.gameSettings.keyBindSneak.getKeyCode()) {
      go(EnumFacing.DOWN);
    }
  }

  @Override
  protected void actionPerformed(GuiButton b) {
    EnumFacing dir = EnumFacing.values()[b.id];
    go(dir);
  }

  private void go(EnumFacing dir) {
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    BlockCoord loc = cb.getLocation();
    PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(cb.getEntity(), dir));
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), player.worldObj, loc.x, loc.y, loc.z);
  }

  protected String getBlockNameForDirection(EnumFacing direction) {
    Block b = cb.getBundleWorldObj().getBlockState(cb.getLocation().getLocation(direction).getBlockPos()).getBlock();
    if (b != null && b != Blocks.air) {
      return b.getLocalizedName();
    }
    return null;
  }

  @Override
  public void initGui() {
    GuiButton b;
    for (EnumFacing dir : EnumFacing.VALUES) {
      Point p = getOffsetForDir(dir, cons.contains(dir));
      adjacentBlockNames[dir.ordinal()] = getBlockNameForDirection(dir);
      textPositions[dir.ordinal()] = new Point(p.x, p.y + 20);
      b = new GuiButton(dir.ordinal(), p.x, p.y, 60, 20, dir.toString());
      buttonList.add(b);
      if(!cons.contains(dir)) {
        b.enabled = false;
      }
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {

    drawDefaultBackground();

    super.drawScreen(par1, par2, par3);

    int butHeight = 20;
    String txt = "Select Connection to Adjust";
    int x = width / 2 - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(txt) / 2);
    int y = height / 2 - butHeight * 3 - 5;
        
    drawString(Minecraft.getMinecraft().fontRendererObj, txt, x, y, ColorUtil.getARGB(Color.white));
    for (int i = 0; i < 6; i++) {
      String blockName = adjacentBlockNames[i];
      if (blockName == null) {
        continue;
      }
      int textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(blockName);
      drawString(Minecraft.getMinecraft().fontRendererObj, blockName, textPositions[i].x + 60 / 2 - textWidth / 2, textPositions[i].y, ColorUtil.getARGB(Color.gray));
    }

    if (Minecraft.getMinecraft().thePlayer.getName().contains("direwolf20") && ((EnderIO.proxy.getTickCount() / 16) & 1) == 1) {
      txt = "You can also right-click the connector directly";
      x = width / 2 - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(txt) / 2);
      y = height / 2 + butHeight * 3 - 5;
      drawString(Minecraft.getMinecraft().fontRendererObj, txt, x, y, ColorUtil.getARGB(Color.white));
    }
  }

  private EnumFacing W, S, A, D;
  private float w, s, a, d;
  private boolean won, son, aon, don, jon, con;

  private static final float deg2rad = (float) (2 * Math.PI / 360);
  private static final float headg2rad = (float) (2 * Math.PI / 4);

  private Point getOffsetForDir(EnumFacing dir, boolean enabled) {
    int mx = width / 2;
    int my = height / 2;
    int butWidth = 60;
    int butHeight = 20;

    if (dir.getFrontOffsetY() == 0) {

      float playerAngle = Minecraft.getMinecraft().thePlayer.rotationYaw * deg2rad;
      float dirAngle = dir.getHorizontalIndex() * headg2rad;
      float buttonAngle = dirAngle - playerAngle - 90 * deg2rad;

      int ax = (int) (MathHelper.cos((buttonAngle)) * butWidth);
      int ay = (int) (MathHelper.sin((buttonAngle)) * butHeight * 2);

      int x = mx - butWidth / 2 + ax;
      int y = my - butHeight / 2 + ay;

      if (ay < w) {
        W = dir;
        won = enabled;
        w = ay;
      }
      if (ay > s) {
        S = dir;
        son = enabled;
        s = ay;
      }
      if (ax < a) {
        A = dir;
        aon = enabled;
        a = ax;
      }
      if (ax > d) {
        D = dir;
        don = enabled;
        d = ax;
      }

      return new Point(x, y);
    } else {

      int x = mx - butWidth / 2 - dir.getFrontOffsetY() * (5 + butWidth * 2);
      int y = my - butHeight / 2 - (dir.getFrontOffsetY() * butHeight * 2);

      if (dir == EnumFacing.DOWN) {
        con = enabled;
      } else {
        jon = enabled;
      }

      return new Point(x, y);
    }
  }

}
