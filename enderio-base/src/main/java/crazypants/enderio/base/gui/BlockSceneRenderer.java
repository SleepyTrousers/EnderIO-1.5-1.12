package crazypants.enderio.base.gui;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.vecmath.Camera;
import com.enderio.core.common.vecmath.Matrix4d;
import com.enderio.core.common.vecmath.Vector3d;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class BlockSceneRenderer {

  private final float pitch;
  private final double distance;

  private @Nonnull Minecraft mc = Minecraft.getMinecraft();

  private final @Nonnull Vector3d origin = new Vector3d();
  private final @Nonnull Vector3d eye = new Vector3d();
  private final @Nonnull Camera camera = new Camera();
  private final @Nonnull Matrix4d pitchRot = new Matrix4d();
  private final @Nonnull Matrix4d yawRot = new Matrix4d();

  private final @Nonnull NNList<Pair<BlockPos, IBlockState>> blocks = new NNList<>();

  public BlockSceneRenderer(@Nonnull final NNList<Pair<BlockPos, IBlockState>> blocks) {
    this.blocks.addAll(blocks);

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (Pair<BlockPos, IBlockState> pair : blocks) {
      BlockPos bc = pair.getKey();
      min.set(Math.min(bc.getX(), min.x), Math.min(bc.getY(), min.y), Math.min(bc.getZ(), min.z));
      max.set(Math.max(bc.getX(), max.x), Math.max(bc.getY(), max.y), Math.max(bc.getZ(), max.z));
    }
    Vector3d size = new Vector3d(max);
    size.sub(min);
    size.scale(0.5);
    Vector3d c = new Vector3d(min.x + size.x + .5, min.y + size.y + .5, min.z + size.z + .5);
    size.scale(2);

    origin.set(c);
    pitchRot.setIdentity();
    yawRot.setIdentity();

    pitch = -22.5f; // looks nice for fire on bedrock, may need a parameter for other renderings

    distance = Math.max(Math.max(size.x, size.y), size.z) + 4;
  }

  public void drawScreen(int x, int y, int w, int h) {
    ScaledResolution scaledresolution = new ScaledResolution(mc);

    int vpx = x * scaledresolution.getScaleFactor();
    int vpy = (scaledresolution.getScaledHeight() - h - y) * scaledresolution.getScaleFactor();
    int vpw = w * scaledresolution.getScaleFactor();
    int vph = h * scaledresolution.getScaleFactor();

    if (updateCamera(vpx, vpy, vpw, vph)) {
      applyCamera();
      renderScene();
      resetCamera();
    }
  }

  private void renderScene() {
    GlStateManager.enableCull();
    GlStateManager.enableRescaleNormal();

    RenderHelper.disableStandardItemLighting();
    mc.entityRenderer.disableLightmap();
    RenderUtil.bindBlockTexture();

    GlStateManager.disableLighting();
    GlStateManager.enableTexture2D();
    GlStateManager.enableAlpha();

    final LayerRenderer layerRenderer = new LayerRenderer(new Vector3d((-origin.x) + eye.x, (-origin.y) + eye.y, (-origin.z) + eye.z));

    BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
    try {
      NNList.of(BlockRenderLayer.class).apply(layerRenderer);
    } finally {
      ForgeHooksClient.setRenderLayer(oldRenderLayer);
      GlStateManager.depthMask(true);
    }

  }

  private final class LayerRenderer implements Callback<BlockRenderLayer> {

    private final @Nonnull Vector3d translation;

    private LayerRenderer(@Nonnull Vector3d translation) {
      this.translation = translation;
    }

    @Override
    public void apply(@Nonnull BlockRenderLayer layer) {
      ForgeHooksClient.setRenderLayer(layer);
      setGlStateForPass(layer);
      BufferBuilder wr = Tessellator.getInstance().getBuffer();
      wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
      wr.setTranslation(translation.x, translation.y, translation.z);
      blocks.apply(new BlockRenderer(layer));
      Tessellator.getInstance().draw();
      wr.setTranslation(0, 0, 0);
    }
  }

  private static final class BlockRenderer implements Callback<Pair<BlockPos, IBlockState>> {
    private final @Nonnull BlockRenderLayer layer;

    private BlockRenderer(@Nonnull BlockRenderLayer layer) {
      this.layer = layer;
    }

    @Override
    public void apply(@Nonnull Pair<BlockPos, IBlockState> entry) {
      BlockPos pos = entry.getKey();
      IBlockState bs = entry.getValue();
      if (bs.getBlock().canRenderInLayer(bs, layer) && pos != null) {
        BufferBuilder worldRendererIn = Tessellator.getInstance().getBuffer();
        try {
          BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
          EnumBlockRenderType type = bs.getRenderType();
          if (type != EnumBlockRenderType.MODEL) {
            blockrendererdispatcher.renderBlock(bs, pos, Minecraft.getMinecraft().world, worldRendererIn);
          } else {
            IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(bs);
            blockrendererdispatcher.getBlockModelRenderer().renderModel(Minecraft.getMinecraft().world, ibakedmodel, bs, pos, worldRendererIn, false);
          }
        } catch (Throwable throwable) {
          // Just bury a render issue here, it is only a GUI screen
        }
      }
    }
  }

  private static void setGlStateForPass(@Nonnull BlockRenderLayer layer) {
    GlStateManager.color(1, 1, 1);
    if (layer != BlockRenderLayer.TRANSLUCENT) {
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
    } else {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.depthMask(false);
    }
  }

  private boolean updateCamera(int vpx, int vpy, int vpw, int vph) {
    if (vpw <= 0 || vph <= 0) {
      return false;
    }
    camera.setViewport(vpx, vpy, vpw, vph);
    camera.setProjectionMatrixAsPerspective(30, 0.05, 50, vpw, vph);
    eye.set(0, 0, distance);
    pitchRot.makeRotationX(Math.toRadians(pitch));
    yawRot.makeRotationY(Math.toRadians(Minecraft.getSystemTime() / 16));
    pitchRot.transform(eye);
    yawRot.transform(eye);
    camera.setViewMatrixAsLookAt(eye, RenderUtil.ZERO_V, RenderUtil.UP_V);
    return camera.isValid();
  }

  private void applyCamera() {
    Rectangle vp = camera.getViewport();
    GL11.glViewport(vp.x, vp.y, vp.width, vp.height);
    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    final Matrix4d camaraViewMatrix = camera.getTransposeProjectionMatrix();
    if (camaraViewMatrix != null) {
      RenderUtil.loadMatrix(camaraViewMatrix);
    }
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    final Matrix4d cameraViewMatrix = camera.getTransposeViewMatrix();
    if (cameraViewMatrix != null) {
      RenderUtil.loadMatrix(cameraViewMatrix);
    }
    GL11.glTranslatef(-(float) eye.x, -(float) eye.y, -(float) eye.z);
  }

  protected void resetCamera() {
    ScaledResolution scaledresolution = new ScaledResolution(mc);
    GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
  }

}
