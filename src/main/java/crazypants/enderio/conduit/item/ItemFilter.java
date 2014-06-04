package crazypants.enderio.conduit.item;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import crazypants.gui.TemplateSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemFilter implements IInventory, IItemFilter {

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

  private boolean isAdvanced;

  public ItemFilter() {
    this(5, false);
  }
  
  public ItemFilter(boolean advanced) {
    this(advanced ? 10 : 5, advanced);
  }

  private ItemFilter(int numItems, boolean isAdvanced) {
    this.isAdvanced = isAdvanced;
    items = new ItemStack[numItems];
    oreIds = new int[numItems];
    Arrays.fill(oreIds, -99);
  }

  @Override
  public boolean doesFilterCaptureStack(ItemStack item) {
    return isSticky() && itemMatched(item);
  }

  @Override
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
      if(!matched && it != null && item.getItem() == it.getItem()) {
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

  @Override
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

  @Override
  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("isBlacklist", isBlacklist);
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);
    nbtRoot.setBoolean("isAdvanced", isAdvanced);

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

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");
    isAdvanced = nbtRoot.getBoolean("isAdvanced");

    int numItems = isAdvanced ? 10 : 5;
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
  public String getInventoryName() {
    return "Item Filter";
  }

  @Override
  public int getInventoryStackLimit() {
    return 0;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public void markDirty() {
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public List<Slot> getSlots() {
    List<Slot> result = new ArrayList<Slot>();
    
    int topY = 67;
    int leftX = 33;
    int index = 0;    
    int numRows = isAdvanced ? 2 : 1;
    for (int row = 0; row < numRows; ++row) {
      for (int col = 0; col < 5; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;        
        result.add(new TemplateSlot(this, index, x, y));        
        index++;
      }
    }    
    return result;
  }
  
  @Override
  public int getSlotCount() { 
    return getSizeInventory();
  }

  public boolean isAdvanced() {    
    return isAdvanced;
  }
  
  public boolean isDefault() {
    return !isAdvanced && !isValid() && isBlacklist == DEFAULT_BLACKLIST &&
        matchMeta == DEFAULT_META &&
        matchNBT == DEFAULT_MBT &&
        useOreDict == DEFAULT_ORE_DICT &&
        sticky == DEFAULT_STICKY;
  }

  @Override
  public String toString() {
//    return "ItemFilter [isBlacklist=" + isBlacklist + ", matchMeta=" + matchMeta + ", matchNBT=" + matchNBT + ", useOreDict=" + useOreDict + ", sticky="
//        + sticky + ", items=" + Arrays.toString(items) + ", oreIds=" + Arrays.toString(oreIds) + ", isAdvanced=" + isAdvanced + "]";
    return "ItemFilter [isAdvanced=" + isAdvanced + "]";
  }
  
  

}
