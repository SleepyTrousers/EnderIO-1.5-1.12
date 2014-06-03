package crazypants.enderio.conduit.item;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemFilter {

  void readFromNBT(NBTTagCompound nbtRoot);

  void writeToNBT(NBTTagCompound nbtRoot);

  boolean doesItemPassFilter(ItemStack item);

  boolean doesFilterCaptureStack(ItemStack item);

  boolean isValid();

  boolean isSticky();

  List<Slot> getSlots();

  int getSlotCount();
  
}
