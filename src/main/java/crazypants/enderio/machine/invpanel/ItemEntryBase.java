package crazypants.enderio.machine.invpanel;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEntryBase {

    public final int dbID;
    public final int hash;
    public final int itemID;
    public final int meta;
    public final NBTTagCompound nbt;

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
        if (obj instanceof ItemEntryBase) {
            ItemEntryBase other = (ItemEntryBase) obj;
            return this.dbID == other.dbID;
        }
        return false;
    }

    boolean equals(int itemID, int meta, NBTTagCompound nbt) {
        return this.itemID == itemID && this.meta == meta
                && ((this.nbt == nbt) || (this.nbt != null && this.nbt.equals(nbt)));
    }

    public Item getItem() {
        return Item.getItemById(itemID);
    }

    @Override
    public String toString() {
        return "ItemEntryBase{" + "dbID="
                + dbID
                + ", hash="
                + hash
                + ", itemID="
                + itemID
                + ", meta="
                + meta
                + ", nbt="
                + nbt
                + '}';
    }
}
