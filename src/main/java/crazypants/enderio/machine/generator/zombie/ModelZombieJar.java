package crazypants.enderio.machine.generator.zombie;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelZombieJar extends ModelBase {

    ModelRenderer Head;
    ModelRenderer electrode1;
    ModelRenderer electrode2;
    ModelRenderer electrode3;
    ModelRenderer electrode4;
    ModelRenderer electrode5;
    ModelRenderer electrode6;
    ModelRenderer electrode7;
    ModelRenderer electrode8;
    ModelRenderer electrode9;
    ModelRenderer electrode10;
    ModelRenderer electrode11;
    ModelRenderer electrode12;
    ModelRenderer glass1;
    ModelRenderer glass2;
    ModelRenderer glass3;
    ModelRenderer glass4;
    ModelRenderer top;
    ModelRenderer Lid1;
    ModelRenderer bottom;
    ModelRenderer Lid2;
    ModelRenderer Lid3;
    ModelRenderer Lid4;
    ModelRenderer Lid5;

    public ModelZombieJar() {
        this(0.0f);
    }

    public ModelZombieJar(float par1) {
        Head = new ModelRenderer(this, 0, 0);
        Head.setTextureSize(64, 64);
        Head.addBox(-4F, -4F, -4F, 8, 8, 8);
        Head.setRotationPoint(0F, -8F, 1F);
        electrode1 = new ModelRenderer(this, 31, 40);
        electrode1.setTextureSize(64, 64);
        electrode1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode1.setRotationPoint(-2.872653F, -11.50361F, -2.60217F);
        electrode2 = new ModelRenderer(this, 31, 40);
        electrode2.setTextureSize(64, 64);
        electrode2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode2.setRotationPoint(-0.2268921F, -9.821873F, -3.016515F);
        electrode3 = new ModelRenderer(this, 31, 40);
        electrode3.setTextureSize(64, 64);
        electrode3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode3.setRotationPoint(2.875378F, -10.08223F, -3.571606F);
        electrode4 = new ModelRenderer(this, 31, 40);
        electrode4.setTextureSize(64, 64);
        electrode4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode4.setRotationPoint(3.996085F, -9.873959F, -2.810457F);
        electrode5 = new ModelRenderer(this, 31, 40);
        electrode5.setTextureSize(64, 64);
        electrode5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode5.setRotationPoint(4.288532F, -8.994526F, 0.2129618F);
        electrode6 = new ModelRenderer(this, 31, 40);
        electrode6.setTextureSize(64, 64);
        electrode6.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode6.setRotationPoint(5.037488F, -10.05719F, 3.095634F);
        electrode7 = new ModelRenderer(this, 31, 40);
        electrode7.setTextureSize(64, 64);
        electrode7.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode7.setRotationPoint(-1.494982F, -11.74601F, 5.210991F);
        electrode8 = new ModelRenderer(this, 31, 40);
        electrode8.setTextureSize(64, 64);
        electrode8.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode8.setRotationPoint(1.150779F, -10.06427F, 4.796647F);
        electrode9 = new ModelRenderer(this, 31, 40);
        electrode9.setTextureSize(64, 64);
        electrode9.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode9.setRotationPoint(4.253049F, -10.32463F, 4.241555F);
        electrode10 = new ModelRenderer(this, 31, 40);
        electrode10.setTextureSize(64, 64);
        electrode10.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode10.setRotationPoint(-3.667958F, -11.76913F, -1.517876F);
        electrode11 = new ModelRenderer(this, 31, 40);
        electrode11.setTextureSize(64, 64);
        electrode11.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode11.setRotationPoint(-3.37551F, -10.8897F, 1.505543F);
        electrode12 = new ModelRenderer(this, 31, 40);
        electrode12.setTextureSize(64, 64);
        electrode12.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
        electrode12.setRotationPoint(-2.626554F, -11.95237F, 4.388215F);
        glass1 = new ModelRenderer(this, 36, 2);
        glass1.setTextureSize(64, 64);
        glass1.addBox(-6F, -7F, 0F, 12, 14, 0);
        glass1.setRotationPoint(0F, -8F, -5F);
        glass2 = new ModelRenderer(this, 36, 2);
        glass2.setTextureSize(64, 64);
        glass2.addBox(-6F, -7F, 0F, 12, 14, 0);
        glass2.setRotationPoint(-6F, -8F, 1F);
        glass3 = new ModelRenderer(this, 36, 2);
        glass3.setTextureSize(64, 64);
        glass3.addBox(-6F, -7F, 0F, 12, 14, 0);
        glass3.setRotationPoint(0F, -8F, 7F);
        glass4 = new ModelRenderer(this, 36, 2);
        glass4.setTextureSize(64, 64);
        glass4.addBox(-6F, -7F, 0F, 12, 14, 0);
        glass4.setRotationPoint(6F, -8F, 1F);
        top = new ModelRenderer(this, 1, 17);
        top.setTextureSize(64, 64);
        top.addBox(-6F, -0.5F, -6F, 12, 1, 12);
        top.setRotationPoint(0F, -15.5F, 1F);
        Lid1 = new ModelRenderer(this, 4, 33);
        Lid1.setTextureSize(64, 64);
        Lid1.addBox(-4.5F, -1F, -4.5F, 9, 2, 9);
        Lid1.setRotationPoint(0F, -17F, 1F);
        bottom = new ModelRenderer(this, 1, 17);
        bottom.setTextureSize(64, 64);
        bottom.addBox(-6F, -0.5F, -6F, 12, 1, 12);
        bottom.setRotationPoint(0F, -0.5F, 1F);
        Lid2 = new ModelRenderer(this, 12, 41);
        Lid2.setTextureSize(64, 64);
        Lid2.addBox(-4F, -1F, -0.5F, 8, 2, 1);
        Lid2.setRotationPoint(0F, -17F, -4F);
        Lid3 = new ModelRenderer(this, 12, 41);
        Lid3.setTextureSize(64, 64);
        Lid3.addBox(-4F, -1F, -0.5F, 8, 2, 1);
        Lid3.setRotationPoint(0F, -17F, 6F);
        Lid4 = new ModelRenderer(this, 5, 34);
        Lid4.setTextureSize(64, 64);
        Lid4.addBox(-0.5F, -1F, -4F, 1, 2, 8);
        Lid4.setRotationPoint(5F, -17F, 1F);
        Lid5 = new ModelRenderer(this, 5, 34);
        Lid5.setTextureSize(64, 64);
        Lid5.addBox(-0.5F, -1F, -4F, 1, 2, 8);
        Lid5.setRotationPoint(-5F, -17F, 1F);
    }

    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {

        Head.rotateAngleX = 0.03054326F;
        Head.rotateAngleY = 0.1745329F;
        Head.rotateAngleZ = 0.2392846F;
        Head.renderWithRotation(par7);

        electrode1.rotateAngleX = 0.03054326F;
        electrode1.rotateAngleY = 0.1745329F;
        electrode1.rotateAngleZ = 0.2392846F;
        electrode1.renderWithRotation(par7);

        electrode2.rotateAngleX = 0.03054326F;
        electrode2.rotateAngleY = 0.1745329F;
        electrode2.rotateAngleZ = 0.2392846F;
        electrode2.renderWithRotation(par7);

        electrode3.rotateAngleX = 0.03054326F;
        electrode3.rotateAngleY = 0.1745329F;
        electrode3.rotateAngleZ = 0.2392846F;
        electrode3.renderWithRotation(par7);

        electrode4.rotateAngleX = 0.03054326F;
        electrode4.rotateAngleY = 0.1745329F;
        electrode4.rotateAngleZ = 0.2392846F;
        electrode4.renderWithRotation(par7);

        electrode5.rotateAngleX = 0.03054326F;
        electrode5.rotateAngleY = 0.1745329F;
        electrode5.rotateAngleZ = 0.2392846F;
        electrode5.renderWithRotation(par7);

        electrode6.rotateAngleX = 0.03054326F;
        electrode6.rotateAngleY = 0.1745329F;
        electrode6.rotateAngleZ = 0.2392846F;
        electrode6.renderWithRotation(par7);

        electrode7.rotateAngleX = 0.03054326F;
        electrode7.rotateAngleY = 0.1745329F;
        electrode7.rotateAngleZ = 0.2392846F;
        electrode7.renderWithRotation(par7);

        electrode8.rotateAngleX = 0.03054326F;
        electrode8.rotateAngleY = 0.1745329F;
        electrode8.rotateAngleZ = 0.2392846F;
        electrode8.renderWithRotation(par7);

        electrode9.rotateAngleX = 0.03054326F;
        electrode9.rotateAngleY = 0.1745329F;
        electrode9.rotateAngleZ = 0.2392846F;
        electrode9.renderWithRotation(par7);

        electrode10.rotateAngleX = 0.03054326F;
        electrode10.rotateAngleY = 0.1745329F;
        electrode10.rotateAngleZ = 0.2392846F;
        electrode10.renderWithRotation(par7);

        electrode11.rotateAngleX = 0.03054326F;
        electrode11.rotateAngleY = 0.1745329F;
        electrode11.rotateAngleZ = 0.2392846F;
        electrode11.renderWithRotation(par7);

        electrode12.rotateAngleX = 0.03054326F;
        electrode12.rotateAngleY = 0.1745329F;
        electrode12.rotateAngleZ = 0.2392846F;
        electrode12.renderWithRotation(par7);

        glass1.rotateAngleX = 0F;
        glass1.rotateAngleY = 0F;
        glass1.rotateAngleZ = 0F;
        glass1.renderWithRotation(par7);

        glass2.rotateAngleX = 0F;
        glass2.rotateAngleY = -1.570796F;
        glass2.rotateAngleZ = 0F;
        glass2.renderWithRotation(par7);

        glass3.rotateAngleX = 0F;
        glass3.rotateAngleY = 0F;
        glass3.rotateAngleZ = 0F;
        glass3.renderWithRotation(par7);

        glass4.rotateAngleX = 0F;
        glass4.rotateAngleY = -1.570796F;
        glass4.rotateAngleZ = 0F;
        glass4.renderWithRotation(par7);

        top.rotateAngleX = 0F;
        top.rotateAngleY = 0F;
        top.rotateAngleZ = 0F;
        top.renderWithRotation(par7);

        Lid1.rotateAngleX = 0F;
        Lid1.rotateAngleY = 0F;
        Lid1.rotateAngleZ = 0F;
        Lid1.renderWithRotation(par7);

        bottom.rotateAngleX = 0F;
        bottom.rotateAngleY = 0F;
        bottom.rotateAngleZ = 0F;
        bottom.renderWithRotation(par7);

        Lid2.rotateAngleX = 0F;
        Lid2.rotateAngleY = 0F;
        Lid2.rotateAngleZ = 0F;
        Lid2.renderWithRotation(par7);

        Lid3.rotateAngleX = 0F;
        Lid3.rotateAngleY = 0F;
        Lid3.rotateAngleZ = 0F;
        Lid3.renderWithRotation(par7);

        Lid4.rotateAngleX = 0F;
        Lid4.rotateAngleY = 0F;
        Lid4.rotateAngleZ = 0F;
        Lid4.renderWithRotation(par7);

        Lid5.rotateAngleX = 0F;
        Lid5.rotateAngleY = 0F;
        Lid5.rotateAngleZ = 0F;
        Lid5.renderWithRotation(par7);
    }
}
