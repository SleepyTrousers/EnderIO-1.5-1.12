package crazypants.enderio.item.darksteel.upgrade.glider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.handler.darksteel.DarkSteelController;
import crazypants.enderio.handler.darksteel.IRenderUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;

@SideOnly(Side.CLIENT)
public class GliderUpgradeLayer implements IRenderUpgrade {

  public static final GliderUpgradeLayer instance = new GliderUpgradeLayer();

  private GliderUpgradeLayer() {
  }

  // This is basically the CapeLayer with minimal (marked) changes to make future updating easier

  @Override
  public void doRenderLayer(RenderPlayer renderPlayer, ItemStack piece, AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
      float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
    if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && DarkSteelController.instance.isGlideActive(entitylivingbaseIn)) { // changed
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      // removed: this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, -0.15F, 0.375F); // changed
      double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks
          - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
      double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks
          - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
      double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks
          - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
      float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
      double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
      double d4 = (-MathHelper.cos(f * (float) Math.PI / 180.0F));
      float f1 = (float) d1 * 10.0F;
      f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
      float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
      float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

      if (f2 < 0.0F) {
        f2 = 0.0F;
      }

      float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
      f1 = f1
          + MathHelper
              .sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified)
                  * partialTicks) * 6.0F) * 32.0F * f4 * .1f; // changed

      if (entitylivingbaseIn.isSneaking()) {
        f1 += 25.0F;
      }

      GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

      // removed: this.playerRenderer.getMainModel().renderCape(0.0625F);

      // added start
      GlStateManager.translate(0.0F, 0.55F + 0.15F, 0.1F);
      GlStateManager.scale(3, 3, 0.5);

      ItemStack glider = new ItemStack(ModObject.itemGliderWing, 1, 1);

      final net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType none = net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.NONE;
      Minecraft.getMinecraft().getRenderItem().renderItem(glider, none);
      // added end

      GlStateManager.popMatrix();
    }
  }

}