package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IItemOutputFilterUpgrade {

  @Nullable
  IOutputSignalFilter createOutputSignalFilterFromStack(@Nonnull ItemStack stack);

}
