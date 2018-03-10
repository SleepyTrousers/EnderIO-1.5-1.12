package crazypants.enderio.conduits.conduit.item;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemEntry {

  Item getItem();

  int getDbID();

  int getHash();

  int getItemID();

  int getMeta();
  
  NBTTagCompound getNbt();

  boolean equals(int itemID, int meta, NBTTagCompound nbt);
}