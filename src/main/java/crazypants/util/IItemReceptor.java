package crazypants.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IItemReceptor {

  boolean canInsertIntoObject(Object into, ForgeDirection side);

  int doInsertItem(Object into, ItemStack item, ForgeDirection side);

}
