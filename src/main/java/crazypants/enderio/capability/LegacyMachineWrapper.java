package crazypants.enderio.capability;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.AbstractInventoryMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class LegacyMachineWrapper implements IItemHandler {
  protected final AbstractInventoryMachineEntity machine;
  protected final EnumFacing side;
  protected IoMode lastIoMode = null;
  protected final List<Integer> slots = new ArrayList<Integer>();

  public LegacyMachineWrapper(AbstractInventoryMachineEntity machine, EnumFacing side) {
    this.machine = machine;
    this.side = side;
  }

  protected void computeSlotMappings() {
    final IoMode ioMode = machine.getIoMode(side);
    if (ioMode != lastIoMode) {
      slots.clear();
      final SlotDefinition slotDefinition = machine.getSlotDefinition();
      for (int i = 0; i < slotDefinition.getNumSlots(); i++) {
        if ((ioMode.canRecieveInput() && slotDefinition.isInputSlot(i)) || ((ioMode.canOutput() && slotDefinition.isOutputSlot(i)))) {
          slots.add(i);
        }
      }
      lastIoMode = ioMode;
    }
  }

  @Override
  public int getSlots() {
    computeSlotMappings();
    return slots.size();
  }

  protected int extSlot2intSlot(int external) {
    computeSlotMappings();
    if (external >= 0 && external < slots.size()) {
      return slots.get(external);
    }
    return -1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return machine.getStackInSlot(extSlot2intSlot(slot));
  }

  @Override
  public ItemStack insertItem(int external, ItemStack stack, boolean simulate) {
    if (Prep.isInvalid(stack) || !machine.getIoMode(side).canRecieveInput()) {
      return stack;
    }

    int slot = extSlot2intSlot(external);
    if (!machine.getSlotDefinition().isInputSlot(slot)||!this.machine.canInsertItem(slot,stack,side)) {
      return stack;
    }

    return doInsertItem(slot, stack, simulate);
  }

  protected ItemStack doInsertItem(int internal, ItemStack stack, boolean simulate) {
    ItemStack existing = machine.getStackInSlot(internal);
    if (Prep.isValid(existing)) {
      int max = Math.min(existing.getMaxStackSize(), machine.getInventoryStackLimit(internal));
      if (existing.stackSize >= max || !ItemUtil.areStackMergable(existing, stack)) {
        return stack;
      }
      int movable = Math.min(max - existing.stackSize, stack.stackSize);
      if (!simulate) {
        existing.stackSize += movable;
        machine.markDirty();
      }
      if (movable >= stack.stackSize) {
        return Prep.getEmpty();
      } else {
        ItemStack copy = stack.copy();
        copy.stackSize -= movable;
        return copy;
      }
    } else {
      if (!machine.isMachineItemValidForSlot(internal, stack)) {
        return stack;
      }
      int max = Math.min(stack.getMaxStackSize(), machine.getInventoryStackLimit(internal));
      if (max >= stack.stackSize) {
        if (!simulate) {
          machine.setInventorySlotContents(internal, stack.copy());
        }
        return Prep.getEmpty();
      } else {
        ItemStack copy = stack.copy();
        copy.stackSize = max;
        if (!simulate) {
          machine.setInventorySlotContents(internal, copy);
        }
        copy = stack.copy();
        copy.stackSize -= max;
        return copy;
      }
    }
  }

  @Override
  public ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount <= 0 || !machine.getIoMode(side).canOutput())
      return Prep.getEmpty();

    int slot = extSlot2intSlot(external);
    if (!machine.getSlotDefinition().isOutputSlot(slot)) {
      return Prep.getEmpty();
    }

    return doExtractItem(slot, amount, simulate);
  }

  protected ItemStack doExtractItem(int internal, int amount, boolean simulate) {
    ItemStack existing = machine.getStackInSlot(internal);

    if (Prep.isInvalid(existing)) {
      return Prep.getEmpty();
    }

    int max = Math.min(amount, existing.stackSize);

    ItemStack copy = existing.copy();
    copy.stackSize = max;

    if (!simulate) {
      existing.stackSize -= max;
      if (existing.stackSize <= 0) {
        machine.setInventorySlotContents(internal, Prep.getEmpty());
      } else {
        machine.markDirty();
      }
    }
    return copy;
  }

}
