package crazypants.enderio.base.invpanel.database;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemEntry {

  Item getItem();

  int getDbID();

  int getHash();

  int getItemID();

  int getMeta();

  @Nonnull
  NBTTagCompound getNbt();

  boolean equals(int itemID, int meta, NBTTagCompound nbt);
}