package crazypants.enderio.zoo.entity.render;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.zoo.entity.EntityWitherCat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

//Copied straight from ModelOelot to remove the casts
public class ModelWitherCat extends ModelBase {

  /** The back left leg model for the Ocelot. */
  ModelRenderer ocelotBackLeftLeg;
  /** The back right leg model for the Ocelot. */
  ModelRenderer ocelotBackRightLeg;
  /** The front left leg model for the Ocelot. */
  ModelRenderer ocelotFrontLeftLeg;
  /** The front right leg model for the Ocelot. */
  ModelRenderer ocelotFrontRightLeg;
  /** The tail model for the Ocelot. */
  ModelRenderer ocelotTail;
  /** The second part of tail model for the Ocelot. */
  ModelRenderer ocelotTail2;
  /** The head model for the Ocelot. */
  ModelRenderer ocelotHead;
  /** The body model for the Ocelot. */
  ModelRenderer ocelotBody;
  int field_78163_i = 1;

  public ModelWitherCat() {
    setTextureOffset("head.main", 0, 0);
    setTextureOffset("head.nose", 0, 24);
    setTextureOffset("head.ear1", 0, 10);
    setTextureOffset("head.ear2", 6, 10);
    ocelotHead = new ModelRenderer(this, "head");
    ocelotHead.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
    ocelotHead.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
    ocelotHead.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
    ocelotHead.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
    ocelotHead.setRotationPoint(0.0F, 15.0F, -9.0F);
    ocelotBody = new ModelRenderer(this, 20, 0);
    ocelotBody.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
    ocelotBody.setRotationPoint(0.0F, 12.0F, -10.0F);
    ocelotTail = new ModelRenderer(this, 0, 15);
    ocelotTail.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
    ocelotTail.rotateAngleX = 0.9F;
    ocelotTail.setRotationPoint(0.0F, 15.0F, 8.0F);
    ocelotTail2 = new ModelRenderer(this, 4, 15);
    ocelotTail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
    ocelotTail2.setRotationPoint(0.0F, 20.0F, 14.0F);
    ocelotBackLeftLeg = new ModelRenderer(this, 8, 13);
    ocelotBackLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
    ocelotBackLeftLeg.setRotationPoint(1.1F, 18.0F, 5.0F);
    ocelotBackRightLeg = new ModelRenderer(this, 8, 13);
    ocelotBackRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
    ocelotBackRightLeg.setRotationPoint(-1.1F, 18.0F, 5.0F);
    ocelotFrontLeftLeg = new ModelRenderer(this, 40, 0);
    ocelotFrontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
    ocelotFrontLeftLeg.setRotationPoint(1.2F, 13.8F, -5.0F);
    ocelotFrontRightLeg = new ModelRenderer(this, 40, 0);
    ocelotFrontRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
    ocelotFrontRightLeg.setRotationPoint(-1.2F, 13.8F, -5.0F);
  }

  /**
   * Sets the models various rotation angles then renders the model.
   */
  @Override
  public void render(@Nonnull Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

    if (isChild) {
      float f6 = 2.0F;
      GL11.glPushMatrix();
      GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
      GL11.glTranslatef(0.0F, 10.0F * p_78088_7_, 4.0F * p_78088_7_);
      ocelotHead.render(p_78088_7_);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
      GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
      ocelotBody.render(p_78088_7_);
      ocelotBackLeftLeg.render(p_78088_7_);
      ocelotBackRightLeg.render(p_78088_7_);
      ocelotFrontLeftLeg.render(p_78088_7_);
      ocelotFrontRightLeg.render(p_78088_7_);
      ocelotTail.render(p_78088_7_);
      ocelotTail2.render(p_78088_7_);
      GL11.glPopMatrix();
    } else {
      ocelotHead.render(p_78088_7_);
      ocelotBody.render(p_78088_7_);
      ocelotTail.render(p_78088_7_);
      ocelotTail2.render(p_78088_7_);
      ocelotBackLeftLeg.render(p_78088_7_);
      ocelotBackRightLeg.render(p_78088_7_);
      ocelotFrontLeftLeg.render(p_78088_7_);
      ocelotFrontRightLeg.render(p_78088_7_);
    }
  }

  /**
   * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms and legs, where par1 represents the time(so
   * that arms and legs swing back and forth) and par2 represents how "far" arms and legs can swing at most.
   */
  @Override
  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_,
      @Nonnull Entity p_78087_7_) {

    ocelotHead.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
    ocelotHead.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);

    if (field_78163_i != 3) {
      ocelotBody.rotateAngleX = ((float) Math.PI / 2F);

      if (field_78163_i == 2) {
        ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.0F * p_78087_2_;
        ocelotBackRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + 0.3F) * 1.0F * p_78087_2_;
        ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI + 0.3F) * 1.0F * p_78087_2_;
        ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.0F * p_78087_2_;
        ocelotTail2.rotateAngleX = 1.7278761F + ((float) Math.PI / 10F) * MathHelper.cos(p_78087_1_) * p_78087_2_;
      } else {
        ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.0F * p_78087_2_;
        ocelotBackRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.0F * p_78087_2_;
        ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.0F * p_78087_2_;
        ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.0F * p_78087_2_;

        if (field_78163_i == 1) {
          ocelotTail2.rotateAngleX = 1.7278761F + ((float) Math.PI / 4F) * MathHelper.cos(p_78087_1_) * p_78087_2_;
        } else {
          ocelotTail2.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(p_78087_1_) * p_78087_2_;
        }
      }
    }
  }

  /**
   * Used for easily adding entity-dependent animations. The second and third float params here are the same second and third as in the setRotationAngles
   * method.
   */
  @Override
  public void setLivingAnimations(@Nonnull EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
    EntityWitherCat entityocelot = (EntityWitherCat) p_78086_1_;
    ocelotBody.rotationPointY = 12.0F;
    ocelotBody.rotationPointZ = -10.0F;
    ocelotHead.rotationPointY = 15.0F;
    ocelotHead.rotationPointZ = -9.0F;
    ocelotTail.rotationPointY = 15.0F;
    ocelotTail.rotationPointZ = 8.0F;
    ocelotTail2.rotationPointY = 20.0F;
    ocelotTail2.rotationPointZ = 14.0F;
    ocelotFrontLeftLeg.rotationPointY = ocelotFrontRightLeg.rotationPointY = 13.8F;
    ocelotFrontLeftLeg.rotationPointZ = ocelotFrontRightLeg.rotationPointZ = -5.0F;
    ocelotBackLeftLeg.rotationPointY = ocelotBackRightLeg.rotationPointY = 18.0F;
    ocelotBackLeftLeg.rotationPointZ = ocelotBackRightLeg.rotationPointZ = 5.0F;
    ocelotTail.rotateAngleX = 0.9F;

    if (entityocelot.isSneaking()) {
      ++ocelotBody.rotationPointY;
      ocelotHead.rotationPointY += 2.0F;
      ++ocelotTail.rotationPointY;
      ocelotTail2.rotationPointY += -4.0F;
      ocelotTail2.rotationPointZ += 2.0F;
      ocelotTail.rotateAngleX = ((float) Math.PI / 2F);
      ocelotTail2.rotateAngleX = ((float) Math.PI / 2F);
      field_78163_i = 0;
    } else if (entityocelot.isSprinting()) {
      ocelotTail2.rotationPointY = ocelotTail.rotationPointY;
      ocelotTail2.rotationPointZ += 2.0F;
      ocelotTail.rotateAngleX = ((float) Math.PI / 2F);
      ocelotTail2.rotateAngleX = ((float) Math.PI / 2F);
      field_78163_i = 2;
    }
    // else if (entityocelot.isSitting())
    // {
    // ocelotBody.rotateAngleX = ((float)Math.PI / 4F);
    // ocelotBody.rotationPointY += -4.0F;
    // ocelotBody.rotationPointZ += 5.0F;
    // ocelotHead.rotationPointY += -3.3F;
    // ++ocelotHead.rotationPointZ;
    // ocelotTail.rotationPointY += 8.0F;
    // ocelotTail.rotationPointZ += -2.0F;
    // ocelotTail2.rotationPointY += 2.0F;
    // ocelotTail2.rotationPointZ += -0.8F;
    // ocelotTail.rotateAngleX = 1.7278761F;
    // ocelotTail2.rotateAngleX = 2.670354F;
    // ocelotFrontLeftLeg.rotateAngleX = ocelotFrontRightLeg.rotateAngleX = -0.15707964F;
    // ocelotFrontLeftLeg.rotationPointY = ocelotFrontRightLeg.rotationPointY = 15.8F;
    // ocelotFrontLeftLeg.rotationPointZ = ocelotFrontRightLeg.rotationPointZ = -7.0F;
    // ocelotBackLeftLeg.rotateAngleX = ocelotBackRightLeg.rotateAngleX = -((float)Math.PI / 2F);
    // ocelotBackLeftLeg.rotationPointY = ocelotBackRightLeg.rotationPointY = 21.0F;
    // ocelotBackLeftLeg.rotationPointZ = ocelotBackRightLeg.rotationPointZ = 1.0F;
    // field_78163_i = 3;
    // }
    else {
      field_78163_i = 1;
    }
  }

}
