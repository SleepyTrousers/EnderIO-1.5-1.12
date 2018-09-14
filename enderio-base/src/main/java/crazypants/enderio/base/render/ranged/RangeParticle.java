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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class RangeParticle<T extends TileEntity & IRanged> extends Particle {

  private static final int INIT_TIME = 20;
  private static final int AGE_LIMIT = 20 * 60 * 10; // 10 minutes

  private final T owner;
  private final Vector4f color;
  private int age = 0;

  public RangeParticle(T owner) {
    this(owner, new Vector4f(1, 1, 1, 0.4f));
  }

  public RangeParticle(T owner, Vector4f color) {
    super(owner.getWorld(), owner.getPos().getX(), owner.getPos().getY(), owner.getPos().getZ());
    this.owner = owner;
    this.color = color;
  }

  @Override
  public void onUpdate() {
    age++;
  }

  @Override
  public boolean isAlive() {
    return age < AGE_LIMIT && ((TileEntity) owner).hasWorld() && !((TileEntity) owner).isInvalid() && owner.isShowingRange()
        && world.getTileEntity(owner.getPos()) == owner;
  }

  @Override
  public int getFXLayer() {
    return 3;
  }

  @Override
  public void renderParticle(@Nonnull BufferBuilder worldRendererIn, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ,
      float rotationYZ, float rotationXY, float rotationXZ) {

    GlStateManager.pushMatrix();
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    RenderUtil.bindBlockTexture();
    GlStateManager.depthMask(false);

    float scale = Math.min((age + partialTicks) / INIT_TIME, 1);

    // Vanilla bug? Particle.interpPosX/Y/Z variables are always one frame behind
    double x = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
    double y = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
    double z = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
    GlStateManager.translate(-x, -y, -z);

    GlStateManager.color(color.x, color.y, color.z, color.w);

    RenderUtil.renderBoundingBox(scale(owner.getPos(), owner.getBounds(), scale).expand(0.01, 0.01, 0.01), IconUtil.instance.whiteTexture);

    GlStateManager.depthMask(true);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

  private @Nonnull BoundingBox scale(final @Nonnull BlockPos source, final @Nonnull BoundingBox bounds, float scale) {
    final double sourceX = source.getX() + .5, sourceY = source.getY() + .5, sourceZ = source.getZ() + .5;
    return new BoundingBox( //
        scale(sourceX, bounds.minX, scale), //
        scale(sourceY, bounds.minY, scale), //
        scale(sourceZ, bounds.minZ, scale), //
        scale(sourceX, bounds.maxX, scale), //
        scale(sourceY, bounds.maxY, scale), //
        scale(sourceZ, bounds.maxZ, scale));
  }

  private double scale(double x0, double x1, double scale) {
    return x0 * (1 - scale) + x1 * scale;
  }

}
