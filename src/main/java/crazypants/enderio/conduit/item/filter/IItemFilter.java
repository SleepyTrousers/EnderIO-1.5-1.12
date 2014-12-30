package crazypants.enderio.conduit.item.filter;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.conduit.item.NetworkedInventory;

public interface IItemFilter {

  void readFromNBT(NBTTagCompound nbtRoot);

  void writeToNBT(NBTTagCompound nbtRoot);

  void writeToByteBuf(ByteBuf buf);

  void readFromByteBuf(ByteBuf buf);

  boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item);

  boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item);

  boolean isValid();

  boolean isSticky();

  List<Slot> getSlots(int xOffset, int yOffset);

  int getSlotCount();

}
