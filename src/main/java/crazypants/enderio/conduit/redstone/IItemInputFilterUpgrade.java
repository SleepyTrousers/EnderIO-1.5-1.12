package crazypants.enderio.conduit.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.conduit.redstone.filters.IInputSignalFilter;
import net.minecraft.item.ItemStack;

public interface IItemInputFilterUpgrade {

  @Nullable
  IInputSignalFilter createInputSignalFilterFromStack(@Nonnull ItemStack stack);

}
