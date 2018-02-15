package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import net.minecraft.item.ItemStack;

public interface IItemFilterUpgrade extends IClearableConfiguration {

  IItemFilter createFilterFromStack(@Nonnull ItemStack stack);

}
