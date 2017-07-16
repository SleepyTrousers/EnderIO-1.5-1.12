package crazypants.enderio.filter;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IItemFilterUpgrade {

  IItemFilter createFilterFromStack(@Nonnull ItemStack stack);

}
