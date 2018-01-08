package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;

/**
 * See {@link IHasPlayerRenderer}.
 * 
 * @author Henry Loenwind
 *
 */
public interface IRenderUpgrade {

  void doRenderLayer(@Nonnull RenderPlayer renderPlayer, @Nonnull ItemStack piece, @Nonnull AbstractClientPlayer entitylivingbaseIn, float p_177141_2_,
      float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale);
}
