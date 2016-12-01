package crazypants.enderio.capability;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

public class EnderInventory implements IItemHandler {

  public static enum Type {
    ALL,
    INPUT,
    OUTPUT,
    INOUT,
    UPGRADE,
    INTERNAL,
  }

  private final Map<String, InventorySlot> idents = new HashMap<String, InventorySlot>();
  private final EnumMap<EnderInventory.Type, List<InventorySlot>> slots = new EnumMap<EnderInventory.Type, List<InventorySlot>>(EnderInventory.Type.class);
  private final View allSlots = new View(EnderInventory.Type.ALL);
  private @Nullable TileEntity owner = null;

  public EnderInventory() {
    for (EnderInventory.Type type : EnderInventory.Type.values()) {
      slots.put(type, new ArrayList<InventorySlot>());
    }
  }

  public void add(EnderInventory.Type type, Enum<?> ident, InventorySlot slot) {
    add(type, ident.name(), slot);
  }

  public void add(EnderInventory.Type type, String ident, InventorySlot slot) {
    if (idents.containsKey(ident)) {
      throw new RuntimeException("Duplicate slot '" + ident + "'");
    }
    if (type == EnderInventory.Type.ALL) {
      throw new RuntimeException("Invalid type '" + type + "'");
    }
    idents.put(ident, slot);
    slots.get(type).add(slot);
    slots.get(EnderInventory.Type.ALL).add(slot);
    if (type == EnderInventory.Type.INPUT || type == EnderInventory.Type.OUTPUT) {
      slots.get(EnderInventory.Type.INOUT).add(slot);
    }
    if (type == EnderInventory.Type.INOUT) {
      slots.get(EnderInventory.Type.INPUT).add(slot);
      slots.get(EnderInventory.Type.OUTPUT).add(slot);
    }
    slot.setOwner(owner);
  }

  public InventorySlot getSlot(Enum<?> ident) {
    return getSlot(ident.name());
  }

  public InventorySlot getSlot(String ident) {
    if (!idents.containsKey(ident)) {
      throw new RuntimeException("Unknown slot '" + ident + "'");
    }
    return idents.get(ident);
  }

  public View getView(EnderInventory.Type type) {
    return new View(type);
  }

  public NBTTagCompound writeToNBT() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return tag;
  }

  public void writeToNBT(NBTTagCompound tag) {
    for (Entry<String, InventorySlot> entry : idents.entrySet()) {
      if (entry.getValue() != null) {
        NBTTagCompound subTag = new NBTTagCompound();
        entry.getValue().writeToNBT(subTag);
        tag.setTag(entry.getKey(), subTag);
      }
    }
  }

  public void readFromNBT(NBTTagCompound tag, String name) {
    readFromNBT(tag.getCompoundTag(name));
  }

  public void readFromNBT(NBTTagCompound tag) {
    for (Entry<String, InventorySlot> entry : idents.entrySet()) {
      if (entry.getValue() != null) {
        entry.getValue().readFromNBT(tag.getCompoundTag(entry.getKey()));
      }
    }
  }

  public void setOwner(TileEntity owner) {
    this.owner = owner;
    for (InventorySlot slot : idents.values()) {
      slot.setOwner(owner);
    }
  }

  @Override
  public int getSlots() {
    return allSlots.getSlots();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return allSlots.getStackInSlot(slot);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return allSlots.insertItem(slot, stack, simulate);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return allSlots.extractItem(slot, amount, simulate);
  }

  public class View implements IItemHandler, Iterable<InventorySlot> {

    private final EnderInventory.Type type;

    private View(Type type) {
      this.type = type;
    }

    public InventorySlot getSlot(int slot) {
      if (slot >= 0 && slot < getSlots()) {
        return slots.get(type).get(slot);
      }
      return null;
    }

    @Override
    public int getSlots() {
      return slots.get(type).size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
      if (slot >= 0 && slot < getSlots()) {
        InventorySlot inventorySlot = slots.get(type).get(slot);
        if (inventorySlot != null) {
          return inventorySlot.getStackInSlot(0);
        }
      }
      return Prep.getEmpty();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      if (slot >= 0 && slot < getSlots()) {
        InventorySlot inventorySlot = slots.get(type).get(slot);
        if (inventorySlot != null) {
          return inventorySlot.insertItem(0, stack, simulate);
        }
      }
      return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (slot >= 0 && slot < getSlots()) {
        InventorySlot inventorySlot = slots.get(type).get(slot);
        if (inventorySlot != null) {
          return inventorySlot.extractItem(0, amount, simulate);
        }
      }
      return Prep.getEmpty();
    }

    @Override
    public Iterator<InventorySlot> iterator() {
      return new Iterator<InventorySlot>() {

        int i = 0;

        @Override
        public boolean hasNext() {
          return i < getSlots();
        }

        @Override
        public InventorySlot next() {
          return getSlot(i++);
        }
      };
    }

  }
}
