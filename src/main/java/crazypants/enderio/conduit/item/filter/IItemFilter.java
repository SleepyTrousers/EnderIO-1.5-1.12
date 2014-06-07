package crazypants.enderio.conduit.item.filter;

import java.util.List;

import crazypants.enderio.conduit.item.NetworkedInventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemFilter {

  void readFromNBT(NBTTagCompound nbtRoot);

  void writeToNBT(NBTTagCompound nbtRoot);

  boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item);

  boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item);

  boolean isValid();

  boolean isSticky();

  List<Slot> getSlots();

  int getSlotCount();
  
}
