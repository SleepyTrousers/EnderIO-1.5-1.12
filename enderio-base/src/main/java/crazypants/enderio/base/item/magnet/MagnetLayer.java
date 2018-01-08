package crazypants.enderio.base.item.magnet;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IRenderUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagnetLayer implements IRenderUpgrade {

  public static final @Nonnull MagnetLayer instance = new MagnetLayer();

  private MagnetLayer() {
  }

  // see LayerCustomHead

  @Override
  public void doRenderLayer(@Nonnull RenderPlayer renderPlayer, @Nonnull ItemStack piece, @Nonnull AbstractClientPlayer entitylivingbaseIn, float p_177141_2_,
      float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    GlStateManager.pushMatrix();

    if (entitylivingbaseIn.isSneaking()) {
      GlStateManager.translate(0.0F, 0.2F, 0.0F);
    }

    renderPlayer.getMainModel().bipedHead.postRender(0.0625F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(0.75f, 0.9f, 2);
    GlStateManager.translate(0.0F, 2.7f * 0.0625F, .49 * 0.0625F);

    Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, piece, TransformType.NONE);

    GlStateManager.popMatrix();
  }

}