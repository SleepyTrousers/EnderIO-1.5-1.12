package crazypants.enderio.gui;

import net.minecraft.item.ItemStack;

public interface IResourceTooltipProvider {

  String getUnlocalizedNameForTooltip(ItemStack itemStack);

}
