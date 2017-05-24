package crazypants.enderio.item.darksteel.upgrade.elytra;

import crazypants.enderio.EnderIO;
import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ElytraUpgradeLayer implements IRenderUpgrade {

  private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation(EnderIO.DOMAIN, "textures/models/armor/elytra.png");
  private final ModelElytra modelElytra = new ModelElytra();

  public static final ElytraUpgradeLayer instance = new ElytraUpgradeLayer();

  private ElytraUpgradeLayer() {
  }

  @Override
  public void doRenderLayer(RenderPlayer renderPlayer, ItemStack piece, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
      float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableBlend();

    if (entitylivingbaseIn.isPlayerInfoSet() && entitylivingbaseIn.getLocationElytra() != null) {
      renderPlayer.bindTexture(entitylivingbaseIn.getLocationElytra());
    } else if (entitylivingbaseIn.hasPlayerInfo() && entitylivingbaseIn.getLocationCape() != null && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) {
      renderPlayer.bindTexture(entitylivingbaseIn.getLocationCape());
    } else {
      renderPlayer.bindTexture(TEXTURE_ELYTRA);
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, 0.0F, 0.125F);
    modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
    modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

    if (piece.isItemEnchanted()) {
      LayerArmorBase.renderEnchantedGlint(renderPlayer, entitylivingbaseIn, modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
          headPitch, scale);
    }

    GlStateManager.popMatrix();
  }

}
