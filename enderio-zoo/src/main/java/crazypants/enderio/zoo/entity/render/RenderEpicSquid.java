package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.entity.EntityEpicSquid;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEpicSquid extends RenderLiving<EntityEpicSquid> {

  public static final Factory FACTORY = new Factory();

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

  public static class Factory implements IRenderFactory<EntityEpicSquid> {

    @Override
    public Render<? super EntityEpicSquid> createRenderFor(RenderManager manager) {
      return new RenderEpicSquid(manager);
    }
  }

}
