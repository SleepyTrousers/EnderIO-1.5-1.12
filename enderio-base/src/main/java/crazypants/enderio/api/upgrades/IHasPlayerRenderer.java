package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This interface allows {@link IDarkSteelUpgrade}s and items that are in {@link EntityEquipmentSlot}s To contribute to the rendering of players.
 * <p>
 * The rendering happens as part of Ender IO's {@link LayerRenderer}&lt;{@link AbstractClientPlayer}&gt;.
 * 
 * @author Henry Loenwind
 *
 */
public interface IHasPlayerRenderer {

  @Nonnull
  @SideOnly(Side.CLIENT)
  IRenderUpgrade getRender();

}