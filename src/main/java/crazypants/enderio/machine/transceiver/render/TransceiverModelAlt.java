package crazypants.enderio.machine.transceiver.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.transceiver.TileTransceiver;

public class TransceiverModelAlt extends ModelBase implements IModel {

    private static final String TEXTURE = "enderio:models/transceiverAlt.png";

    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
    ModelRenderer Shape12;
    ModelRenderer Shape13;
    ModelRenderer Shape14;
    ModelRenderer Shape15;
    ModelRenderer Shape16;
    ModelRenderer Shape17;
    ModelRenderer Shape18;
    ModelRenderer Shape19;
    ModelRenderer Shape20;
    ModelRenderer Shape21;
    ModelRenderer Shape22;
    ModelRenderer Shape23;
    ModelRenderer Shape24;
    ModelRenderer Shape25;

    public TransceiverModelAlt() {
        textureWidth = 32;
        textureHeight = 32;

        Shape1 = new ModelRenderer(this, 0, 7);
        Shape1.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape1.setRotationPoint(-3F, 24F, -8F);
        Shape1.setTextureSize(32, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 1.570796F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 6, 1, 5);
        Shape2.setRotationPoint(7F, 23F, 3F);
        Shape2.setTextureSize(32, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 1.570796F, 1.570796F, 0F);
        Shape3 = new ModelRenderer(this, 0, 0);
        Shape3.addBox(0F, 0F, 0F, 6, 1, 5);
        Shape3.setRotationPoint(-3F, 23F, 7F);
        Shape3.setTextureSize(32, 32);
        Shape3.mirror = true;
        setRotation(Shape3, 1.570796F, 0F, 0F);
        Shape4 = new ModelRenderer(this, 0, 7);
        Shape4.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape4.setRotationPoint(-8F, 24F, 3F);
        Shape4.setTextureSize(32, 32);
        Shape4.mirror = true;
        setRotation(Shape4, 1.570796F, 1.570796F, 0F);
        Shape5 = new ModelRenderer(this, 0, 0);
        Shape5.addBox(0F, 0F, 0F, 6, 1, 5);
        Shape5.setRotationPoint(-3F, 14F, -8F);
        Shape5.setTextureSize(32, 32);
        Shape5.mirror = true;
        setRotation(Shape5, 1.570796F, 0F, 0F);
        Shape6 = new ModelRenderer(this, 0, 7);
        Shape6.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape6.setRotationPoint(-7F, 13F, 8F);
        Shape6.setTextureSize(32, 32);
        Shape6.mirror = true;
        // setRotation(Shape6, 1.570796F, 1.570796F, 1.570796F);
        setRotation(Shape6, 0F, 1.570796F, 1.570796F);
        Shape7 = new ModelRenderer(this, 0, 7);
        Shape7.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape7.setRotationPoint(-8F, 13F, -8F);
        Shape7.setTextureSize(32, 32);
        Shape7.mirror = true;
        setRotation(Shape7, 1.570796F, 0F, 1.570796F);
        Shape8 = new ModelRenderer(this, 0, 0);
        Shape8.addBox(0F, 0F, 0F, 6, 1, 5);
        Shape8.setRotationPoint(-8F, 14F, 3F);
        Shape8.setTextureSize(32, 32);
        Shape8.mirror = true;
        setRotation(Shape8, 1.570796F, 1.570796F, 0F);
        Shape9 = new ModelRenderer(this, 0, 7);
        Shape9.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape9.setRotationPoint(-3F, 23F, 2F);
        Shape9.setTextureSize(32, 32);
        Shape9.mirror = true;
        setRotation(Shape9, 0F, 0F, 0F);
        Shape10 = new ModelRenderer(this, 0, 7);
        Shape10.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape10.setRotationPoint(2F, 23F, 3F);
        Shape10.setTextureSize(32, 32);
        Shape10.mirror = true;
        setRotation(Shape10, 0F, 1.570796F, 0F);
        Shape11 = new ModelRenderer(this, 0, 7);
        Shape11.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape11.setRotationPoint(-3F, 23F, -8F);
        Shape11.setTextureSize(32, 32);
        Shape11.mirror = true;
        setRotation(Shape11, 0F, 0F, 0F);
        Shape12 = new ModelRenderer(this, 0, 7);
        Shape12.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape12.setRotationPoint(-8F, 23F, 3F);
        Shape12.setTextureSize(32, 32);
        Shape12.mirror = true;
        setRotation(Shape12, 0F, 1.570796F, 0F);
        Shape13 = new ModelRenderer(this, 0, 7);
        Shape13.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape13.setRotationPoint(-8F, 8F, 3F);
        Shape13.setTextureSize(32, 32);
        Shape13.mirror = true;
        setRotation(Shape13, 0F, 1.570796F, 0F);
        Shape14 = new ModelRenderer(this, 0, 7);
        Shape14.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape14.setRotationPoint(-3F, 8F, 2F);
        Shape14.setTextureSize(32, 32);
        Shape14.mirror = true;
        setRotation(Shape14, 0F, 0F, 0F);
        Shape15 = new ModelRenderer(this, 0, 7);
        Shape15.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape15.setRotationPoint(-3F, 8F, -8F);
        Shape15.setTextureSize(32, 32);
        Shape15.mirror = true;
        setRotation(Shape15, 0F, 0F, 0F);
        Shape16 = new ModelRenderer(this, 0, 7);
        Shape16.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape16.setRotationPoint(2F, 8F, 3F);
        Shape16.setTextureSize(32, 32);
        Shape16.mirror = true;
        setRotation(Shape16, 0F, 1.570796F, 0F);
        Shape17 = new ModelRenderer(this, 0, 7);
        Shape17.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape17.setRotationPoint(7F, 14F, 3F);
        Shape17.setTextureSize(32, 32);
        Shape17.mirror = true;
        setRotation(Shape17, 1.570796F, 1.570796F, 0F);
        Shape18 = new ModelRenderer(this, 0, 7);
        Shape18.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape18.setRotationPoint(-7F, 13F, -2F);
        Shape18.setTextureSize(32, 32);
        Shape18.mirror = true;
        setRotation(Shape18, 0F, 1.570796F, 1.570796F);
        Shape19 = new ModelRenderer(this, 0, 7);
        Shape19.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape19.setRotationPoint(8F, 13F, -2F);
        Shape19.setTextureSize(32, 32);
        Shape19.mirror = true;
        setRotation(Shape19, 0F, 1.570796F, 1.570796F);
        Shape20 = new ModelRenderer(this, 0, 7);
        Shape20.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape20.setRotationPoint(8F, 13F, 8F);
        Shape20.setTextureSize(32, 32);
        Shape20.mirror = true;
        setRotation(Shape20, 0F, 1.570796F, 1.570796F);
        Shape21 = new ModelRenderer(this, 0, 7);
        Shape21.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape21.setRotationPoint(-3F, 14F, 7F);
        Shape21.setTextureSize(32, 32);
        Shape21.mirror = true;
        setRotation(Shape21, 1.570796F, 0F, 0F);
        Shape22 = new ModelRenderer(this, 0, 7);
        Shape22.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape22.setRotationPoint(2F, 13F, -8F);
        Shape22.setTextureSize(32, 32);
        Shape22.mirror = true;
        setRotation(Shape22, 1.570796F, 0F, 1.570796F);
        Shape23 = new ModelRenderer(this, 0, 0);
        Shape23.addBox(0F, 0F, 0F, 6, 1, 5);
        Shape23.setRotationPoint(-8F, 13F, 7F);
        Shape23.setTextureSize(32, 32);
        Shape23.mirror = true;
        setRotation(Shape23, 1.570796F, 0F, 1.570796F);
        Shape24 = new ModelRenderer(this, 0, 7);
        Shape24.addBox(0F, 0F, 0F, 6, 1, 6);
        Shape24.setRotationPoint(2F, 13F, 7F);
        Shape24.setTextureSize(32, 32);
        Shape24.mirror = true;
        setRotation(Shape24, 1.570796F, 0F, 1.570796F);
        Shape25 = new ModelRenderer(this, 0, 15);
        Shape25.addBox(0F, 0F, 0F, 8, 8, 8);
        Shape25.setRotationPoint(-4F, 12F, -4F);
        Shape25.setTextureSize(32, 32);
        Shape25.mirror = true;
        setRotation(Shape25, 0F, 0F, 0F);
    }

    @Override
    public void render() {

        RenderUtil.bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, -1.5f, 0.5f);
        GL11.glPushMatrix();

        float f5 = 0.0625f;
        Shape1.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
        Shape4.render(f5);
        Shape5.render(f5);
        Shape6.render(f5);
        Shape7.render(f5);
        Shape8.render(f5);
        Shape9.render(f5);
        Shape10.render(f5);
        Shape11.render(f5);
        Shape12.render(f5);
        Shape13.render(f5);
        Shape14.render(f5);
        Shape15.render(f5);
        Shape16.render(f5);
        Shape17.render(f5);
        Shape18.render(f5);
        Shape19.render(f5);
        Shape20.render(f5);
        Shape21.render(f5);
        Shape22.render(f5);
        Shape23.render(f5);
        Shape24.render(f5);
        // Shape25.render(f5);

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(TileTransceiver cube, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + 1, (float) z);
        render();
        GL11.glPopMatrix();
    }
}
