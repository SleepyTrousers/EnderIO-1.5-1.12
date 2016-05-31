package crazypants.enderio.machine.ranged;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class RangeParticle extends Particle {

  private static final int INIT_TIME = 20;

  private final IRanged owner;
  private final Vector4f color;
  private int age = 0;

  public RangeParticle(IRanged owner) {
    this(owner, new Vector4f(1, 1, 1, 0.4f));
  }

  public RangeParticle(IRanged owner, Vector4f color) {
    super(owner.getRangeWorldObj(), owner.getLocation().x, owner.getLocation().y, owner.getLocation().z);
    this.owner = owner;
    this.color = color;
  }

  @Override
  public void onUpdate() {
    age++;
  }

  @Override
  public boolean isAlive() {
    if (!((TileEntity) owner).hasWorldObj() || ((TileEntity) owner).isInvalid() || !owner.isShowingRange()) {
      return false;
    }
    BlockCoord bc = owner.getLocation();
    if (!(worldObj.getTileEntity(bc.getBlockPos()) instanceof IRanged)) {
      return false;
    }
    return true;
  }

  @Override
  public int getFXLayer() {
    return 3;
  }

  @Override
  public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
      float rotationXY, float rotationXZ) {

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

    GlStateManager.translate(owner.getLocation().x - interpPosX, owner.getLocation().y - interpPosY, owner.getLocation().z - interpPosZ);

    GlStateManager.color(color.x, color.y, color.z, color.w);

    GlStateManager.translate(0.5f, 0.5f, 0.5f);
    GlStateManager.scale(scale, scale, scale);
    GlStateManager.translate(-0.5f, -0.5f, -0.5f);

    RenderUtil.renderBoundingBox(owner.getRangeBox(), IconUtil.instance.whiteTexture);

    GlStateManager.depthMask(true);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
  }

}
