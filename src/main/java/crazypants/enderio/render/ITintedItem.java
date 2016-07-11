package crazypants.enderio.render;

import net.minecraft.item.ItemStack;

/**
 * Un-sided alternative to {@link net.minecraft.client.renderer.color.IItemColor} to be used together with the {@link PaintTintHandler}
 *
 */
public interface ITintedItem {

  int getItemTint(ItemStack stack, int tintIndex);

}
