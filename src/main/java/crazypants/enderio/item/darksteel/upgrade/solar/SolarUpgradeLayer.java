package crazypants.enderio.item.darksteel.upgrade.solar;

import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.init.ModObject.blockSolarPanel;

@SideOnly(Side.CLIENT)
public class SolarUpgradeLayer implements IRenderUpgrade {

  public static final SolarUpgradeLayer instance = new SolarUpgradeLayer();

  private SolarUpgradeLayer() {
  }

  // see LayerCustomHead

  @Override
  public void doRenderLayer(RenderPlayer renderPlayer, ItemStack piece, AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
      float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    GlStateManager.pushMatrix();

    if (entitylivingbaseIn.isSneaking()) {
      GlStateManager.translate(0.0F, 0.2F, 0.0F);
    }

    renderPlayer.getMainModel().bipedHead.postRender(0.0625F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    float f2 = 0.625F;
    GlStateManager.translate(0.0F, -0.25F, 0.0F);
    GlStateManager.translate(0.0F, -f2, 0.0F); // added
    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.scale(f2, -f2, -f2);

    Minecraft
        .getMinecraft()
        .getItemRenderer()
        .renderItem(entitylivingbaseIn, new ItemStack(blockSolarPanel.getBlock(), 1, SolarUpgrade.loadFromItem(piece).getLevel() - 1),
            net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.HEAD);

    GlStateManager.popMatrix();
  }

}