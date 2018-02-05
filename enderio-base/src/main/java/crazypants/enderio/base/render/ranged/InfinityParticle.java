package crazypants.enderio.base.render.ranged;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfinityParticle extends Particle {

  private static final int INIT_TIME = 25;
  private static final int FADE_TIME = 5;
  private static final int AGE_LIMIT = 40;

  public InfinityParticle(@Nonnull World world, @Nonnull BlockPos location, @Nonnull Vector4f offset) {
    this(world, location, new Vector4f(0, 0, 0, 0.4f), offset);
  }

  public InfinityParticle(@Nonnull World world, @Nonnull BlockPos location, @Nonnull Vector4f color, @Nonnull Vector4f offset) {
    super(world, location.getX(), location.getY(), location.getZ());
    setRBGColorF(color.x, color.y, color.z);
    setAlphaF(color.w);
    setSize(offset.w, offset.w);
    setPosition(location.getX() + .5f, location.getY() + .5f - height / 2f, location.getZ() + .5f);
    move(offset.x - .5f, offset.y - .5f, offset.z - .5f);
    setMaxAge(AGE_LIMIT);
    particleAge = -rand.nextInt(10);
  }

  @Override
  public int getFXLayer() {
    return 3;
  }

  @Override
  public void renderParticle(@Nonnull BufferBuilder worldRendererIn, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ,
      float rotationYZ, float rotationXY, float rotationXZ) {

    if (particleAge < 0) {
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

    float scale = Math.min((particleAge + partialTicks) / INIT_TIME, 1);
    float fade = particleAge < FADE_TIME ? 1f : ((particleMaxAge - particleAge) / (float) (particleMaxAge - FADE_TIME));

    GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

    GlStateManager.color(getRedColorF(), getGreenColorF(), getBlueColorF(), particleAlpha * fade);

    RenderUtil.renderBoundingBox((new BoundingBox(getBoundingBox())).scale(scale), IconUtil.instance.whiteTexture);

    GlStateManager.depthMask(true);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

}
