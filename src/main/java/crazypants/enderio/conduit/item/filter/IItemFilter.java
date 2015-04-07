package crazypants.enderio.conduit.item.filter;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.gui.GhostSlot;

public interface IItemFilter {

  void readFromNBT(NBTTagCompound nbtRoot);

  void writeToNBT(NBTTagCompound nbtRoot);

  void writeToByteBuf(ByteBuf buf);

  void readFromByteBuf(ByteBuf buf);

  /**
   * Checks if the given item passes the filter or not.
   * @param inv the attached inventory - or null when used without an inventory (eg for a GUI)
   * @param item the item to check
   * @return true if the item is allowed to pass
   */
  boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item);

  boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item);

  boolean isValid();

  boolean isSticky();

  void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb);

  int getSlotCount();

}
