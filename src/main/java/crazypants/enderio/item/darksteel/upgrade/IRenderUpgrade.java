package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

public interface IRenderUpgrade {

  void render(RenderPlayerEvent event, ItemStack stack, boolean head);

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
      GlStateManager.translate(0, (player != Minecraft.getMinecraft().thePlayer ? 1.7F : 0) - player.getDefaultEyeHeight(), 0);
    }
  }
}
