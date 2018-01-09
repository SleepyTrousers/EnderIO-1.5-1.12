package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface ILimitedItemFilter extends IItemFilter {

  @Override
  int getMaxCountThatPassesFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item);

  @Override
  boolean isLimited();
}
