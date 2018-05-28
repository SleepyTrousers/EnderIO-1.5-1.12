package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.entity.EntityDireWolf;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelDireWolf extends ModelBase {

  /** main box for the wolf head */
  public ModelRenderer wolfHeadMain;
  /** The wolf's body */
  public ModelRenderer wolfBody;
  /** Wolf'se first leg */
  public ModelRenderer wolfLeg1;
  /** Wolf's second leg */
  public ModelRenderer wolfLeg2;
  /** Wolf's third leg */
  public ModelRenderer wolfLeg3;
  /** Wolf's fourth leg */
  public ModelRenderer wolfLeg4;
  /** The wolf's tail */
  ModelRenderer wolfTail;
  /** The wolf's mane */
  ModelRenderer wolfMane;

  public ModelDireWolf() {
    float f = 0.0F;
    float f1 = 13.5F;
    wolfHeadMain = new ModelRenderer(this, 0, 0);
    wolfHeadMain.addBox(-3.0F, -3.0F, -4.0F, 6, 6, 6, f);
    wolfHeadMain.setRotationPoint(-1.0F, f1, -7.0F);

    wolfBody = new ModelRenderer(this, 18, 17);
    wolfBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f);
    wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);

    wolfMane = new ModelRenderer(this, 34, 0);
    wolfMane.addBox(-3.5F, -3.0F, -3.0F, 7, 6, 7, f);
    wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);

    wolfLeg1 = new ModelRenderer(this, 48, 22);
    wolfLeg1.addBox(-1.7F, 0.0F, -1.0F, 2, 8, 2, f);
    wolfLeg1.setTextureOffset(52, 14).addBox(-1.75F, -2.5F, -2F, 2, 4, 4, f);
    wolfLeg1.setRotationPoint(-2.5F, 16.0F, 6.0F);

    wolfLeg2 = new ModelRenderer(this, 48, 22);
    wolfLeg2.addBox(-1.8F, 0.0F, -1.0F, 2, 8, 2, f);
    wolfLeg2.setTextureOffset(52, 14).addBox(-1.75F, -2.5F, -2F, 2, 4, 4, f);
    wolfLeg2.setRotationPoint(2.0F, 16.0F, 6.0F);

    wolfLeg3 = new ModelRenderer(this, 0, 22);
    wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
    wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
    wolfLeg4 = new ModelRenderer(this, 0, 22);
    wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
    wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);

    wolfTail = new ModelRenderer(this, 9, 22);
    wolfTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
    wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);

    wolfHeadMain.setTextureOffset(16, 18).addBox(-2.5F, -5.0F, -1.5F, 1, 2, 2, f);
    wolfHeadMain.setTextureOffset(16, 18).addBox(1.5F, -5.0F, -1.5F, 1, 2, 2, f);
    wolfHeadMain.setTextureOffset(0, 14).addBox(-1.5F, 0.0F, -7.0F, 3, 3, 4, f);
  }

  /**
   * Sets the models various rotation angles then renders the model.
   */
  @Override
  public void render(@Nonnull Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
    super.render(entity, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, entity);

    // if(isChild) {
    // float f6 = 2.0F;
    // GL11.glPushMatrix();
    // GL11.glTranslatef(0.0F, 5.0F * p_78088_7_, 2.0F * p_78088_7_);
    // // wolfHeadMain.renderWithRotation(p_78088_7_);
    // GL11.glPopMatrix();
    // GL11.glPushMatrix();
    // GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
    // GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
    // // wolfBody.render(p_78088_7_);
    // wolfLeg1.render(p_78088_7_);
    // wolfLeg2.render(p_78088_7_);
    // wolfLeg3.render(p_78088_7_);
    // wolfLeg4.render(p_78088_7_);
    // // wolfTail.renderWithRotation(p_78088_7_);
    // // wolfMane.render(p_78088_7_);
    // GL11.glPopMatrix();
    // } else {
    wolfHeadMain.renderWithRotation(p_78088_7_);
    wolfBody.render(p_78088_7_);
    wolfLeg1.render(p_78088_7_);
    wolfLeg2.render(p_78088_7_);
    wolfLeg3.render(p_78088_7_);
    wolfLeg4.render(p_78088_7_);
    wolfTail.renderWithRotation(p_78088_7_);
    wolfMane.render(p_78088_7_);
    // }
  }

  @Override
  public void setLivingAnimations(@Nonnull EntityLivingBase entity, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
    EntityDireWolf entitywolf = (EntityDireWolf) entity;

    if (entitywolf.isAngry()) {
      wolfTail.rotateAngleY = 0.0F;
    } else {
      wolfTail.rotateAngleY = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
    }

    // if(entitywolf.isSitting()) {
    // wolfMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
    // wolfMane.rotateAngleX = ((float) Math.PI * 2F / 5F);
    // wolfMane.rotateAngleY = 0.0F;
    // wolfBody.setRotationPoint(0.0F, 18.0F, 0.0F);
    // wolfBody.rotateAngleX = ((float) Math.PI / 4F);
    // wolfTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
    // wolfLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
    // wolfLeg1.rotateAngleX = ((float) Math.PI * 3F / 2F);
    // wolfLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
    // wolfLeg2.rotateAngleX = ((float) Math.PI * 3F / 2F);
    // wolfLeg3.rotateAngleX = 5.811947F;
    // wolfLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
    // wolfLeg4.rotateAngleX = 5.811947F;
    // wolfLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
    // } else {
    wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
    wolfBody.rotateAngleX = ((float) Math.PI / 2F);
    wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
    wolfMane.rotateAngleX = wolfBody.rotateAngleX;
    wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
    // wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
    // wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
    // wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
    // wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
    wolfLeg1.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
    wolfLeg2.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
    wolfLeg3.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
    wolfLeg4.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
    // }

    // //Begging head tilt I think
    // wolfHeadMain.rotateAngleZ = entitywolf.getInterestedAngle(p_78086_4_) + entitywolf.getShakeAngle(p_78086_4_, 0.0F);
    // wolfMane.rotateAngleZ = entitywolf.getShakeAngle(p_78086_4_, -0.08F);
    // wolfBody.rotateAngleZ = entitywolf.getShakeAngle(p_78086_4_, -0.16F);
    // wolfTail.rotateAngleZ = entitywolf.getShakeAngle(p_78086_4_, -0.2F);
  }

  @Override
  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_,
      @Nonnull Entity p_78087_7_) {
    super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
    wolfHeadMain.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
    wolfHeadMain.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
    wolfTail.rotateAngleX = p_78087_3_;
  }

}
