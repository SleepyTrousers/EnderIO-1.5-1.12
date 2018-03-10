package crazypants.enderio.conduits.conduit.item;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEntryBase implements IItemEntry {

  private final int dbID;
  private final int hash;
  private final int itemID;
  private final int meta;
  private final NBTTagCompound nbt;

  protected ItemEntryBase(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
    this.dbID = dbID;
    this.hash = hash;
    this.itemID = itemID;
    this.meta = meta;
    this.nbt = nbt;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof ItemEntryBase) {
      ItemEntryBase other = (ItemEntryBase) obj;
      return this.dbID == other.dbID;
    }
    return false;
  }

  @Override
  public boolean equals(int itemID, int meta, NBTTagCompound nbt) {
    return this.itemID == itemID && this.meta == meta && ((this.nbt == nbt) || (this.nbt != null && this.nbt.equals(nbt)));
  }

  @Override
  public Item getItem() {
    return Item.getItemById(itemID);
  }
  
  @Override
  public int getDbID() {
    return dbID;
  }

  @Override
  public int getHash() {
    return hash;
  }

  @Override
  public int getItemID() {
    return itemID;
  }

  @Override
  public int getMeta() {
    return meta;
  }

  @Override
  public NBTTagCompound getNbt() {
    return nbt;
  }

  @Override
  public String toString() {
    return "ItemEntryBase{" + "dbID=" + dbID + ", hash=" + hash + ", itemID=" + itemID + ", meta=" + meta + ", nbt=" + nbt + '}';
  }
}
