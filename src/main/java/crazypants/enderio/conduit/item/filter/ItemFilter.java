package crazypants.enderio.conduit.item.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.BasicItemFilterGui;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.gui.item.ItemConduitFilterContainer;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.util.Prep;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
  DamageMode damageMode = DamageMode.DISABLED;

  ItemStack[] items;

  final List<int[]> oreIds;

  private boolean isAdvanced, isLimited;

  public void copyFrom(ItemFilter o) {
    isBlacklist = o.isBlacklist;
    matchMeta = o.matchMeta;
    matchNBT = o.matchNBT;
    useOreDict = o.useOreDict;
    sticky = o.sticky;
    damageMode = o.damageMode;
    items = o.items;
    oreIds.clear();
    oreIds.addAll(o.oreIds);
    isAdvanced = o.isAdvanced;
    isLimited = o.isLimited;
  }

  public ItemFilter() {
    this(5, false);
  }

  public ItemFilter(boolean advanced) {
    this(advanced ? 10 : 5, advanced);
  }

  private ItemFilter(int numItems, boolean isAdvanced) {
    this.isAdvanced = isAdvanced;
    items = new ItemStack[numItems];
    oreIds = new ArrayList<int[]>(numItems);
    for (int i = 0; i < numItems; i++) {
      oreIds.add(null);
    }
    isLimited = false;
  }

  public ItemFilter(int damage) {
    this(damage > 0);
    isLimited = damage > 1;
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    return isSticky() && itemMatched(item) != 0;
  }

  @Override
  public boolean doesItemPassFilter(@Nullable NetworkedInventory inv, ItemStack item) {
    return !isValid() || (isBlacklist != (itemMatched(item) != 0));
  }

  public int getMaxCountThatPassesFilter(@Nullable NetworkedInventory inv, ItemStack item) {
    if (isLimited) {
      if (!isValid()) {
        return 0;
      }
      int value = itemMatched(item);
      // Note: No blacklist for limited filters
      if (value <= 0) {
        return 0;
      }
      return value;
    } else {
      return doesItemPassFilter(inv, item) ? Integer.MAX_VALUE : 0;
    }
  }

  /**
   * Checks if the given item passes the filter.
   * 
   * @param item
   *          The item to check against the filter
   * @return 0 if the item does not pass. -1 if it passes but no single rule could be identified that lets it pass. Otherwise the size limit for the given item.
   */
  private int itemMatched(ItemStack item) {
    if (damageMode.passesFilter(item)) {
      // if there are no filter items, but a damage mode is set, the filter will let items pass that match that filter mode
      boolean canPassFilter = damageMode != DamageMode.DISABLED;
      for (int i = 0; i < items.length; i++) {
        ItemStack filterStack = items[i];
        if (Prep.isValid(filterStack)) {
          if (item.getItem() == filterStack.getItem()) {
            if (!matchMeta || !item.getHasSubtypes() || item.getMetadata() == filterStack.getMetadata()) {
              if (!matchNBT || isNBTMatch(item, filterStack)) {
                return filterStack.stackSize;
              }
            }
          }
          if (useOreDict && isOreDicMatch(i, item)) {
            return filterStack.stackSize;
          }
          canPassFilter = false;
        }
      }
      return canPassFilter ? -1 : 0;
    }

    return 0;
  }

  private boolean isOreDicMatch(int filterItemIndex, ItemStack item) {
    int[] ids1 = getCachedIds(filterItemIndex);
    if (ids1 == null || ids1.length == 0) {
      return false;
    }
    int[] ids2 = OreDictionary.getOreIDs(item);
    if (ids2 == null || ids2.length == 0) {
      return false;
    }
    for (int id1 : ids1) {
      for (int id2 : ids2) {
        if (id1 == id2) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isNBTMatch(ItemStack filter, ItemStack item) {
    return getTag(filter).equals(getTag(item));
  }

  private static final @Nonnull NBTTagCompound EMPTY_NBT = new NBTTagCompound();

  private @Nonnull NBTTagCompound getTag(ItemStack item) {
    if (Prep.isInvalid(item) || !item.hasTagCompound()) {
      return EMPTY_NBT;
    }
    NBTTagCompound nbt = item.getTagCompound();
    if (nbt == null || nbt.hasNoTags()) {
      return EMPTY_NBT;
    }
    if (nbt.hasKey("GEN")) {
      // ignore Forestry bee generation (age of bee)
      (nbt = nbt.copy()).removeTag("GEN");
    }
    return nbt;
  }

  private int[] getCachedIds(int filterItemIndex) {
    int[] res = oreIds.get(filterItemIndex);
    if (res == null) {
      ItemStack item = items[filterItemIndex];
      if (item == null) {
        res = new int[0];
      } else {
        res = OreDictionary.getOreIDs(item);
        if (res == null) {
          res = new int[0];
        }
      }
      oreIds.set(filterItemIndex, res);
    }
    return res;
  }

  @Override
  public boolean isValid() {
    if (damageMode != DamageMode.DISABLED) {
      return true;
    }
    for (ItemStack item : items) {
      if (item != null) {
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

  public DamageMode getDamageMode() {
    return damageMode;
  }

  public void setDamageMode(DamageMode damageMode) {
    this.damageMode = damageMode;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("isBlacklist", isBlacklist);
    nbtRoot.setBoolean("matchMeta", matchMeta);
    nbtRoot.setBoolean("matchNBT", matchNBT);
    nbtRoot.setBoolean("useOreDict", useOreDict);
    nbtRoot.setBoolean("sticky", sticky);
    nbtRoot.setBoolean("isAdvanced", isAdvanced);
    if (isAdvanced) {
      nbtRoot.setBoolean("isLimited", isLimited);
    }
    nbtRoot.setByte("damageMode", (byte) damageMode.ordinal());

    int i = 0;
    for (ItemStack item : items) {
      NBTTagCompound itemTag = new NBTTagCompound();
      if (item != null) {
        item.writeToNBT(itemTag);
        nbtRoot.setTag("item" + i, itemTag);
      }
      i++;
    }

  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    ItemConduitFilterContainer cont = new ItemConduitFilterContainer(itemConduit, gui.getDir(), isInput);
    BasicItemFilterGui basicItemFilterGui = new BasicItemFilterGui(gui, cont, !isInput);
    basicItemFilterGui.createFilterSlots();
    return basicItemFilterGui;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    matchMeta = nbtRoot.getBoolean("matchMeta");
    matchNBT = nbtRoot.getBoolean("matchNBT");
    useOreDict = nbtRoot.getBoolean("useOreDict");
    sticky = nbtRoot.getBoolean("sticky");
    isAdvanced = nbtRoot.getBoolean("isAdvanced");
    if (isAdvanced) {
      if (nbtRoot.hasKey("isLimited")) {
        isLimited = nbtRoot.getBoolean("isLimited");
      } else {
        isLimited = false;
      }
    }
    if (nbtRoot.hasKey("damageMode")) {
      damageMode = DamageMode.values()[nbtRoot.getByte("damageMode") & 255];
    } else {
      damageMode = DamageMode.DISABLED;
    }

    int numItems = isAdvanced ? 10 : 5;
    items = new ItemStack[numItems];
    oreIds.clear();
    for (int i = 0; i < numItems; i++) {
      oreIds.add(null);
    }
    for (int i = 0; i < numItems; i++) {
      NBTBase tag = nbtRoot.getTag("item" + i);
      if (tag instanceof NBTTagCompound) {
        items[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag);
      } else {
        items[i] = null;
      }
    }
  }

  @Override
  public void writeToByteBuf(ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

  @Override
  public int getSizeInventory() {
    return items.length;
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    if (i < 0 || i >= items.length) {
      return null;
    }
    return items[i];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    oreIds.set(fromSlot, null);
    ItemStack item = items[fromSlot];
    items[fromSlot] = null;
    if (item == null) {
      return null;
    }
    item.stackSize = 0;
    return item;
  }

  @Override
  public void setInventorySlotContents(int i, @Nullable ItemStack itemstack) {
    if (itemstack != null) {
      items[i] = itemstack.copy();
      items[i].stackSize = 0;
    } else {
      items[i] = null;
    }
    oreIds.set(i, null);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    if (index < 0 || index >= items.length) {
      return null;
    }
    ItemStack res = items[index];
    items[index] = null;
    return res;
  }

  @Override
  public void clear() {
    for (int i = 0; i < items.length; i++) {
      items[i] = null;
    }
  }

  @Override
  public String getName() {
    return "Item Filter";
  }

  @Override
  public int getInventoryStackLimit() {
    return 0;
  }

  @Override
  public boolean hasCustomName() {
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
  public void openInventory(EntityPlayer e) {
  }

  @Override
  public void closeInventory(EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {
    int topY = yOffset;
    int leftX = xOffset;
    int index = 0;
    int numRows = isAdvanced ? 2 : 1;
    for (int row = 0; row < numRows; ++row) {
      for (int col = 0; col < 5; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 20;
        slots.add(new ItemFilterGhostSlot(index, x, y, cb));
        index++;
      }
    }
  }

  @Override
  public int getSlotCount() {
    return getSizeInventory();
  }

  public boolean isAdvanced() {
    return isAdvanced;
  }

  public boolean isLimited() {
    return isLimited;
  }

  public boolean isDefault() {
    return !isAdvanced && !isValid() && isBlacklist == DEFAULT_BLACKLIST && matchMeta == DEFAULT_META && matchNBT == DEFAULT_MBT
        && useOreDict == DEFAULT_ORE_DICT && sticky == DEFAULT_STICKY;
  }

  @Override
  public String toString() {
    // return "ItemFilter [isBlacklist=" + isBlacklist + ", matchMeta=" + matchMeta + ", matchNBT=" + matchNBT + ", useOreDict=" + useOreDict + ", sticky="
    // + sticky + ", items=" + Arrays.toString(items) + ", oreIds=" + Arrays.toString(oreIds) + ", isAdvanced=" + isAdvanced + "]";
    return "ItemFilter [isAdvanced=" + isAdvanced + ", items=" + Arrays.toString(items) + "]";
  }

  class ItemFilterGhostSlot extends GhostSlot {
    private final int slot;
    private final Runnable cb;

    ItemFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.x = x;
      this.y = y;
      this.slot = slot;
      this.cb = cb;
      this.displayStdOverlay = isLimited;
      this.stackSizeLimit = isLimited ? 64 * 3 : 1;
    }

    @Override
    public void putStack(ItemStack stack) {
      if (stack != null) {
        stack = stack.copy();
        stack.stackSize = MathHelper.clamp_int(stack.stackSize, 1, stackSizeLimit);
      }
      items[slot] = stack;
      cb.run();
    }

    @Override
    public ItemStack getStack() {
      return items[slot];
    }
  }

  @Override
  public ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

}
