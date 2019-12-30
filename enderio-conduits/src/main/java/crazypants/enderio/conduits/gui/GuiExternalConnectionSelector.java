package crazypants.enderio.conduits.gui;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.PacketOpenConduitUI;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GuiExternalConnectionSelector extends GuiScreen {

  private static int BUTTON_HEIGHT = 20;
  private static int BUTTON_WIDTH = 60;

  Set<EnumFacing> cons;
  IConduitBundle cb;
  EnumMap<EnumFacing, Point> textPositions = new EnumMap<EnumFacing, Point>(EnumFacing.class);
  EnumMap<EnumFacing, ItemStack> stacks = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  EnumMap<EnumFacing, Point> stackPositions = new EnumMap<EnumFacing, Point>(EnumFacing.class);
  EnumMap<EnumFacing, BlockPos> neighborPositions = new EnumMap<>(EnumFacing.class);

  private long keyLock = Minecraft.getSystemTime() + 2000;

  public GuiExternalConnectionSelector(IConduitBundle cb) {
    this.cb = cb;
    cons = new HashSet<EnumFacing>();
    for (IClientConduit conduit : cb.getClientConduits()) {
      if (ConduitRegistry.getNetwork(conduit).canConnectToAnything()) {
        Set<EnumFacing> conCons = conduit.getConduitConnections();
        for (EnumFacing dir : EnumFacing.VALUES) {
          if (!conCons.contains(dir)) {
            cons.add(dir);
          }
        }

      } else {
        cons.addAll(conduit.getExternalConnections());
      }
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (keyCode == 1 || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
      mc.player.closeScreen();
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
    } else if (con && keyCode == mc.gameSettings.keyBindSneak.getKeyCode() && keyLock < Minecraft.getSystemTime()) {
      // the player needs to hold this key to open the GUI, so don't close it on them instantly...
      go(EnumFacing.DOWN);
    }
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) {
    EnumFacing dir = EnumFacing.values()[b.id];
    go(dir);
  }

  private void go(EnumFacing dir) {
    if (dir != null) {
      if (neighborPositions.containsKey(dir)) {
        final BlockPos goPos = neighborPositions.get(dir);

        double d0 = goPos.getX() + .5 - mc.player.posX;
        double d2 = goPos.getZ() + .5 - mc.player.posZ;
        double d1 = goPos.getY() + .5 - (mc.player.posY + mc.player.getEyeHeight());

        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        mc.player.rotationPitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI))));
        mc.player.rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F);

        ConduitRegistry.getConduitModObjectNN().openClientGui(mc.world, goPos, mc.player, null, 0);
      } else {
        PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(cb.getEntity(), dir));
      }
    }
  }

  protected void findBlockDataForDirection(@Nonnull EnumFacing direction) {
    World world = cb.getBundleworld();
    BlockPos blockPos = cb.getLocation().offset(direction);
    if (!world.isAirBlock(blockPos)) {
      IBlockState bs = world.getBlockState(blockPos);
      Block b = bs.getBlock();
      if (b != ConduitRegistry.getConduitModObjectNN().getBlock()) {
        try {
          ItemStack pickBlock = b.getPickBlock(bs, new RayTraceResult(new Vec3d(0, 0, 0), direction.getOpposite(), blockPos), world, blockPos,
              Minecraft.getMinecraft().player);
          if (Prep.isValid(pickBlock)) {
            stacks.put(direction, pickBlock);
            return;
          }
        } catch (Throwable t) {
        }
        // fallback:
        try {
          Item item = b.getItemDropped(bs.getActualState(world, blockPos), world.rand, 0);
          if (NullHelper.untrust(item) != null) {
            stacks.put(direction, new ItemStack(item, 1, b.damageDropped(bs)));
          }
        } catch (Throwable t) {
        }
      } else {
        neighborPositions.put(direction, blockPos);
        stacks.put(direction, new ItemStack(ModObject.itemConduitFacade.getItemNN())); // fallback
        TileEntity te = world.getTileEntity(blockPos);
        if (te instanceof IConduitBundle) {
          IConduitBundle conduit = (IConduitBundle) te;
          Iterator<IClientConduit> iterator = conduit.getClientConduits().iterator();
          while (iterator.hasNext()) {
            IClientConduit next = iterator.next();
            if (next != null) {
              stacks.put(direction, next.createItem());
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public void initGui() {
    GuiButton b;
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (dir != null) {
        findBlockDataForDirection(dir);
        Point p = getOffsetForDir(dir, cons.contains(dir) || neighborPositions.containsKey(dir));
        if (neighborPositions.containsKey(dir)) {
          textPositions.put(dir, new Point(-3000, -3000));
          int offset = (BUTTON_WIDTH - BUTTON_HEIGHT) / 2;
          stackPositions.put(dir, new Point(p.x + 2 + offset, p.y + 2));
          b = new GuiButton(dir.ordinal(), p.x + offset, p.y, BUTTON_HEIGHT, BUTTON_HEIGHT, "");
          buttonList.add(b);
        } else {
          textPositions.put(dir, new Point(p.x, p.y + BUTTON_HEIGHT + 1));
          stackPositions.put(dir, new Point(p.x + 2, p.y + 2));
          b = new GuiButton(dir.ordinal(), p.x, p.y, BUTTON_WIDTH, BUTTON_HEIGHT, (stacks.containsKey(dir) ? "  " : "") + dir.toString());
          buttonList.add(b);
          if (!cons.contains(dir)) {
            b.enabled = false;
            if (dir.getFrontOffsetY() != 0 && !stacks.containsKey(dir)) {
              b.visible = false;
            }
          }
        }
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

    int scale = fontRenderer.getUnicodeFlag() ? 1 : 2;

    for (EnumFacing dir : EnumFacing.VALUES) {
      if (stacks.containsKey(dir)) {
        ItemStack stack = stacks.get(dir);
        String blockName = stack.getDisplayName();
        int textWidth = fontRenderer.getStringWidth(blockName) / scale;
        Point p = textPositions.get(dir);

        GL11.glPushMatrix();
        GL11.glScalef(1f / scale, 1f / scale, 1f / scale);
        drawString(fontRenderer, blockName, scale * (p.x + BUTTON_WIDTH / 2 - textWidth / 2), scale * p.y, ColorUtil.getARGB(Color.gray));
        GL11.glPopMatrix();
      }
    }

    super.drawScreen(par1, par2, par3);

    String txt = "Select Connection to Adjust";
    int x = width / 2 - (fontRenderer.getStringWidth(txt) / 2);
    int y = height / 2 - BUTTON_HEIGHT * 3 - 5;

    drawString(fontRenderer, txt, x, y, ColorUtil.getARGB(Color.white));

    if (Minecraft.getMinecraft().player.getName().contains("direwolf20") && ((EnderIO.proxy.getTickCount() / 16) & 1) == 1) {
      txt = "You can also right-click the connector directly";
      x = width / 2 - (fontRenderer.getStringWidth(txt) / 2);
      y = (int) (height / 2 + BUTTON_HEIGHT * 2.75 - 5);
      drawString(fontRenderer, txt, x, y, ColorUtil.getARGB(Color.white));
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
        if (stack != null) {
          itemRender.renderItemAndEffectIntoGUI(stack, p.x, p.y);
        }
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

      float playerAngle = Minecraft.getMinecraft().player.rotationYaw * deg2rad;
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

      int x = mx - BUTTON_WIDTH / 2;// - dir.getFrontOffsetY() * (5 + BUTTON_WIDTH * 2);
      int y = (int) (my - BUTTON_HEIGHT / 2 - ((dir.getFrontOffsetY() * 4 + .5) * BUTTON_HEIGHT));

      if (dir == EnumFacing.DOWN) {
        con = enabled;
      } else {
        jon = enabled;
      }

      return new Point(x, y);
    }
  }

}
