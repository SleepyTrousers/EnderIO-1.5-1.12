package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import crazypants.enderio.zoo.entity.EntityWitherCat;
import crazypants.enderio.zoo.entity.EntityWitherCat.GrowthMode;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderWitherCat extends RenderLiving<EntityWitherCat> {

  public static final Factory FACTORY = new Factory();

  private @Nonnull ResourceLocation texture = new ResourceLocation("enderzoo:entity/wither_cat.png");
  private @Nonnull ResourceLocation angryTexture = new ResourceLocation("enderzoo:entity/wither_cat_angry.png");

  public RenderWitherCat(RenderManager rm) {
    super(rm, new ModelWitherCat(), 0.4F);
    addLayer(new AngryLayer());
  }

  @Override
  protected ResourceLocation getEntityTexture(@Nonnull EntityWitherCat p_110775_1_) {
    return texture;
  }

  @Override
  public void doRender(@Nonnull EntityWitherCat entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
    super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    // Debug to show hit box
    // RenderUtil.renderEntityBoundingBox(entity, x, y, z);
  }

  @Override
  protected void preRenderCallback(@Nonnull EntityWitherCat entity, float partialTick) {

    EntityWitherCat cat = entity;
    float scale = cat.getScale();
    if (scale > 1) {
      if (cat.getGrowthMode() == GrowthMode.SHRINK) {
        partialTick *= -1;
      }
      scale = Math.min(cat.getAngryScale(), scale + cat.getScaleInc() * partialTick);
      float widthFactor = 1 - (cat.getAngryScale() - scale);
      GL11.glScalef(scale + (0.25f * widthFactor), scale, scale - (0.1f * widthFactor));
    }

  }

  private class AngryLayer implements LayerRenderer<EntityWitherCat> {

    @Override
    public void doRenderLayer(@Nonnull EntityWitherCat cat, float p_177201_2_, float p_177201_3_, float p_177201_4_, float p_177201_5_, float p_177201_6_,
        float p_177201_7_, float p_177201_8_) {

      float blendFactor = 1.0F;
      float scale = cat.getScale();
      blendFactor = 1 - (cat.getAngryScale() - scale);

      if (blendFactor > 0) {
        bindTexture(angryTexture);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);

        GL14.glBlendColor(1.0f, 1.0f, 1.0f, blendFactor);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1, -1);

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
        GL11.glEnable(GL11.GL_LIGHTING);

        getMainModel().render(cat, p_177201_2_, p_177201_3_, p_177201_5_, p_177201_6_, p_177201_7_, p_177201_8_);
        setLightmap(cat);
      }

    }

    @Override
    public boolean shouldCombineTextures() {
      return false;
    }

  }

  public static class Factory implements IRenderFactory<EntityWitherCat> {

    @Override
    public Render<? super EntityWitherCat> createRenderFor(RenderManager manager) {
      return new RenderWitherCat(manager);
    }
  }

}
