package crazypants.enderio.base.conduit.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.conduit.redstone.filters.IOutputSignalFilter;
import net.minecraft.item.ItemStack;

public interface IItemOutputFilterUpgrade {

  @Nullable
  IOutputSignalFilter createOutputSignalFilterFromStack(@Nonnull ItemStack stack);

}
