package crazypants.enderio.conduit.item.filter;

import javax.annotation.Nullable;

import crazypants.enderio.conduit.item.NetworkedInventory;
import net.minecraft.item.ItemStack;

public interface ILimitedItemFilter extends IItemFilter {

  int getMaxCountThatPassesFilter(@Nullable NetworkedInventory inv, ItemStack item);

  boolean isLimited();

}