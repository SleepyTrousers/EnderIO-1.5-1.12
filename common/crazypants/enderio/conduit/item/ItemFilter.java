package crazypants.enderio.conduit.item;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemFilter implements IInventory {

  private static final boolean DEFAULT_BLACKLIST = false;

  private static final boolean DEFAULT_META = true;

  private static final boolean DEFAULT_MBT = true;

  private static final boolean DEFAULT_ORE_DICT = false;

  private static final boolean DEFAULT_STICKY = false;

  boolean isBlacklist = DEFAULT_BLACKLIST;
  boolean matchMeta = true;
  boolean matchNBT = true;
  boolean useOreDict = false;
  boolean sticky = false;

  ItemStack[] items;

  int[] oreIds;

  public ItemFilter() {
    this(10);
  }

  private ItemFilter(int numItems) {
    items = new ItemStack[numItems];
    oreIds = new int[numItems];
    Arrays.fill(oreIds, -99);
  }

  public boolean doesFilterCaptureStack(ItemStack item) {
    return isSticky() && itemMatched(item);
  }

  public boolean doesItemPassFilter(ItemStack item) {
    if(!isValid()) {
      return true;
    }
    boolean matched = itemMatched(item);
    return isBlacklist ? !matched : matched;
  }

  private boolean itemMatched(ItemStack item) {
    if(item == null) {
      return false;
    }

    int oreId = OreDictionary.getOreID(item);
    boolean matched = false;
    int i = 0;
    for (ItemStack it : items) {
      if(useOreDict && oreId > 0) {
        if(getOreIdForStack(i) == oreId) {
          matched = true;
        }
      }
      if(!matched && it != null && item.itemID == it.itemID) {
        matched = true;
        if(matchMeta && item.getItemDamage() != it.getItemDamage()) {
          matched = false;
        } else if(matchNBT && !ItemStack.areItemStackTagsEqual(item, it)) {
          matched = false;
        }
      }
      if(matched) {
        break;
      }
      i++;
    }
    return matched;
  }

  private int getOreIdForStack(int i) {
    ItemStack item = items[i];
    if(item == null) {
      return -1;
    }
    int res = oreIds[i];
    if(res == -99) {
      res = OreDictionary.getOreID(item);
      oreIds[i] = res;
    }
    return res;
  }

  public boolean isValid() {
    for (ItemStack item : items) {
      if(item != null) {
        return true;
      }
    }
    return false;
  }

  public boolean isBlacklist() {
    return isBlacklist;
  }

  public void setBlacklist(boolean isBlacklist) {
    this.isBlacklist = isBlacklist;
  }

  public boolean isMatchMeta() {
    return matchMeta;
  }

  public void setMatchMeta(boolean matchMeta) {
    this.matchMeta = matchMeta;
  }

  public boolean isMatchNBT() {
    return matchNBT;
  }

  public void setMatchNBT(boolean matchNbt) {
    this.matchNBT = matchNbt;
  }

  public boolean isUseOreDict() {
    return useOreDict;
  }

  public void setUseOreDict(boolean useOreDict) {
    this.useOreDict = useOreDict;
  }

  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("isBlacklist", isBlacklist);
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);

    int i = 0;
    for (ItemStack item : items) {
      NBTTagCompound itemTag = new NBTTagCompound();
      if(item != null) {
        item.writeToNBT(itemTag);
        nbtRoot.setTag("item" + i, itemTag);
      }
      i++;
    }

  }

  public void readFromNBT(NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");

    int numItems = 10;
    items = new ItemStack[numItems];
    oreIds = new int[numItems];
    for (int i = 0; i < numItems; i++) {
      oreIds[i] = -99;
      NBTBase tag = nbtRoot.getTag("item" + i);
      if(tag instanceof NBTTagCompound) {
        items[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag);
      } else {
        items[i] = null;
      }
    }
  }

  @Override
  public int getSizeInventory() {
    return items.length;
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    if(i < 0 || i >= items.length) {
      return null;
    }
    return items[i];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    oreIds[fromSlot] = -99;
    ItemStack item = items[fromSlot];
    items[fromSlot] = null;
    if(item == null) {
      return null;
    }
    item.stackSize = 0;
    return item;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int i, ItemStack itemstack) {
    if(itemstack != null) {
      items[i] = itemstack.copy();
      items[i].stackSize = 0;
    } else {
      items[i] = null;
    }
    oreIds[i] = -99;
  }

  @Override
  public String getInvName() {
    return "Item Filter";
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 0;
  }

  @Override
  public void onInventoryChanged() {
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  public boolean isDefault() {
    return !isValid() && isBlacklist == DEFAULT_BLACKLIST &&
        matchMeta == DEFAULT_META &&
        matchNBT == DEFAULT_MBT &&
        useOreDict == DEFAULT_ORE_DICT &&
        sticky == DEFAULT_STICKY;
  }
}
