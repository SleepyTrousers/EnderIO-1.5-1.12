package crazypants.enderio.base.filter.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.base.filter.gui.IItemFilterContainer;
import crazypants.enderio.base.filter.gui.IItemFilterGui;
import crazypants.enderio.base.filter.gui.SpeciesItemFilterGui;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.util.Prep;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

// TODO: Move to integration-forestry after big conduit merge

public class SpeciesItemFilter implements IInventory, IItemFilter {

  private static final boolean DEFAULT_BLACKLIST = false;
  private static final boolean DEFAULT_STICKY = false;
  private static final SpeciesMode DEFAULT_SPECIES_MODE = SpeciesMode.BOTH;

  boolean isBlacklist = DEFAULT_BLACKLIST;
  boolean sticky = DEFAULT_STICKY;
  SpeciesMode speciesMode = DEFAULT_SPECIES_MODE;

  private NNList<ItemStack> items;
  private String[] primarySpeciesUids;
  private String[] secondarySpeciesUids;

  public void copyFrom(SpeciesItemFilter o) {
    isBlacklist = o.isBlacklist;
    sticky = o.sticky;
    speciesMode = o.speciesMode;
    items = o.items;
    primarySpeciesUids = o.primarySpeciesUids;
    secondarySpeciesUids = o.secondarySpeciesUids;
  }

  public SpeciesItemFilter() {
    this(10);
  }

  private SpeciesItemFilter(int numItems) {
    items = new NNList<>(numItems, ItemStack.EMPTY);
    primarySpeciesUids = new String[numItems];
    secondarySpeciesUids = new String[numItems];
  }

  @Override
  public boolean doesItemPassFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item) {
    return doesItemPassFilter(item);
  }

  public boolean doesItemPassFilter(@Nonnull ItemStack item) {
    if (!isValid()) {
      return true;
    }
    boolean matched = itemMatched(item);
    return isBlacklist ? !matched : matched;
  }

  private boolean itemMatched(@Nonnull ItemStack item) {
    if (Prep.isInvalid(item)) {
      return false;
    }

    ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(item);
    if (speciesRoot == null) {
      return false;
    }

    IIndividual member = speciesRoot.getMember(item);
    if (member == null) {
      return false;
    }

    IGenome genome = member.getGenome();
    String primarySpeciesUid = genome.getPrimary().getUID();
    String secondarySpeciesUid = genome.getSecondary().getUID();

    for (int slot = 0; slot < items.size(); slot++) {
      ItemStack slotItem = items.get(slot);
      if (slotItem.getItem() == item.getItem()) {
        switch (speciesMode) {
        case BOTH:
          if (primarySpeciesUids[slot].equals(primarySpeciesUid) && secondarySpeciesUids[slot].equals(secondarySpeciesUid)) {
            return true;
          }
          break;
        case PRIMARY:
          if (primarySpeciesUids[slot].equals(primarySpeciesUid)) {
            return true;
          }
          break;
        case SECONDARY:
          if (secondarySpeciesUids[slot].equals(secondarySpeciesUid)) {
            return true;
          }
          break;
        }
      }
    }

    return false;
  }

  @Override
  public boolean isValid() {
    for (ItemStack item : items) {
      if (item != null && AlleleManager.alleleRegistry.getSpeciesRoot(item) != null) {
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

  @Override
  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  public SpeciesMode getSpeciesMode() {
    return speciesMode;
  }

  public void setSpeciesMode(SpeciesMode mode) {
    this.speciesMode = mode;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    nbtRoot.setBoolean("isBlacklist", isBlacklist);
    nbtRoot.setBoolean("sticky", sticky);
    nbtRoot.setByte("speciesMode", (byte) speciesMode.ordinal());

    int i = 0;
    for (ItemStack item : items) {
      if (item != null) {
        nbtRoot.setTag("item" + i, item.serializeNBT());
      }
      i++;
    }

  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
  // ItemConduitFilterContainer cont = new ItemConduitFilterContainer(itemConduit, gui.getDir(), isInput);
  // SpeciesItemFilterGui itemFilterGui = new SpeciesItemFilterGui(gui, cont, !isInput);
  // itemFilterGui.createFilterSlots();
  // return itemFilterGui;
  // }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    sticky = nbtRoot.getBoolean("sticky");
    speciesMode = SpeciesMode.values()[nbtRoot.getByte("speciesMode") & 255];

    int numItems = 10;
    items = new NNList<>(numItems, ItemStack.EMPTY);
    primarySpeciesUids = new String[numItems];
    secondarySpeciesUids = new String[numItems];
    for (int i = 0; i < numItems; i++) {
      NBTBase tag = nbtRoot.getTag("item" + i);
      ItemStack itemStack = ItemStack.EMPTY;
      if (tag instanceof NBTTagCompound) {
        itemStack = new ItemStack((NBTTagCompound) tag);
      }
      setItem(i, itemStack);
    }
  }

  @Nonnull
  private ItemStack setItem(int slot, @Nonnull ItemStack itemStack) {
    if (slot < 0 || slot >= items.size()) {
      return ItemStack.EMPTY;
    }

    ItemStack prevStack = items.get(slot);
    if (!prevStack.isEmpty()) {
      this.primarySpeciesUids[slot] = null;
      this.secondarySpeciesUids[slot] = null;
    }

    items.set(slot, itemStack);

    ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
    if (speciesRoot != null) {
      IIndividual member = speciesRoot.getMember(itemStack);
      if (member != null) {
        IGenome genome = member.getGenome();
        primarySpeciesUids[slot] = genome.getPrimary().getUID();
        secondarySpeciesUids[slot] = genome.getSecondary().getUID();
      }
    }

    return prevStack;
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

  @Override
  public int getSizeInventory() {
    return items.size();
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int i) {
    if (i < 0 || i >= items.size()) {
      return ItemStack.EMPTY;
    }
    return items.get(i);
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int fromSlot, int amount) {
    setItem(fromSlot, ItemStack.EMPTY);
    return ItemStack.EMPTY;
  }

  @Override
  public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
    ItemStack copy = itemstack.copy();
    copy.setCount(0);
    setItem(i, copy);
  }

  @Override
  public @Nonnull ItemStack removeStackFromSlot(int index) {
    return setItem(index, ItemStack.EMPTY);
  }

  @Override
  public void clear() {
    for (int i = 0; i < items.size(); i++) {
      setItem(i, ItemStack.EMPTY);
    }
  }

  @Override
  @Nonnull
  public String getName() {
    return "Species Item Filter";
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
  public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  public void openInventory(@Nonnull EntityPlayer e) {
  }

  @Override
  public void closeInventory(@Nonnull EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return true;
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, final int xOffset, final int yOffset, @Nullable Runnable cb) {
    int index = 0;
    int numRows = 2;
    for (int row = 0; row < numRows; ++row) {
      for (int col = 0; col < 5; ++col) {
        int x = xOffset + col * 18;
        int y = yOffset + row * 20;
        slots.add(new ItemFilterGhostSlot(index, x, y, cb));
        index++;
      }
    }
  }

  @Override
  public int getSlotCount() {
    return getSizeInventory();
  }

  public boolean isDefault() {
    return !isValid() && isBlacklist == DEFAULT_BLACKLIST && speciesMode == DEFAULT_SPECIES_MODE && sticky == DEFAULT_STICKY;
  }

  @Override
  public String toString() {
    return "SpeciesItemFilter [speciesMode=" + speciesMode + ", items=" + items + "]";
  }

  class ItemFilterGhostSlot extends GhostSlot {
    private final Runnable cb;

    ItemFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.setX(x);
      this.setY(y);
      this.setSlot(slot);
      this.cb = cb;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      if (AlleleManager.alleleRegistry.getSpeciesRoot(stack) != null) {
        stack = stack.copy();
        stack.setCount(1);
        setItem(getSlot(), stack);
      } else {
        setItem(getSlot(), ItemStack.EMPTY);
      }

      cb.run();
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return getStackInSlot(getSlot());
    }
  }

  @Override
  @Nonnull
  public ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
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

  @Override
  public boolean isEmpty() {
    return items.isEmpty() || items.stream().allMatch(ItemStack::isEmpty);
  }

  @Override
  public IItemFilterGui getGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    return new SpeciesItemFilterGui(gui, filterContainer, isStickyModeAvailable);
  }
}
