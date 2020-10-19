package crazypants.enderio.base.invpanel.database;

import crazypants.enderio.util.IMapKey;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemEntry extends IMapKey {

  Item getItem();

  int getDbID();

  int getHash();

  int getItemID();

  int getMeta();

  NBTTagCompound getNbt();

  boolean equals(int itemID, int meta, NBTTagCompound nbt);
}