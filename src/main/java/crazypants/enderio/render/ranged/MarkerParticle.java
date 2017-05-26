package crazypants.enderio.render.ranged;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MarkerParticle extends Particle {

  private final @Nonnull Vector4f color;
  private final @Nonnull BlockPos pos;
  private int tolive, maxage;

  public MarkerParticle(@Nonnull World world, @Nonnull BlockPos pos) {
    this(world, pos, new Vector4f(1, 1, 1, 0.4f));
  }

  public MarkerParticle(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vector4f color) {
    this(world, pos, color, 34);
  }

  public MarkerParticle(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vector4f color, int maxage) {
    super(world, pos.getX(), pos.getY(), pos.getZ());
    this.pos = pos;
    this.color = color;
    this.tolive = this.maxage = maxage;
  }

  @Override
  public void onUpdate() {
    tolive--;
  }

  @Override
  public boolean isAlive() {
    return tolive > 0;
  }

  @Override
  public int getFXLayer() {
    return 3;
  }

  @Override
  public void renderParticle(@Nonnull VertexBuffer worldRendererIn, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ,
      float rotationYZ, float rotationXY, float rotationXZ) {

    if (!isAlive()) {
      return;
    }

    GlStateManager.pushMatrix();
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    RenderUtil.bindBlockTexture();
    GlStateManager.depthMask(false);

    GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

    float fade = tolive / (float) maxage;

    GlStateManager.color(color.x, color.y, color.z, color.w * fade);

    RenderUtil.renderBoundingBox(new BoundingBox(pos).expand(0.01, 0.01, 0.01), IconUtil.instance.whiteTexture);

    GlStateManager.depthMask(true);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

}
