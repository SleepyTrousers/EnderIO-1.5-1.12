package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.entity.EntityEpicSquid;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEpicSquid extends RenderLiving<EntityEpicSquid> {

  private static final @Nonnull ResourceLocation EPIC_TEXTURES = new ResourceLocation(EnderIOZoo.DOMAIN, "entity/epicsquid.png");

  public RenderEpicSquid(RenderManager renderManagerIn) {
    super(renderManagerIn, new ModelSquid(), 0.7F);
  }

  @Override
  protected @Nonnull ResourceLocation getEntityTexture(@Nonnull EntityEpicSquid entity) {
    return EPIC_TEXTURES;
  }

  @Override
  protected void applyRotations(@Nonnull EntityEpicSquid entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
    float f = entityLiving.prevSquidPitch + (entityLiving.squidPitch - entityLiving.prevSquidPitch) * partialTicks;
    float f1 = entityLiving.prevSquidYaw + (entityLiving.squidYaw - entityLiving.prevSquidYaw) * partialTicks;
    GlStateManager.translate(0.0F, 0.5F, 0.0F);
    GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(0.0F, -1.2F, 0.0F);
  }

  @Override
  protected float handleRotationFloat(@Nonnull EntityEpicSquid livingBase, float partialTicks) {
    return livingBase.lastTentacleAngle + (livingBase.tentacleAngle - livingBase.lastTentacleAngle) * partialTicks;
  }

  @Override
  protected boolean setBrightness(@Nonnull EntityEpicSquid entitylivingbaseIn, float partialTicks, boolean combineTextures) {
    if (super.setBrightness(entitylivingbaseIn, partialTicks, combineTextures)) {
      if (entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0) {
        float amount = (entitylivingbaseIn.hurtTime) / (float) entitylivingbaseIn.maxHurtTime;
        this.brightnessBuffer.position(0);
        this.brightnessBuffer.put(0.0F);
        this.brightnessBuffer.put(0.8f);
        this.brightnessBuffer.put(0.2F);
        this.brightnessBuffer.put(0.6F * amount);
        this.brightnessBuffer.flip();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexEnv(8960, 8705, this.brightnessBuffer);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      }
      return true;
    }
    return false;
  }
}
