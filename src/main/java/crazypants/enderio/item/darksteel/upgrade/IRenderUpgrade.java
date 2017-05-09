package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IRenderUpgrade {

  /**
   * A few helper methods for rendering. Credit to Vazkii, used from Botania.
   * {@link #translateToHeadLevel(EntityPlayer)} edited to remove sneaking
   * translation.
   */
  public static class Helper {

    public static void rotateIfSneaking(EntityPlayer player) {
      if (player.isSneaking())
        applySneakingRotation();
    }

    public static void applySneakingRotation() {
      GlStateManager.rotate(28.64789F, 1.0F, 0.0F, 0.0F);
    }

    public static void translateToHeadLevel(EntityPlayer player) {
      GlStateManager.translate(0, (player != Minecraft.getMinecraft().player ? 1.7F : 0) - player.getDefaultEyeHeight(), 0);
    }
  }

  void doRenderLayer(RenderPlayer renderPlayer, ItemStack piece, AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
      float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale);
}
