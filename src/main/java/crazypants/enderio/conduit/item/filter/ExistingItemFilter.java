package crazypants.enderio.conduit.item.filter;

import java.util.Collections;
import java.util.List;

import crazypants.enderio.conduit.item.NetworkedInventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ExistingItemFilter implements IItemFilter {

  boolean matchMeta = true;
  boolean useOreDict = false;
  boolean convertOreDict = false;
  boolean matchNBT = false;
  boolean sticky = false;
  
  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {  
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);
  }

  @Override
  public boolean doesItemPassFilter(NetworkedInventory ni, ItemStack item) {
    if(item == null) {
      return false;
    }        
    int[] slots = ni.getInventory().getAccessibleSlotsFromSide(ni.getInventorySide());
    for(int i=0;i<slots.length;i++) {
      ItemStack stack = ni.getInventory().getStackInSlot(i);
      if(stackEqual(item, stack)) {
        return true;
      }
    }    
    return false;
  }

  boolean stackEqual(ItemStack toInsert, ItemStack existing) {
    if(toInsert == null || existing == null) {
      return false;
    }    
    
    boolean matched = false;
    if(useOreDict) {
      int existingId = OreDictionary.getOreID(existing);
      matched = existingId != -1 && existingId == OreDictionary.getOreID(toInsert); 
    }     
    if(!matched) {      
      matched = Item.getIdFromItem(toInsert.getItem()) == Item.getIdFromItem(existing.getItem());    
      if(matched && matchMeta) {
        matched = toInsert.getItemDamage() == existing.getItemDamage();        
      }
      if(matched && matchNBT) {        
        matched = ItemStack.areItemStackTagsEqual(toInsert, existing);
      }
    }    
    return matched;
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    return sticky && doesItemPassFilter(inv, item);
  }

  @Override
  public boolean isValid() {
    return true;
  }
  
  @Override
  public List<Slot> getSlots() {
    return Collections.emptyList();
  }

  @Override
  public int getSlotCount() {
    return 0;
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

}
