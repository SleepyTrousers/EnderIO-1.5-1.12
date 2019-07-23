package crazypants.enderio.machines.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IRenderUpgrade;
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
public class SolarUpgradeLayer implements IRenderUpgrade {

  private static final @Nonnull SolarUpgradeLayer instance = new SolarUpgradeLayer();

  SolarUpgrade solarUpgrade;

  private SolarUpgradeLayer() {
  }

  // see LayerCustomHead

  @Override
  public void doRenderLayer(@Nonnull RenderPlayer renderPlayer, EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack piece,
      @Nonnull AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_,
      float p_177141_7_, float scale) {
    if (equipmentSlot != EntityEquipmentSlot.HEAD) {
      return;
    }

    if (solarUpgrade == null) {
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
    GlStateManager.translate(0.0F, -f2, 0.0F); // added
    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.scale(f2, -f2, -f2);

    Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, solarUpgrade.getRenderItem(), TransformType.HEAD);

    GlStateManager.popMatrix();
  }

  public static @Nonnull SolarUpgradeLayer withUpgrade(@Nonnull SolarUpgrade upgrade) {
    instance.solarUpgrade = upgrade;
    return instance;
  }

}