package crazypants.enderio.base.filter.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.base.filter.gui.ExistingItemFilterGui;
import crazypants.enderio.base.filter.gui.IItemFilterContainer;
import crazypants.enderio.base.filter.gui.IItemFilterGui;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ExistingItemFilter implements IItemFilter {

  private boolean matchMeta = true;
  private boolean useOreDict = false;
  private boolean matchNBT = false;
  private boolean sticky = false;
  private boolean blacklist = false;

  private NNList<ItemStack> snapshot = null;

  @Override
  public boolean doesItemPassFilter(@Nullable INetworkedInventory ni, @Nonnull ItemStack item) {
    if (Prep.isInvalid(item)) {
      return false;
    }
    if (snapshot != null) {
      return isStackInSnapshot(item) == !blacklist;
    } else if (ni != null && ni.getInventory() != null) {
      return isStackInInventory(ni, item) == !blacklist;
    }
    return false;
  }

  private boolean isStackInInventory(@Nonnull INetworkedInventory ni, @Nonnull ItemStack item) {
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

  boolean isStackInSnapshot(@Nonnull ItemStack item) {
    NNIterator<ItemStack> iterator = snapshot.iterator();
    while (iterator.hasNext()) {
      if (stackEqual(item, iterator.next())) {
        return true;
      }
    }
    return false;
  }

  boolean stackEqual(@Nonnull ItemStack toInsert, @Nonnull ItemStack existing) {
    if (Prep.isInvalid(toInsert) || Prep.isInvalid(existing)) {
      return false;
    }

    boolean matched = false;
    if (useOreDict) {
      matched = isSameOreRegistered(existing, toInsert);
    }
    if (!matched) {
      matched = toInsert.getItem() == existing.getItem();
      if (matched && matchMeta) {
        matched = toInsert.getItemDamage() == existing.getItemDamage();
      }
      if (matched && matchNBT) {
        matched = ItemStack.areItemStackTagsEqual(toInsert, existing);
      }
    }
    return matched;
  }

  private boolean isSameOreRegistered(@Nonnull ItemStack existing, @Nonnull ItemStack toInsert) {
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
  public boolean isValid() {
    return true;
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

  public void setSnapshot(@Nonnull INetworkedInventory ni) {
    snapshot = new NNList<ItemStack>();
    mergeSnapshot(ni);
  }

  public void mergeSnapshot(@Nonnull INetworkedInventory ni) {
    IItemHandler inventory = ni.getInventory();
    if (inventory != null) {
      mergeSnapshot(inventory);
    }
  }

  public boolean mergeSnapshot(@Nonnull IItemHandler inventory) {
    if (snapshot == null) {
      snapshot = new NNList<ItemStack>();
    }
    boolean added = false;
    int numSlots = inventory.getSlots();
    for (int i = 0; i < numSlots; i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (Prep.isValid(stack) && !isStackInSnapshot(stack)) {
        snapshot.add(stack);
        added = true;
      }
    }
    return added;
  }

  public NNList<ItemStack> getSnapshot() {
    return snapshot;
  }

  public void setSnapshot(NNList<ItemStack> snapshot) {
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

  // @Override
  // @SideOnly(Side.CLIENT)
  // public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
  // return new ExistingItemFilterGui(gui, itemConduit, isInput);
  // }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    readSettingsFromNBT(nbtRoot);

    if (nbtRoot.hasKey("snapshot")) {
      snapshot = new NNList<ItemStack>();
      NBTTagList itemList = (NBTTagList) nbtRoot.getTag("snapshot");
      for (int i = 0; i < itemList.tagCount(); i++) {
        NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
        ItemStack itemStack = new ItemStack(itemTag);
        if (Prep.isValid(itemStack)) {
          snapshot.add(itemStack);
        }
      }

    } else {
      snapshot = null;
    }
  }

  protected void readSettingsFromNBT(NBTTagCompound nbtRoot) {
    blacklist = NbtValue.FILTER_BLACKLIST.getBoolean(nbtRoot);
    matchMeta = NbtValue.FILTER_META.getBoolean(nbtRoot);
    matchNBT = NbtValue.FILTER_NBT.getBoolean(nbtRoot);
    useOreDict = NbtValue.FILTER_OREDICT.getBoolean(nbtRoot);
    sticky = NbtValue.FILTER_STICKY.getBoolean(nbtRoot);
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
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
    NbtValue.FILTER_BLACKLIST.setBoolean(nbtRoot, blacklist);
    NbtValue.FILTER_META.setBoolean(nbtRoot, matchMeta);
    NbtValue.FILTER_NBT.setBoolean(nbtRoot, matchNBT);
    NbtValue.FILTER_OREDICT.setBoolean(nbtRoot, useOreDict);
    NbtValue.FILTER_STICKY.setBoolean(nbtRoot, sticky);
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
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
  public IItemFilterGui getGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    return new ExistingItemFilterGui(gui, filterContainer, isStickyModeAvailable);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound settingsTag = NetworkUtil.readNBTTagCompound(buf);
    readSettingsFromNBT(settingsTag);
    int numItems = buf.readInt();
    if (numItems == 0) {
      snapshot = null;
      return;
    }
    snapshot = new NNList<ItemStack>();
    for (int i = 0; i < numItems; i++) {
      NBTTagCompound itemTag = NetworkUtil.readNBTTagCompound(buf);
      ItemStack item = new ItemStack(itemTag);
      if (Prep.isValid(item)) {
        snapshot.add(item);
      }
    }

  }

}
