package crazypants.enderio.base.item.darksteel.upgrade.elytra;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.EnderIO;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ElytraUpgradeLayer implements IRenderUpgrade {

  private static final @Nonnull ResourceLocation TEXTURE_ELYTRA = new ResourceLocation(EnderIO.DOMAIN, "textures/models/armor/elytra.png");
  private final @Nonnull ModelElytra modelElytra = new ModelElytra();

  public static final @Nonnull ElytraUpgradeLayer instance = new ElytraUpgradeLayer();

  private ElytraUpgradeLayer() {
  }

  @Override
  public void doRenderLayer(@Nonnull RenderPlayer renderPlayer, EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack piece,
      @Nonnull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
      float headPitch, float scale) {
    if (equipmentSlot != EntityEquipmentSlot.CHEST) {
      return;
    }

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    final ResourceLocation locationElytra = entitylivingbaseIn.getLocationElytra();
    if (entitylivingbaseIn.isPlayerInfoSet() && locationElytra != null) {
      renderPlayer.bindTexture(locationElytra);
    } else {
      final ResourceLocation locationCape = entitylivingbaseIn.getLocationCape();
      if (entitylivingbaseIn.hasPlayerInfo() && locationCape != null && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) {
        renderPlayer.bindTexture(locationCape);
      } else {
        renderPlayer.bindTexture(TEXTURE_ELYTRA);
      }
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, 0.0F, 0.125F);
    modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
    modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

    if (piece.isItemEnchanted()) {
      LayerArmorBase.renderEnchantedGlint(renderPlayer, entitylivingbaseIn, modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
          headPitch, scale);
    }

    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }

}
