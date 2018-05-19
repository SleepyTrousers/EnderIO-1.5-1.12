package crazypants.enderio.zoo.entity.render;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.zoo.entity.EntityWitherWitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

//Pretty much a copy / paste from RenderWitch
public class RenderWitherWitch extends RenderLiving<EntityWitherWitch> {

  public static final Factory FACTORY = new Factory();

  private static final ResourceLocation witchTextures = new ResourceLocation("enderzoo:entity/wither_witch.png");
  private final ModelWitch witchModel;

  public RenderWitherWitch(RenderManager rm) {
    super(rm, new ModelWitch(0.0F), 0.5F);
    this.witchModel = (ModelWitch) this.mainModel;
    addLayer(new LayerHeldItemWitch(this));
  }

  public void doRender(EntityWitherWitch p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    ItemStack itemstack = p_76986_1_.getHeldItem(EnumHand.MAIN_HAND);
    this.witchModel.holdingItem = itemstack != null;
    super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  protected void func_82410_b() {
    GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
  }

  protected void preRenderCallback(EntityWitherWitch p_77041_1_, float p_77041_2_) {
    float f1 = 0.9375F;
    GL11.glScalef(f1, f1, f1);
  }

  protected ResourceLocation getEntityTexture(EntityWitherWitch p_110775_1_) {
    return witchTextures;
  }

  public static class Factory implements IRenderFactory<EntityWitherWitch> {

    @Override
    public Render<? super EntityWitherWitch> createRenderFor(RenderManager manager) {
      return new RenderWitherWitch(manager);
    }
  }

  public static class LayerHeldItemWitch implements LayerRenderer<EntityWitherWitch> {
    private final RenderWitherWitch witchRenderer;

    public LayerHeldItemWitch(RenderWitherWitch witchRendererIn) {
      this.witchRenderer = witchRendererIn;
    }

    public void doRenderLayer(EntityWitherWitch entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_,
        float p_177141_6_, float p_177141_7_, float scale) {
      ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);

      if (itemstack != null) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();

        if (this.witchRenderer.getMainModel().isChild) {
          GlStateManager.translate(0.0F, 0.625F, 0.0F);
          GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
          float f = 0.5F;
          GlStateManager.scale(f, f, f);
        }

        ((ModelWitch) this.witchRenderer.getMainModel()).villagerNose.postRender(0.0625F);
        GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
        Item item = itemstack.getItem();
        Minecraft minecraft = Minecraft.getMinecraft();
      //TODO: method is gone, so skip?
//        if (item instanceof ItemBlock && minecraft.getBlockRendererDispatcher().isEntityBlockAnimated(Block.getBlockFromItem(item))) {
//          GlStateManager.translate(0.0F, 0.0625F, -0.25F);
//          GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
//          GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
//          float f4 = 0.375F;
//          GlStateManager.scale(f4, -f4, f4);
//        } else 
          if (item == Items.BOW) {
          GlStateManager.translate(0.0F, 0.125F, -0.125F);
          GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
          float f1 = 0.625F;
          GlStateManager.scale(f1, -f1, f1);
          GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
          GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
        } else if (item.isFull3D()) {
          if (item.shouldRotateAroundWhenRendering()) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, -0.0625F, 0.0F);
          }

          this.witchRenderer.transformHeldFull3DItemLayer();
          GlStateManager.translate(0.0625F, -0.125F, 0.0F);
          float f2 = 0.625F;
          GlStateManager.scale(f2, -f2, f2);
          GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
          GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
        } else {
          GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
          float f3 = 0.875F;
          GlStateManager.scale(f3, f3, f3);
          GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
          GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
        minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        GlStateManager.popMatrix();
      }
    }

    public boolean shouldCombineTextures() {
      return false;
    }
  }

}
