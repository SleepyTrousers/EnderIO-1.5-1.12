package crazypants.enderio.conduits.conduit.item;

import net.minecraft.item.ItemStack;

public interface IInventoryDatabase<ItemEntry extends IItemEntry> {

  int getGeneration();

  ItemEntry lookupItem(ItemStack stack, ItemEntry hint, boolean create);

  ItemEntry getItem(int dbID);

  ItemEntry getExistingItem(int dbID);

}