package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IItemInputFilterUpgrade {

  @Nullable
  IInputSignalFilter createInputSignalFilterFromStack(@Nonnull ItemStack stack);

}
