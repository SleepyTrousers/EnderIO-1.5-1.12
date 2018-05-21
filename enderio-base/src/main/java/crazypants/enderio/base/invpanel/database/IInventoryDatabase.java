package crazypants.enderio.base.invpanel.database;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IInventoryDatabase<ItemEntry extends IItemEntry> {

  int getGeneration();

  ItemEntry lookupItem(@Nonnull ItemStack stack, ItemEntry hint, boolean create);

  ItemEntry getItem(int dbID);

  ItemEntry getExistingItem(int dbID);

}