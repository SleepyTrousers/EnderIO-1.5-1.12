package crazypants.enderio.zoo.entity.render;

import crazypants.enderio.zoo.entity.EntityOwl;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * ModelOwl - Yulife Created using Tabula 5.1.0
 */
public class ModelOwl extends ModelBase {

  public ModelRenderer body;
  public ModelRenderer head;
  public ModelRenderer legL;
  public ModelRenderer legR;
  public ModelRenderer tailBase;
  public ModelRenderer footL;
  public ModelRenderer footR;
  public ModelRenderer wingR;
  public ModelRenderer wingL;
  public ModelRenderer earL;
  public ModelRenderer earR;
  public ModelRenderer beak;

  public ModelRenderer tail1;
  public ModelRenderer tail2;
  public ModelRenderer tail3;

  private boolean useEars = true;

  public ModelOwl() {

    textureWidth = 64;
    textureHeight = 32;
    wingR = new ModelRenderer(this, 23, 0);
    wingR.setRotationPoint(-3.0F, 0.0F, 0.0F);
    wingR.addBox(-1.0F, 0.0F, -3.0F, 1, 5, 5, 0.0F);
    legL = new ModelRenderer(this, 23, 11);
    legL.setRotationPoint(1.5F, 7.0F, 0.0F);
    legL.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
    tail1 = new ModelRenderer(this, 58, 5);
    tail1.setRotationPoint(0.0F, 0.0F, 0.0F);
    tail1.addBox(0.0F, 2.0F, -1.7F, 2, 3, 1, 0.0F);
    setRotateAngle(tail1, 0.0F, 0.0F, 0.9599310885968813F);
    if (useEars) {
      earR = new ModelRenderer(this, 27, 29);
      earR.setRotationPoint(0.0F, 0.0F, 0.0F);
      earR.addBox(-5.0F, -8.0F, -3.0F, 3, 2, 1, 0.0F);
    }
    tailBase = new ModelRenderer(this, 56, 0);
    tailBase.setRotationPoint(0.0F, 3.5F, 2.2F);
    tailBase.addBox(-1.5F, 1.0F, -1.8F, 3, 3, 1, 0.0F);
    setRotateAngle(tailBase, 0.6373942428283291F, 0.0F, 0.0F);
    footR = new ModelRenderer(this, 28, 11);
    footR.setRotationPoint(0.0F, 1.0F, 0.0F);
    footR.addBox(-1.0F, 0.0F, -2.0F, 2, 1, 3, 0.0F);
    wingL = new ModelRenderer(this, 23, 0);
    wingL.mirror = true;
    wingL.setRotationPoint(3.0F, 0.0F, -0.5F);
    wingL.addBox(0.0F, 0.0F, -2.5F, 1, 5, 5, 0.0F);
    head = new ModelRenderer(this, 0, 20);
    head.setRotationPoint(0.0F, 15.0F, -0.5F);
    head.addBox(-3.5F, -6.0F, -3.0F, 7, 6, 6, 0.0F);
    setRotateAngle(head, 0.0F, 0.045553093477052F, 0.0F);
    tail2 = new ModelRenderer(this, 58, 5);
    tail2.mirror = true;
    tail2.setRotationPoint(0.0F, 0.0F, 0.0F);
    tail2.addBox(-2.0F, 2.0F, -1.7F, 2, 3, 1, 0.0F);
    setRotateAngle(tail2, 0.0F, 0.0F, -0.9599310885968813F);
    tail3 = new ModelRenderer(this, 58, 5);
    tail3.setRotationPoint(0.0F, 0.0F, 0.0F);
    tail3.addBox(-1.0F, 2.0F, -1.7F, 2, 3, 1, 0.0F);
    legR = new ModelRenderer(this, 23, 11);
    legR.setRotationPoint(-1.5F, 7.0F, 0.0F);
    legR.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
    footL = new ModelRenderer(this, 28, 11);
    footL.setRotationPoint(0.0F, 1.0F, 0.0F);
    footL.addBox(-1.0F, 0.0F, -2.0F, 2, 1, 3, 0.0F);
    if (useEars) {
      earL = new ModelRenderer(this, 27, 29);
      earL.mirror = true;
      earL.setRotationPoint(0.0F, 0.0F, 0.0F);
      earL.addBox(2.0F, -8.0F, -3.0F, 3, 2, 1, 0.0F);
    }
    beak = new ModelRenderer(this, 36, 29);
    beak.setRotationPoint(-0.5F, -2.3F, -0.4F);
    beak.addBox(0.0F, -0.8F, -4.0F, 1, 1, 2, 0.0F);
    setRotateAngle(beak, 0.36425021489121656F, 0.0F, 0.0F);
    body = new ModelRenderer(this, 0, 0);
    body.setRotationPoint(0.0F, 15.0F, 0.0F);
    body.addBox(-3.0F, 0.0F, -3.0F, 6, 7, 5, 0.0F);
    body.addChild(wingR);
    body.addChild(legL);
    tailBase.addChild(tail1);
    if (useEars) {
      head.addChild(earR);
      head.addChild(earL);
    }
    body.addChild(tailBase);
    legR.addChild(footR);
    body.addChild(wingL);
    tailBase.addChild(tail2);
    tailBase.addChild(tail3);
    body.addChild(legR);
    legL.addChild(footL);

    head.addChild(beak);
  }

  /**
   * This is a helper function from Tabula to set the rotation of model parts
   */
  public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }

  @Override
  public void render(Entity entity, float time, float limbSwing, float f2, float headY, float headX, float scale) {
    super.render(entity, time, limbSwing, f2, headY, headX, scale);

    setRotationAngles(time, limbSwing, f2, headY, headX, scale, entity);

    float height = 25;
    // float owlScale = 0.65f;
    float owlScale = 1f;
    float transFactor = 1 - owlScale;

    if (isChild) {

      float headScale = owlScale * 0.6f;
      owlScale *= 0.5f;
      float translateScale = 1 - owlScale;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, translateScale * (height - 2) * scale, 0.0F);
      GlStateManager.scale(headScale, headScale, headScale);
      head.render(scale);
      GlStateManager.popMatrix();

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, translateScale * height * scale, 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);

      body.render(scale);
      GlStateManager.popMatrix();
    } else {

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, transFactor * height * scale, 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);

      head.render(scale);
      body.render(scale);

      GlStateManager.popMatrix();
    }
  }

  @Override
  public void setRotationAngles(float limbSwing1, float limbSwing2, float rotationAngle, float headY, float headX, float yOffset, Entity entity) {

    head.rotateAngleX = headX / (180F / (float) Math.PI);
    head.rotateAngleY = headY / (180F / (float) Math.PI);

    if (entity instanceof EntityOwl) {
      EntityOwl owl = (EntityOwl) entity;
      body.rotateAngleX = owl.getBodyAngle();
      wingL.rotateAngleZ = -owl.getWingAngle();
      wingR.rotateAngleZ = owl.getWingAngle();
    } else {
      body.rotateAngleX = 0;
      wingL.rotateAngleZ = 0;
      wingR.rotateAngleZ = 0;
    }

    if (!entity.isAirBorne) {
      float limbSpeed = 2.5f;
      legR.rotateAngleX = MathHelper.cos(limbSwing1 * limbSpeed) * 1.4F * limbSwing2;
      legL.rotateAngleX = MathHelper.cos(limbSwing1 * limbSpeed + (float) Math.PI) * 1.4F * limbSwing2;
    } else {
      legR.rotateAngleX = 0;
      legL.rotateAngleX = 0;
    }

  }
}
