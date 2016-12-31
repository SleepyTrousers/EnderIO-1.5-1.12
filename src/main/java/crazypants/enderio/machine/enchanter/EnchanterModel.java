package crazypants.enderio.machine.enchanter;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class EnchanterModel extends ModelBase {

  ModelRenderer coverRight;
  ModelRenderer coverMiddle;
  ModelRenderer coverLeft;
  ModelRenderer pageLeft;
  ModelRenderer sheetRight;
  ModelRenderer pageRight;

  public EnchanterModel() {

    textureWidth = 64;
    textureHeight = 64;

    coverRight = new ModelRenderer(this, 0, 32);
    coverRight.addBox(-0.1F, 0F, -4F, 5, 1, 8);
    coverRight.setRotationPoint(1F, 11F, 0F);
    coverRight.setTextureSize(64, 64);
    coverRight.mirror = true;
    setRotation(coverRight, 0.2268928F + 0.155f, 0F, -0.0523599F);
    coverMiddle = new ModelRenderer(this, 26, 32);
    coverMiddle.addBox(-1F, 0F, -4F, 2, 1, 8);
    coverMiddle.setRotationPoint(0F, 11F, 0F);
    coverMiddle.setTextureSize(64, 64);
    coverMiddle.mirror = true;
    setRotation(coverMiddle, 0.2268928F + 0.155f, 0F, 0F);
    coverLeft = new ModelRenderer(this, 0, 32);
    coverLeft.addBox(-5F, 0F, -4F, 5, 1, 8);
    coverLeft.setRotationPoint(-0.9F, 11F, 0F);
    coverLeft.setTextureSize(64, 64);
    coverLeft.mirror = true;
    setRotation(coverLeft, 0.2268928F + 0.155f, 0F, 0.0523599F);
    pageLeft = new ModelRenderer(this, 0, 41);
    pageLeft.addBox(-5F, -1F, -3.5F, 5, 1, 7);
    pageLeft.setRotationPoint(0F, 11F, 0F);
    pageLeft.setTextureSize(64, 64);
    pageLeft.mirror = true;
    setRotation(pageLeft, 0.2268928F + 0.155f, 0F, 0.0523599F);
    sheetRight = new ModelRenderer(this, 0, 41);
    sheetRight.addBox(-0.1F, -0.8F, -3.5F, 5, 0, 7);
    sheetRight.setRotationPoint(0F, 11F, 0F);
    sheetRight.setTextureSize(64, 64);
    sheetRight.mirror = true;
    setRotation(sheetRight, 0.2268928F + 0.155f, 0.0349066F, -0.0349066F);
    pageRight = new ModelRenderer(this, 24, 41);
    pageRight.addBox(-0.1F, -1F, -3.5F, 5, 1, 7);
    pageRight.setRotationPoint(0F, 11F, 0F);
    pageRight.setTextureSize(64, 64);
    pageRight.mirror = true;
    setRotation(pageRight, 0.2268928F + 0.155f, 0F, -0.0523599F);
  }

  @Override
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
    coverRight.render(f5);
    coverMiddle.render(f5);
    coverLeft.render(f5);
    pageLeft.render(f5);
    sheetRight.render(f5);
    pageRight.render(f5);
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
