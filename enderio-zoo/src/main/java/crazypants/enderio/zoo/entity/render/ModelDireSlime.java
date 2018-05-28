package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDireSlime extends ModelBase {
  ModelRenderer[] sliceRenderers = new ModelRenderer[16];
  ModelRenderer[] coreRenderers = new ModelRenderer[24];
  ModelRenderer coreRendererClay;
  ModelRenderer coreRenderer;

  public ModelDireSlime() {
    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i] = new ModelRenderer(this, 0, i);
      this.sliceRenderers[i].setTextureSize(64, 64);
      this.sliceRenderers[i].addBox(-8.0F, 8 + i, -8.0F, 16, 1, 16);
    }

    this.coreRendererClay = new ModelRenderer(this, 0, 32);
    this.coreRendererClay.setTextureSize(64, 64);
    this.coreRendererClay.addBox(-3.0F, 13.0F, -3.0F, 6, 6, 6);

    for (int i = 0; i < this.coreRenderers.length; ++i) {
      this.coreRenderers[i] = new ModelRenderer(this, 32, 32 + i);
      this.coreRenderers[i].setTextureSize(64, 64);
      this.coreRenderers[i].addBox(-3.0F, 13.0F, -3.0F, 6, 6, 6);
    }
  }

  @Override
  public void setLivingAnimations(@Nonnull EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
    EntityMagmaCube entitymagmacube = (EntityMagmaCube) p_78086_1_;
    float f3 = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * p_78086_4_;
    int size = entitymagmacube.getSlimeSize();

    if (f3 < 0.0F) {
      f3 = 0.0F;
    }

    if (size > 1) {
      int i = (p_78086_1_.ticksExisted >> 2) % 8;
      coreRenderer = coreRenderers[i];
    } else {
      coreRenderer = coreRendererClay;
    }

    coreRenderer.rotationPointY = f3 * 1.7F;

    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i].rotationPointY = (-(4 - i)) * f3 * 1.7F;
    }
  }

  @Override
  public void render(@Nonnull Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
    this.coreRenderer.render(p_78088_7_);

    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i].render(p_78088_7_);
    }
  }
}