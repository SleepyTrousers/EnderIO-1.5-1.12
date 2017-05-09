package crazypants.enderio.conduit.item.filter;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.ExistingItemFilterGui;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.NetworkedInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ExistingItemFilter implements IItemFilter {

  boolean matchMeta = true;
  boolean useOreDict = false;
  boolean convertOreDict = false;
  boolean matchNBT = false;
  boolean sticky = false;
  private boolean blacklist = false;

  List<ItemStack> snapshot = null;

  @Override
  public boolean doesItemPassFilter(NetworkedInventory ni, ItemStack item) {
    if (item == null) {
      return false;
    }
    if (snapshot != null) {
      return isStackInSnapshot(item) == !blacklist;
    } else if (ni != null && ni.getInventory() != null) {
      return isStackInInventory(ni, item) == !blacklist;
    }
    return false;
  }

  private boolean isStackInInventory(NetworkedInventory ni, ItemStack item) {
    IItemHandler inventory = ni.getInventory();
    if (inventory != null) {
      int numSlots = inventory.getSlots();
      for (int i = 0; i < numSlots; i++) {
        ItemStack stack = inventory.getStackInSlot(i);
        if (stackEqual(item, stack)) {
          return true;
        }
      }
    }
    return false;
  }

  boolean isStackInSnapshot(ItemStack item) {
    for (ItemStack stack : snapshot) {
      if (stackEqual(item, stack)) {
        return true;
      }
    }
    return false;
  }

  boolean stackEqual(ItemStack toInsert, ItemStack existing) {
    if (toInsert == null || existing == null) {
      return false;
    }

    boolean matched = false;
    if (useOreDict) {
      matched = isSameOreRegistered(existing, toInsert);//
      // int existingId = OreDictionary.getOreID(existing);
      // matched = existingId != -1 && existingId ==
      // OreDictionary.getOreID(toInsert);
    }
    if (!matched) {
      matched = Item.getIdFromItem(toInsert.getItem()) == Item.getIdFromItem(existing.getItem());
      if (matched && matchMeta) {
        matched = toInsert.getItemDamage() == existing.getItemDamage();
      }
      if (matched && matchNBT) {
        matched = ItemStack.areItemStackTagsEqual(toInsert, existing);
      }
    }
    return matched;
  }

  private boolean isSameOreRegistered(ItemStack existing, ItemStack toInsert) {
    int[] existingIds = OreDictionary.getOreIDs(existing);
    int[] toInsertIds = OreDictionary.getOreIDs(toInsert);
    boolean matched = false;
    for (int i = 0; i < existingIds.length && !matched; i++) {
      int existingId = existingIds[i];
      matched = existingId != -1 && contains(toInsertIds, existingId);
    }
    return matched;
  }

  private boolean contains(int[] ar, int val) {
    for (int i : ar) {
      if (i == val) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    boolean res = sticky && doesItemPassFilter(inv, item);
    return res;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

  public void setSnapshot(NetworkedInventory ni) {
    snapshot = new ArrayList<ItemStack>();
    mergeSnapshot(ni);
  }

  public void mergeSnapshot(NetworkedInventory ni) {
    if (snapshot == null) {
      snapshot = new ArrayList<ItemStack>();
    }
    IItemHandler inventory = ni.getInventory();
    if (inventory != null) {
      int numSlots = inventory.getSlots();
      for (int i = 0; i < numSlots; i++) {
        ItemStack stack = inventory.getStackInSlot(i);
        if (stack != null && !isStackInSnapshot(stack)) {
          snapshot.add(stack);
        }
      }
    }
  }

  public boolean mergeSnapshot(IInventory inventory) {
    if (snapshot == null) {
      snapshot = new ArrayList<ItemStack>();
    }
    int size = inventory.getSizeInventory();
    boolean added = false;
    for (int i = 0; i < size; i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (stack != null && !isStackInSnapshot(stack)) {
        snapshot.add(stack);
        added = true;
      }
    }
    return added;
  }

  public List<ItemStack> getSnapshot() {
    return snapshot;
  }

  public void setSnapshot(List<ItemStack> snapshot) {
    this.snapshot = snapshot;
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
    matchNBT = matchNbt;
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

  public void setBlacklist(boolean value) {
    blacklist = value;
  }

  public boolean isBlacklist() {
    return blacklist;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    return new ExistingItemFilterGui(gui, itemConduit, isInput);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    readSettingsFromNBT(nbtRoot);

    if (nbtRoot.hasKey("snapshot")) {
      snapshot = new ArrayList<ItemStack>();
      NBTTagList itemList = (NBTTagList) nbtRoot.getTag("snapshot");
      for (int i = 0; i < itemList.tagCount(); i++) {
        NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
        ItemStack itemStack = new ItemStack(itemTag);
        if (itemStack != null) {
          snapshot.add(itemStack);
        }
      }

    } else {
      snapshot = null;
    }
  }

  protected void readSettingsFromNBT(NBTTagCompound nbtRoot) {
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");
    blacklist = nbtRoot.getBoolean("blacklist");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    writeSettingToNBT(nbtRoot);

    if (snapshot != null) {

      NBTTagList itemList = new NBTTagList();
      for (ItemStack item : snapshot) {
        if (item != null) {
          NBTTagCompound itemTag = new NBTTagCompound();
          item.writeToNBT(itemTag);
          itemList.appendTag(itemTag);
        }
      }
      nbtRoot.setTag("snapshot", itemList);

    }
  }

  protected void writeSettingToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);
    nbtRoot.setBoolean("blacklist", blacklist);
  }

  @Override
  public void writeToByteBuf(ByteBuf buf) {
    NBTTagCompound settingsTag = new NBTTagCompound();
    writeSettingToNBT(settingsTag);
    NetworkUtil.writeNBTTagCompound(settingsTag, buf);
    buf.writeInt(snapshot == null ? 0 : snapshot.size());
    if (snapshot == null) {
      return;
    }
    for (ItemStack item : snapshot) {
      NBTTagCompound itemRoot = new NBTTagCompound();
      item.writeToNBT(itemRoot);
      NetworkUtil.writeNBTTagCompound(itemRoot, buf);
    }
  }

  @Override
  public void readFromByteBuf(ByteBuf buf) {
    NBTTagCompound settingsTag = NetworkUtil.readNBTTagCompound(buf);
    readSettingsFromNBT(settingsTag);
    int numItems = buf.readInt();
    if (numItems == 0) {
      snapshot = null;
      return;
    }
    snapshot = new ArrayList<ItemStack>(numItems);
    for (int i = 0; i < numItems; i++) {
      NBTTagCompound itemTag = NetworkUtil.readNBTTagCompound(buf);
      ItemStack item = new ItemStack(itemTag);
      if (item != null) {
        snapshot.add(item);
      }
    }

  }

}
