package crazypants.enderio.base.item.darksteel.upgrade.elytra;

import javax.annotation.Nonnull;

import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class CapeFilterLayer extends LayerCape {

  private final LayerRenderer<AbstractClientPlayer> parent;

  public CapeFilterLayer(LayerRenderer<AbstractClientPlayer> parent) {
    super(null);
    this.parent = parent;
  }

  @Override
  public void doRenderLayer(@Nonnull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
      float netHeadYaw, float headPitch, float scale) {

    if (!DarkSteelController.isElytraUpgradeEquipped(entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST))) {
      parent.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }
  }

  @Override
  public boolean shouldCombineTextures() {
    return parent.shouldCombineTextures();
  }

}