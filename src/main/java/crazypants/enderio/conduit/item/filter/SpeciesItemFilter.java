package crazypants.enderio.conduit.item.filter;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.gui.item.ItemConduitFilterContainer;
import crazypants.enderio.conduit.gui.item.SpeciesItemFilterGui;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.NetworkedInventory;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpeciesItemFilter implements IInventory, IItemFilter {

  private static final boolean DEFAULT_BLACKLIST = false;
  private static final boolean DEFAULT_STICKY = false;
  private static final SpeciesMode DEFAULT_SPECIES_MODE = SpeciesMode.BOTH;

  boolean isBlacklist = DEFAULT_BLACKLIST;
  boolean sticky = DEFAULT_STICKY;
  SpeciesMode speciesMode = DEFAULT_SPECIES_MODE;

  private ItemStack[] items;
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
    items = new ItemStack[numItems];
    primarySpeciesUids = new String[numItems];
    secondarySpeciesUids = new String[numItems];
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    return isSticky() && itemMatched(item);
  }

  @Override
  public boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item) {
    return doesItemPassFilter(item);
  }

  public boolean doesItemPassFilter(ItemStack item) {
    if (!isValid()) {
      return true;
    }
    boolean matched = itemMatched(item);
    return isBlacklist ? !matched : matched;
  }

  private boolean itemMatched(ItemStack item) {
    if (item == null) {
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

    for (int slot = 0; slot < items.length; slot++) {
      ItemStack slotItem = items[slot];
      if (slotItem != null && slotItem.getItem() == item.getItem()) {
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
  public void writeToNBT(NBTTagCompound nbtRoot) {
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

  @Override
  @SideOnly(Side.CLIENT)
  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    ItemConduitFilterContainer cont = new ItemConduitFilterContainer(itemConduit, gui.getDir(), isInput);
    SpeciesItemFilterGui itemFilterGui = new SpeciesItemFilterGui(gui, cont, !isInput);
    itemFilterGui.createFilterSlots();
    return itemFilterGui;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    isBlacklist = nbtRoot.getBoolean("isBlacklist");
    sticky = nbtRoot.getBoolean("sticky");
    speciesMode = SpeciesMode.values()[nbtRoot.getByte("speciesMode") & 255];

    int numItems = 10;
    items = new ItemStack[numItems];
    primarySpeciesUids = new String[numItems];
    secondarySpeciesUids = new String[numItems];
    for (int i = 0; i < numItems; i++) {
      NBTBase tag = nbtRoot.getTag("item" + i);
      ItemStack itemStack = null;
      if (tag instanceof NBTTagCompound) {
        itemStack = new ItemStack((NBTTagCompound) tag);
      }
      setItem(i, itemStack);
    }
  }

  @Nullable
  private ItemStack setItem(int slot, @Nullable ItemStack itemStack) {
    if (slot < 0 || slot >= items.length) {
      return null;
    }

    ItemStack prevStack = items[slot];
    if (prevStack != null) {
      this.primarySpeciesUids[slot] = null;
      this.secondarySpeciesUids[slot] = null;
    }

    items[slot] = itemStack;

    if (itemStack != null) {
      ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
      if (speciesRoot != null) {
        IIndividual member = speciesRoot.getMember(itemStack);
        if (member != null) {
          IGenome genome = member.getGenome();
          primarySpeciesUids[slot] = genome.getPrimary().getUID();
          secondarySpeciesUids[slot] = genome.getSecondary().getUID();
        }
      }
    }

    return prevStack;
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
    ItemStack item = setItem(fromSlot, null);
    if (item == null) {
      return null;
    }
    item.stackSize = 0;
    return item;
  }

  @Override
  public void setInventorySlotContents(int i, @Nullable ItemStack itemstack) {
    if (itemstack != null) {
      ItemStack copy = itemstack.copy();
      copy.stackSize = 0;
      setItem(i, copy);
    } else {
      setItem(i, null);
    }
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return setItem(index, null);
  }

  @Override
  public void clear() {
    for (int i = 0; i < items.length; i++) {
      setItem(i, null);
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
  public boolean isUseableByPlayer(@Nonnull EntityPlayer entityplayer) {
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
  public void createGhostSlots(List<GhostSlot> slots, final int xOffset, final int yOffset, Runnable cb) {
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
    return !isValid() && isBlacklist == DEFAULT_BLACKLIST &&
            speciesMode == DEFAULT_SPECIES_MODE &&
            sticky == DEFAULT_STICKY;
  }

  @Override
  public String toString() {
    return "SpeciesItemFilter [speciesMode=" + speciesMode + ", items=" + Arrays.toString(items) + "]";
  }

  class ItemFilterGhostSlot extends GhostSlot {
    private final int slot;
    private final Runnable cb;

    ItemFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.x = x;
      this.y = y;
      this.slot = slot;
      this.cb = cb;
    }

    @Override
    public void putStack(ItemStack stack) {
      if (stack != null && AlleleManager.alleleRegistry.getSpeciesRoot(stack) != null) {
        stack = stack.copy();
        stack.stackSize = 1;
        setItem(slot, stack);
      } else {
        setItem(slot, null);
      }

      cb.run();
    }

    @Override
    public ItemStack getStack() {
      return getStackInSlot(slot);
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


}
