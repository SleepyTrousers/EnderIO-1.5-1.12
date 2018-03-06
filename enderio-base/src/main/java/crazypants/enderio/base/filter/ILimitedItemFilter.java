package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface ILimitedItemFilter extends IItemFilter {

  @Override
  int getMaxCountThatPassesFilter(@Nullable IItemHandler inv, @Nonnull ItemStack item);

  @Override
  boolean isLimited();
}
