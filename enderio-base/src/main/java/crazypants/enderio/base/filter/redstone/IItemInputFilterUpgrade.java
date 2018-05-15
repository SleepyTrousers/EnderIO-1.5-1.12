package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.IItemFilterUpgrade;
import net.minecraft.item.ItemStack;

public interface IItemInputFilterUpgrade extends IItemFilterUpgrade<IInputSignalFilter> {

  @Nullable
  IInputSignalFilter createInputSignalFilterFromStack(@Nonnull ItemStack stack);

}
