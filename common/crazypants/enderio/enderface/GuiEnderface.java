package crazypants.enderio.enderface;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.enderface.te.MeProxy;
import crazypants.enderio.teleport.TravelController;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Camera;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;

public class GuiEnderface extends GuiScreen {

  protected static final RenderBlocks RB = new RenderBlocks();

  private float pitch = -45;
  private float yaw = 45;

  private final EntityPlayer player;
  private final World world;
  private final int ioX;
  private final int ioY;
  private final int ioZ;
  private boolean chunkLoaded;

  private List<ViewableBlocks> blocks = new ArrayList<ViewableBlocks>();

  private int gw;
  private int gh;
  private int guiLeft;
  private int guiTop;
  private int finalGw;
  private int finalGh;
  // private boolean animateInX = true;
  private boolean animateInX = false;
  private boolean animateInY = false;
  // private boolean animating = true;
  float animationDuration = 180;

  private final Vector3d origin = new Vector3d();
  private final Vector3d eye = new Vector3d();
  private final Camera camera = new Camera();
  private final Matrix4d pitchRot = new Matrix4d();
  private final Matrix4d yawRot = new Matrix4d();

  private float scaleAnimX;

  private int range;

  boolean dragging = false;

  private double distance;

  private long initTime;

  public GuiEnderface(EntityPlayer player, World world, int ioX, int ioY, int ioZ) {
    this.player = player;
    this.world = world;
    this.ioX = ioX;
    this.ioY = ioY;
    this.ioZ = ioZ;

    range = Config.enderIoRange;
    distance = 10 + (range * 2);

    TileEntity te = world.getBlockTileEntity(ioX, ioY, ioZ);
    if(te instanceof TileEnderIO) {
      pitch = ((TileEnderIO) te).lastUiPitch;
      yaw = ((TileEnderIO) te).lastUiYaw;
      distance = ((TileEnderIO) te).lastUiDistance;
    }

    origin.set(ioX + 0.5, ioY + 0.5, ioZ + 0.5);
    pitchRot.setIdentity();
    yawRot.setIdentity();

    Chunk c = world.getChunkFromBlockCoords(ioX, ioZ);
    chunkLoaded = c != null && c.isChunkLoaded;
    RB.blockAccess = world;

    blocks.add(new ViewableBlocks(ioX, ioY, ioZ, ModObject.blockEnderIo.id));

    for (int x = ioX - range; x <= ioX + range; x++) {
      for (int y = ioY - range; y <= ioY + range; y++) {
        for (int z = ioZ - range; z <= ioZ + range; z++) {
          int blockId = world.getBlockId(x, y, z);
          if(blockId > 0 && blockId < Block.blocksList.length) {
            blocks.add(new ViewableBlocks(x, y, z, blockId));
          }
        }
      }
    }
  }

  @Override
  public void onGuiClosed() {
    TileEntity te = world.getBlockTileEntity(ioX, ioY, ioZ);
    if(te instanceof TileEnderIO) {
      ((TileEnderIO) te).lastUiPitch = pitch;
      ((TileEnderIO) te).lastUiYaw = yaw;
      ((TileEnderIO) te).lastUiDistance = distance;
    }
  }

  /**
   * Adds the buttons (and other controls) to the screen in question.
   */
  @Override
  public void initGui() {

    finalGw = width * 1;
    finalGh = height * 1;
    // gw = finalGw - 1;
    // gh = finalGh - 1;
    gw = finalGw;
    gh = finalGh;
    // gw = 0;
    // gh = 0;
    guiLeft = (width - gw) / 2;
    guiTop = (height - gh) / 2;

    initTime = world.getTotalWorldTime();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  /**
   * Handles mouse input.
   */
  @Override
  public void handleMouseInput() {
    super.handleMouseInput();

    if(Mouse.getEventButton() == 0) {
      dragging = Mouse.getEventButtonState();
    }

    if(dragging) {

      double dx = (Mouse.getEventDX() / (double) mc.displayWidth);
      double dy = (Mouse.getEventDY() / (double) mc.displayHeight);
      if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
        distance -= dy * 15;
      } else {
        yaw -= dx * 180;
        pitch += dy * 180;
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
    for (ViewableBlocks ug : blocks) {
      if(!ug.bc.equals(new BlockCoord(ioX, ioY, ioZ))) {
        MovingObjectPosition res = Block.blocksList[ug.blockId].collisionRayTrace(world, ug.bc.x, ug.bc.y, ug.bc.z,
            Vec3.createVectorHelper(start.x, start.y, start.z), Vec3.createVectorHelper(end.x, end.y, end.z));
        if(res != null) {
          hits.add(res);
        }
      }
    }
    MovingObjectPosition hit = getClosestHit(Vec3.createVectorHelper(start.x, start.y, start.z), hits);
    if(hit != null) {
      int id = world.getBlockId(hit.blockX, hit.blockY, hit.blockZ);
      if(id == ModObject.blockHyperCube.actualId || id == ModObject.blockCapacitorBank.actualId) {
        Block.blocksList[id].onBlockActivated(world, hit.blockX, hit.blockY, hit.blockZ, player, 0, 0, 0, 0);
      } else {
        openInterface(hit.blockX, hit.blockY, hit.blockZ);
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

  /**
   * Draws the screen and all the components in it.
   */
  @Override
  public void drawScreen(int par1, int par2, float partialTick) {

    animateBackground(partialTick);
    drawDefaultBackground();

    RenderHelper.disableStandardItemLighting();
    RenderHelper.enableGUIStandardItemLighting();
    drawEnderfaceBackground();

    if(!updateCamera(partialTick)) {
      return;
    }
    applyCamera(partialTick);

    if(!animateInX && !animateInY) {

      if(chunkLoaded) {

        TravelController.instance.setSelectionEnabled(false);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.enableLightmap(0);
        RenderUtil.bindBlockTexture();

        Vector3d trans = new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z);
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setTranslation(trans.x, trans.y, trans.z);
        for (ViewableBlocks ug : blocks) {
          RB.setRenderBounds(0, 0, 0, 1, 1, 1);
          RB.renderBlockByRenderType(Block.blocksList[ug.blockId], ug.bc.x, ug.bc.y, ug.bc.z);
        }
        Tessellator.instance.draw();
        Tessellator.instance.setTranslation(0, 0, 0);

        RenderHelper.enableStandardItemLighting();
        TileEntityRenderer.instance.playerX = origin.x - eye.x;
        TileEntityRenderer.instance.playerY = origin.y - eye.y;
        TileEntityRenderer.instance.playerZ = origin.z - eye.z;
        TileEntityRenderer.staticPlayerX = origin.x - eye.x;
        TileEntityRenderer.staticPlayerY = origin.y - eye.y;
        TileEntityRenderer.staticPlayerZ = origin.z - eye.z;
        for (ViewableBlocks ug : blocks) {
          TileEntity tile = world.getBlockTileEntity(ug.bc.x, ug.bc.y, ug.bc.z);
          if(tile != null) {
            TileEntityRenderer.instance.renderTileEntity(tile, partialTick);
          }
        }

        TravelController.instance.setSelectionEnabled(true);

      } else {
        drawCenteredString(fontRenderer, "EnderIO chunk not loaded.", width / 2, height / 2 - 32, 0xFFFFFFFF);
      }
    }
    drawEffectOverlay(partialTick);
  }

  private boolean updateCamera(float partialTick) {
    ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
    int vpx = guiLeft * scaledresolution.getScaleFactor();
    int vpy = guiTop * scaledresolution.getScaleFactor();
    int vpw = (int) ((float) gw / width * mc.displayWidth);
    int vph = (int) ((float) gh / height * mc.displayHeight);
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

    scaleAnimX += (partialTick * 0.25);
    float v = 0.4f;
    float sf = 1.0F * (1 + v / 32);
    GL11.glRotatef((scaleAnimX + partialTick) * 7, 0.0F, 1.0F, 1.0F);
    GL11.glScalef(sf, 1.0F, 1.0f);
    GL11.glRotatef(-(scaleAnimX + partialTick) * 7, 0.0F, 1.0F, 1.0F);

    GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);
  }

  private float portalFade = 1;

  private void drawEffectOverlay(float partialTick) {
    ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_ALPHA_TEST);
    GL11.glDepthMask(false);

    RenderHelper.disableStandardItemLighting();
    RenderHelper.enableGUIStandardItemLighting();

    GL11.glDisable(GL11.GL_LIGHTING);
    mc.entityRenderer.disableLightmap(0);

    portalFade -= (partialTick * (1f / animationDuration));
    portalFade = Math.max(0, portalFade);
    if(portalFade >= 0) {
      drawRect(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0, 0.3f, 0.16f, Math.max(0.3f, portalFade));
    }

    GL11.glEnable(GL11.GL_LIGHTING);
    mc.entityRenderer.enableLightmap(0);

    renderPortalOverlay(0.9f - (0.1f * (1 - portalFade)), scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());

    GL11.glDepthMask(true);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_ALPHA_TEST);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

  }

  /**
   * Renders the portal overlay. Args: portalStrength, width, height
   */
  protected void renderPortalOverlay(float par1, int par2, int par3) {
    if(par1 < 1.0F) {
      par1 *= par1;
      par1 *= par1;
      par1 = par1 * 0.8F + 0.2F;
    }
    GL11.glColor4f(1.0F, 1.0F, 1.0F, par1);
    RenderUtil.bindBlockTexture();
    Icon icon = Block.portal.getBlockTextureFromSide(1);
    float f1 = icon.getMinU();
    float f2 = icon.getMinV();
    float f3 = icon.getMaxU();
    float f4 = icon.getMaxV();
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertexWithUV(0.0D, par3, -90.0D, f1, f4);
    tessellator.addVertexWithUV(par2, par3, -90.0D, f3, f4);
    tessellator.addVertexWithUV(par2, 0.0D, -90.0D, f3, f2);
    tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, f1, f2);
    tessellator.draw();
  }

  private boolean animating() {
    return gw < finalGw || gh < finalGh;
  }

  private void drawRect(int width, int height, float r, float g,
      float b, float a) {
    Tessellator tessellator = Tessellator.instance;
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glColor4f(r, g, b, a);
    tessellator.startDrawingQuads();
    tessellator.addVertex(0.0D, height, 0.0D);
    tessellator.addVertex(width, height, 0.0D);
    tessellator.addVertex(width, 0.0D, 0.0D);
    tessellator.addVertex(0.0D, 0.0D, 0.0D);
    tessellator.draw();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  private void animateBackground(float partialTick) {

    if(!animating()) {
      return;
    }

    // if (animateInX) {
    // animationDuration *= 0.8;
    // }
    int mag = (int) ((width / (animationDuration * 0.5f)) * partialTick);
    int mag2 = mag * 2;

    int ymag = (int) ((height / (animationDuration * 0.5f)) * partialTick);
    // int ymag = mag;
    int ymag2 = ymag * 2;

    if(animateInX) {
      gw -= mag2;
      guiLeft += mag;
      if(gw <= 0) {
        animateInX = false;
        // animateInY = false;
      }
    } else {
      gw += mag2;
      guiLeft -= mag;
    }

    if(animateInY) {
      gh -= ymag2;
      guiTop += ymag;
      if(gh <= 0) {
        animateInY = false;
        // animateInX= false;
      }
    } else {
      gh += ymag2;
      guiTop -= ymag;
    }

    gw = Math.min(gw, finalGw);
    gh = Math.min(gh, finalGh);
    guiLeft = Math.max((width - finalGw) / 2, guiLeft);
    guiTop = Math.max((height - finalGh) / 2, guiTop);

  }

  private void drawEnderfaceBackground() {

    int w = this.gw;
    int h = this.gh;
    int left = this.guiLeft;
    int top = this.guiTop;
    int cx = left + w / 2;
    int cy = top + h / 2;

    // black outline
    drawRect(left, top, left + w, top + h, 0xFF000000);
    left += 1;
    top += 1;
    w -= 2;
    h -= 2;

    // border
    int topH = 0xFFFFFFFF;
    int botH = 0xFF555555;
    int rightH = 0xFF555555;
    int leftH = 0xFFFFFFFF;
    if(animateInX) {
      leftH = 0xFF555555;
      rightH = 0xFFFFFFFF;
    }
    if(animateInY) {
      topH = 0xFF555555;
      botH = 0xFFFFFFFF;
    }

    // drawRect(left, top, left + w, top + 1, topH);
    // drawRect(left, top, left + 1, top + h, leftH);
    // drawRect(left + w - 1, top + 1, left + w, top + h, rightH);
    // drawRect(left + 1, top + h - 1, left + w, top + h, botH);

    left += 1;
    top += 1;
    w -= 2;
    h -= 2;
    // green background
    // drawRect(left, top, left + w, top + h, 0xFF004C2A);
    drawRect(left, top, left + w, top + h, 0xFF00331C);

  }

  void openInterface(int x, int y, int z) {

    if(MeProxy.instance.isMeAccessTerminal(player, x, y, z)) {
      if(Config.enderIoMeAccessEnabled) {
        Packet250CustomPayload pkt = EnderfacePacketProcessor.createMePacket(x, y, z);
        PacketDispatcher.sendPacketToServer(pkt);
      }
    } else {
      Packet250CustomPayload pkt = EnderfacePacketProcessor.createPacketEnderface(x, y, z);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  static class ViewableBlocks {
    BlockCoord bc;
    int blockId;

    private ViewableBlocks(int x, int y, int z, int blockId) {
      super();
      this.bc = new BlockCoord(x, y, z);
      this.blockId = blockId;
    }

  }

}
