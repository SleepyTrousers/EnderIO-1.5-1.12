package crazypants.enderio.base.item.darksteel;

import crazypants.enderio.base.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PaintedHelmetLayer implements IRenderUpgrade {

  public static final PaintedHelmetLayer instance = new PaintedHelmetLayer();

  private PaintedHelmetLayer() {
  }

  // see LayerCustomHead

  @SuppressWarnings("null")
  @Override
  public void doRenderLayer(RenderPlayer renderPlayer, ItemStack piece, AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
      float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {

    ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    if (!itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey("DSPAINT")) {
      return;
    }

    ItemStack paintSource = new ItemStack(itemstack.getTagCompound().getCompoundTag("DSPAINT"));
    if (Prep.isInvalid(paintSource)) {
      return;
    }

    GlStateManager.pushMatrix();

    if (entitylivingbaseIn.isSneaking()) {
      GlStateManager.translate(0.0F, 0.2F, 0.0F);
    }

    renderPlayer.getMainModel().bipedHead.postRender(0.0625F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    float f2 = 0.625F;
    GlStateManager.translate(0.0F, -0.25F, 0.0F);
    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.scale(f2, -f2, -f2);

    Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, paintSource, TransformType.HEAD);

    GlStateManager.popMatrix();
  }

}