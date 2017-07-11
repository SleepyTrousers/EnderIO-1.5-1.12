package crazypants.enderio.filter;

import net.minecraft.item.ItemStack;

public interface IItemFilterUpgrade {

  IItemFilter createFilterFromStack(ItemStack stack);

}
