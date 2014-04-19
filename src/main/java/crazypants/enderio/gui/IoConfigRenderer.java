package crazypants.enderio.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.teleport.TravelController;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Camera;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;

public class IoConfigRenderer {

  protected static final RenderBlocks RB = new RenderBlocks();

  //private int range;
  private boolean dragging = false;
  private float pitch = -45;
  private float yaw = 45;
  private double distance;
  private long initTime;


  private Minecraft mc = Minecraft.getMinecraft();
  private World world = mc.thePlayer.worldObj;

  private final Vector3d origin = new Vector3d();
  private final Vector3d eye = new Vector3d();
  private final Camera camera = new Camera();
  private final Matrix4d pitchRot = new Matrix4d();
  private final Matrix4d yawRot = new Matrix4d();

  public BlockCoord originBC;
  public IIoConfigurable configuarble;

  public IoConfigRenderer(IIoConfigurable configuarble) {
    this.configuarble = configuarble;
    originBC = configuarble.getLocation();
    origin.set(originBC.x + 0.5, originBC.y+ 0.5, originBC.z+ 0.5);
    pitchRot.setIdentity();
    yawRot.setIdentity();
    distance = 4;

    //    blocks.add(new ViewableBlocks(ioX, ioY, ioZ, EnderIO.blockEnderIo));
    //
    //    for (int x = ioX - range; x <= ioX + range; x++) {
    //      for (int y = ioY - range; y <= ioY + range; y++) {
    //        for (int z = ioZ - range; z <= ioZ + range; z++) {
    //          Block block = world.getBlock(x, y, z);
    //          blocks.add(new ViewableBlocks(x, y, z, block));
    //        }
    //      }
    //    }
  }

  public void init() {
    world = mc.thePlayer.worldObj;
    initTime = world.getTotalWorldTime();
    RB.blockAccess = world;
  }

  public void handleMouseInput() {

    if(Mouse.getEventButton() == 0) {
      dragging = Mouse.getEventButtonState();
    }

    if(dragging) {

      double dx = (Mouse.getEventDX() / (double) mc.displayWidth);
      double dy = (Mouse.getEventDY() / (double) mc.displayHeight);
      if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
        distance -= dy * 15;
      } else {
        yaw -= 4 * dx * 180;
        pitch += 2* dy * 180;
        pitch = (float) VecmathUtil.clamp(pitch, -80, 80);
      }
    }

    distance -= Mouse.getDWheel() * 0.01;
    distance = VecmathUtil.clamp(distance, 0.1, 20);

    long elapsed = world.getTotalWorldTime() - initTime;

    if(Mouse.getEventButton() == 1 && !Mouse.getEventButtonState() && camera.isValid() && elapsed > 10) {
      int x = Mouse.getEventX();
      int y = Mouse.getEventY();
      Vector3d start = new Vector3d();
      Vector3d end = new Vector3d();
      if(camera.getRayForPixel(x, y, start, end)) {
        end.scale(distance * 2);
        end.add(start);
        doSelection(start, end);
      }

    }
  }

  private void doSelection(Vector3d start, Vector3d end) {
    start.add(origin);
    end.add(origin);
    List<MovingObjectPosition> hits = new ArrayList<MovingObjectPosition>();

    Block block = world.getBlock(originBC.x, originBC.y, originBC.z);
    if(block != null) {
      MovingObjectPosition hit = block.collisionRayTrace(world, originBC.x, originBC.y, originBC.z, Vec3.createVectorHelper(start.x, start.y, start.z),
          Vec3.createVectorHelper(end.x, end.y, end.z));
      if(hit != null) {
        block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
        configuarble.toggleIoModeForFace(ForgeDirection.getOrientation(hit.sideHit));
      }
    }
  }

  public static MovingObjectPosition getClosestHit(Vec3 origin, Collection<MovingObjectPosition> candidates) {
    double minLengthSquared = Double.POSITIVE_INFINITY;
    MovingObjectPosition closest = null;

    for (MovingObjectPosition hit : candidates) {
      if(hit != null) {
        double lengthSquared = hit.hitVec.squareDistanceTo(origin);
        if(lengthSquared < minLengthSquared) {
          minLengthSquared = lengthSquared;
          closest = hit;
        }
      }
    }
    return closest;
  }

  public void drawScreen(int par1, int par2, float partialTick, Rectangle vp) {
    //TODO: Need to depth sort transparent passes
    if(!updateCamera(partialTick,vp.x,vp.y,vp.width,vp.height)) {
      return;
    }
    applyCamera(partialTick);

    TravelController.instance.setSelectionEnabled(false);

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    RenderHelper.disableStandardItemLighting();
    mc.entityRenderer.enableLightmap(0);
    RenderUtil.bindBlockTexture();

    Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);
    for (int pass = 0; pass < 2; pass++) {

      ForgeHooksClient.setRenderPass(pass);
      setGlStateForPass(pass);

      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.setTranslation(trans.x, trans.y, trans.z);

      Block block = world.getBlock(originBC.x, originBC.y, originBC.z);
      if(block != null) {
        if(block.canRenderInPass(pass)) {
          BlockCoord bc = originBC;
          RB.setRenderBounds(0, 0, 0, 1, 1, 1);
          RB.renderBlockByRenderType(block, bc.x, bc.y, bc.z);
        }
      }

      //      for (ViewableBlocks ug : blocks) {
      //        if(ug.block.canRenderInPass(pass)) {
      //          RB.setRenderBounds(0, 0, 0, 1, 1, 1);
      //          RB.renderBlockByRenderType(ug.block, ug.bc.x, ug.bc.y, ug.bc.z);
      //        }
      //      }
      Tessellator.instance.draw();
      Tessellator.instance.setTranslation(0, 0, 0);
    }

    RenderHelper.enableStandardItemLighting();

    TileEntityRendererDispatcher.instance.field_147558_l = origin.x - eye.x;
    TileEntityRendererDispatcher.instance.field_147560_j = origin.y - eye.y;
    TileEntityRendererDispatcher.instance.field_147561_k = origin.z - eye.z;

    TileEntityRendererDispatcher.staticPlayerX = origin.x - eye.x;
    TileEntityRendererDispatcher.staticPlayerY = origin.y - eye.y;
    TileEntityRendererDispatcher.staticPlayerZ = origin.z - eye.z;

    for (int pass = 0; pass < 2; pass++) {

      ForgeHooksClient.setRenderPass(pass);
      setGlStateForPass(pass);

      //      for (ViewableBlocks ug : blocks) {
      //        TileEntity tile = world.getTileEntity(ug.bc.x, ug.bc.y, ug.bc.z);
      //        if(tile != null) {
      //          Vector3d at = new Vector3d(eye.x - 0.5, eye.y - 0.5, eye.z - 0.5);
      //          at.x += ug.bc.x - ioX;
      //          at.y += ug.bc.y - ioY;
      //          at.z += ug.bc.z - ioZ;
      //          TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, at.x, at.y, at.z, 0);
      //        }
      //      }
    }
    ForgeHooksClient.setRenderPass(-1);
    setGlStateForPass(0);
    TravelController.instance.setSelectionEnabled(true);

  }

  private void setGlStateForPass(int pass) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    if(pass == 0) {
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glDepthMask(true);
    } else {
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDepthMask(false);
    }

  }


  private boolean updateCamera(float partialTick, int vpx, int vpy, int vpw, int vph) {
    if(vpw <= 0 || vph <= 0) {
      return false;
    }
    camera.setViewport(vpx, vpy, vpw, vph);
    camera.setProjectionMatrixAsPerspective(30, 0.05, 50, vpw, vph);
    eye.set(0, 0, distance);
    pitchRot.makeRotationX(Math.toRadians(pitch));
    yawRot.makeRotationY(Math.toRadians(yaw));
    pitchRot.transform(eye);
    yawRot.transform(eye);
    camera.setViewMatrixAsLookAt(eye, RenderUtil.ZERO_V, RenderUtil.UP_V);
    return camera.isValid();
  }

  private void applyCamera(float partialTick) {
    Rectangle vp = camera.getViewport();
    GL11.glViewport(vp.x, vp.y, vp.width, vp.height);
    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    RenderUtil.loadMatrix(camera.getTransposeProjectionMatrix());
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    RenderUtil.loadMatrix(camera.getTransposeViewMatrix());
    GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);

  }


}
