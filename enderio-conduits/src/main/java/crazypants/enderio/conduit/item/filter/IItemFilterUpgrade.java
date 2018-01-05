package crazypants.enderio.conduit.item.filter;

import net.minecraft.item.ItemStack;

public interface IItemFilterUpgrade {

  IItemFilter createFilterFromStack(ItemStack stack);

}
