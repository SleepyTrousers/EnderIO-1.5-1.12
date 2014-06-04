package crazypants.enderio.conduit.item;

import net.minecraft.item.ItemStack;

public interface IItemFilterUpgrade {

  IItemFilter createFilterFromStack(ItemStack stack);

}
