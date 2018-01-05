package crazypants.enderio.base.capability;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class LegacyMachineWrapper implements IItemHandler {
  protected final @Nonnull AbstractInventoryMachineEntity machine;
  protected final @Nonnull EnumFacing side;
  protected IoMode lastIoMode = null;
  protected final @Nonnull NNList<Integer> slots = new NNList<Integer>();

  public LegacyMachineWrapper(@Nonnull AbstractInventoryMachineEntity machine, @Nonnull EnumFacing side) {
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
  public @Nonnull ItemStack getStackInSlot(int slot) {
    return machine.getStackInSlot(extSlot2intSlot(slot));
  }

  @Override
  public @Nonnull ItemStack insertItem(int external, @Nonnull ItemStack stack, boolean simulate) {
    if (Prep.isInvalid(stack) || !machine.getIoMode(side).canRecieveInput()) {
      return stack;
    }

    int slot = extSlot2intSlot(external);
    if (!machine.getSlotDefinition().isInputSlot(slot)) {
      return stack;
    }

    return doInsertItem(slot, stack, simulate);
  }

  protected @Nonnull ItemStack doInsertItem(int internal, @Nonnull ItemStack stack, boolean simulate) {
    ItemStack existing = machine.getStackInSlot(internal);
    if (Prep.isValid(existing)) {
      int max = Math.min(existing.getMaxStackSize(), machine.getInventoryStackLimit(internal));
      if (existing.getCount() >= max || !ItemUtil.areStackMergable(existing, stack)) {
        return stack;
      }
      int movable = Math.min(max - existing.getCount(), stack.getCount());
      if (!simulate) {
        existing.grow(movable);
        machine.markDirty();
      }
      if (movable >= stack.getCount()) {
        return Prep.getEmpty();
      } else {
        ItemStack copy = stack.copy();
        copy.shrink(movable);
        return copy;
      }
    } else {
      if (!machine.isMachineItemValidForSlot(internal, stack)) {
        return stack;
      }
      int max = Math.min(stack.getMaxStackSize(), machine.getInventoryStackLimit(internal));
      if (max >= stack.getCount()) {
        if (!simulate) {
          machine.setInventorySlotContents(internal, stack.copy());
        }
        return Prep.getEmpty();
      } else {
        ItemStack copy = stack.copy();
        copy.setCount(max);
        if (!simulate) {
          machine.setInventorySlotContents(internal, copy);
        }
        copy = stack.copy();
        copy.shrink(max);
        return copy;
      }
    }
  }

  @Override
  public @Nonnull ItemStack extractItem(int external, int amount, boolean simulate) {
    if (amount <= 0 || !machine.getIoMode(side).canOutput())
      return Prep.getEmpty();

    int slot = extSlot2intSlot(external);
    if (!machine.getSlotDefinition().isOutputSlot(slot)) {
      return Prep.getEmpty();
    }

    return doExtractItem(slot, amount, simulate);
  }

  protected @Nonnull ItemStack doExtractItem(int internal, int amount, boolean simulate) {
    ItemStack existing = machine.getStackInSlot(internal);

    if (Prep.isInvalid(existing)) {
      return Prep.getEmpty();
    }

    int max = Math.min(amount, existing.getCount());

    ItemStack copy = existing.copy();
    copy.setCount(max);

    if (!simulate) {
      existing.shrink(max);
      if (Prep.isInvalid(existing)) {
        machine.setInventorySlotContents(internal, Prep.getEmpty());
      } else {
        machine.markDirty();
      }
    }
    return copy;
  }

  @Override
  public int getSlotLimit(int external) {
    int internal = extSlot2intSlot(external);
    return internal >= 0 ? machine.getInventoryStackLimit(internal) : 0;
  }

}