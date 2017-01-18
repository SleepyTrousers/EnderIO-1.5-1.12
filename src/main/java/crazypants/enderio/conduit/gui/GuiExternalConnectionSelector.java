package crazypants.enderio.conduit.gui;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.packet.PacketOpenConduitUI;
import crazypants.enderio.conduit.registry.ConduitRegistry;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static crazypants.enderio.ModObject.blockConduitBundle;

public class GuiExternalConnectionSelector extends GuiScreen {

  private static int BUTTON_HEIGHT = 20;
  private static int BUTTON_WIDTH = 60;

  Set<EnumFacing> cons;
  IConduitBundle cb;
  EnumMap<EnumFacing, Point> textPositions = new EnumMap<EnumFacing, Point>(EnumFacing.class);
  EnumMap<EnumFacing, ItemStack> stacks = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  EnumMap<EnumFacing, Point> stackPositions = new EnumMap<EnumFacing, Point>(EnumFacing.class);

  public GuiExternalConnectionSelector(IConduitBundle cb) {
    this.cb = cb;
    cons = new HashSet<EnumFacing>();
    for (IConduit con : cb.getConduits()) {
      if (ConduitRegistry.get(con).canConnectToAnything()) {
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
    PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(cb.getEntity(), dir));
  }

  protected void findBlockDataForDirection(EnumFacing direction) {
    World world = cb.getBundleWorldObj();
    BlockPos blockPos = cb.getLocation().getLocation(direction).getBlockPos();
    if (!world.isAirBlock(blockPos)) {
      IBlockState bs = world.getBlockState(blockPos);
      Block b = bs.getBlock();
      if (b != null && b != blockConduitBundle.getBlock()) {
        try {// TODO: This seems wrong. pickBlock?
          Item item = b.getItemDropped(bs.getActualState(world, blockPos), world.rand, 0);
          if (item != null) {
            stacks.put(direction, new ItemStack(item, 1, b.damageDropped(bs)));
          }
        } catch (Throwable t) {
        }
      }
    }
  }

  @Override
  public void initGui() {
    GuiButton b;
    for (EnumFacing dir : EnumFacing.VALUES) {
      findBlockDataForDirection(dir);
      Point p = getOffsetForDir(dir, cons.contains(dir));
      textPositions.put(dir, new Point(p.x, p.y + BUTTON_HEIGHT + 1));
      stackPositions.put(dir, new Point(p.x + 2, p.y + 2));
      b = new GuiButton(dir.ordinal(), p.x, p.y, BUTTON_WIDTH, BUTTON_HEIGHT, (stacks.containsKey(dir) ? "  " : "") + dir.toString());
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

    int scale = fontRendererObj.getUnicodeFlag() ? 1 : 2;

    for (EnumFacing dir : EnumFacing.VALUES) {
      if (stacks.containsKey(dir)) {
        ItemStack stack = stacks.get(dir);
        String blockName = null;
        if (stack.hasDisplayName()) {
          blockName = stack.getDisplayName();
        } else {
          blockName = EnderIO.lang.localizeExact(stack.getUnlocalizedName() + ".name");
        }
        if (blockName == null) {
          continue;
        }
        int textWidth = fontRendererObj.getStringWidth(blockName) / scale;
        Point p = textPositions.get(dir);

        GL11.glPushMatrix();
        GL11.glScalef(1f / scale, 1f / scale, 1f / scale);
        drawString(fontRendererObj, blockName, scale * (p.x + BUTTON_WIDTH / 2 - textWidth / 2), scale * p.y, ColorUtil.getARGB(Color.gray));
        GL11.glPopMatrix();
      }
    }

    super.drawScreen(par1, par2, par3);

    String txt = "Select Connection to Adjust";
    int x = width / 2 - (fontRendererObj.getStringWidth(txt) / 2);
    int y = height / 2 - BUTTON_HEIGHT * 3 - 5;

    drawString(fontRendererObj, txt, x, y, ColorUtil.getARGB(Color.white));

    if (Minecraft.getMinecraft().thePlayer.getName().contains("direwolf20") && ((EnderIO.proxy.getTickCount() / 16) & 1) == 1) {
      txt = "You can also right-click the connector directly";
      x = width / 2 - (fontRendererObj.getStringWidth(txt) / 2);
      y = height / 2 + BUTTON_HEIGHT * 3 - 5;
      drawString(fontRendererObj, txt, x, y, ColorUtil.getARGB(Color.white));
    }

    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    GlStateManager.enableLighting();

    for (EnumFacing dir : EnumFacing.VALUES) {
      if (stacks.containsKey(dir)) {
        ItemStack stack = stacks.get(dir);
        Point p = stackPositions.get(dir);
        itemRender.renderItemAndEffectIntoGUI(stack, p.x, p.y);
      }
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
    if (dir.getFrontOffsetY() == 0) {

      float playerAngle = Minecraft.getMinecraft().thePlayer.rotationYaw * deg2rad;
      float dirAngle = dir.getHorizontalIndex() * headg2rad;
      float buttonAngle = dirAngle - playerAngle - 90 * deg2rad;

      int ax = (int) (MathHelper.cos((buttonAngle)) * BUTTON_WIDTH);
      int ay = (int) (MathHelper.sin((buttonAngle)) * BUTTON_HEIGHT * 2);

      int x = mx - BUTTON_WIDTH / 2 + ax;
      int y = my - BUTTON_HEIGHT / 2 + ay;

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

      int x = mx - BUTTON_WIDTH / 2 - dir.getFrontOffsetY() * (5 + BUTTON_WIDTH * 2);
      int y = my - BUTTON_HEIGHT / 2 - (dir.getFrontOffsetY() * BUTTON_HEIGHT * 2);

      if (dir == EnumFacing.DOWN) {
        con = enabled;
      } else {
        jon = enabled;
      }

      return new Point(x, y);
    }
  }

}
