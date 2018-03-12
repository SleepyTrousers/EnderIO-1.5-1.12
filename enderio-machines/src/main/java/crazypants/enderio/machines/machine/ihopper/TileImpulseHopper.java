package crazypants.enderio.machines.machine.ihopper;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import net.minecraft.item.ItemStack;

public class TileImpulseHopper extends AbstractCapabilityPoweredMachineEntity {

  public static final String OUTPUT_SLOT = "OUTPUT";
  public static final String INPUT_SLOT = "INPUT";
  public static final int SLOTS = 6;

  private final @Nonnull NNList<ItemStack> items;

  public TileImpulseHopper() {
    super(CapacitorKey.IMPULSE_HOPPER_POWER_INTAKE, CapacitorKey.IMPULSE_HOPPER_POWER_BUFFER, CapacitorKey.IMPULSE_HOPPER_POWER_USE);

    for (int i = 0; i < SLOTS; i++) {
      getInventory().add(Type.INPUT, INPUT_SLOT + i, new InventorySlot());
      getInventory().add(Type.OUTPUT, OUTPUT_SLOT + i, new InventorySlot());
    }

    items = new NNList<ItemStack>(SLOTS, ItemStack.EMPTY);
  }

  @Override
  public void setGhostSlotContents(int slot, @Nonnull ItemStack stack, int realsize) {
    super.setGhostSlotContents(slot, stack, realsize);
    items.set(slot, stack);
  }

  @Nonnull
  public NNList<ItemStack> getGhostSlotItems() {
    return items;
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  private boolean checkGhostSlot(int slot) {
    return getGhostSlotContents(slot) != ItemStack.EMPTY;
  }

  private boolean checkInputSlot(int slot) {
    final ItemStack ghostStack = getGhostSlotContents(slot);
    final ItemStack inputStack = getInputSlotContents(slot);
    return ghostStack != ItemStack.EMPTY && inputStack != ItemStack.EMPTY && ItemUtil.areStacksEqual(ghostStack, inputStack)
        && ghostStack.getCount() <= inputStack.getCount();
  }

  private boolean checkOutputSlot(int slot) {
    final ItemStack ghostStack = getGhostSlotContents(slot);
    final ItemStack outputStack = getOutputSlotContents(slot);
    return outputStack.isEmpty() || (!ghostStack.isEmpty() && ItemUtil.areStackMergable(ghostStack, outputStack)
        && outputStack.getCount() + ghostStack.getCount() <= outputStack.getMaxStackSize());
  }

  @Nonnull
  private ItemStack getGhostSlotContents(int slot) {
    return items.get(slot);
  }

  @Nonnull
  private ItemStack getInputSlotContents(int slot) {
    return getInventory().getSlot(INPUT_SLOT + slot).get();
  }

  @Nonnull
  private ItemStack getOutputSlotContents(int slot) {
    return getInventory().getSlot(OUTPUT_SLOT + slot).get();
  }

  private int getPowerNeedForSlot(int slot) {
    final ItemStack stack = getGhostSlotContents(slot);
    return !stack.isEmpty() ? CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.getBaseValue() * stack.getCount() : 0;
  }

  /**
   * Used to create a capacitor key with a given usage dynamically
   * 
   * @param energy
   *          the energy to use for the operation
   * @return a capacitor key for this
   */
  @Nonnull
  private ICapacitorKey getCapKey(int energy) {
    CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.setBaseValue(energy);
    return CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (getEnergy().canUseEnergy()) {
      // (1) Check if we can do a copy operation
      int neededPower = 0;
      boolean doSomething = false;
      for (int slot = 0; slot < SLOTS; slot++) {
        if (checkGhostSlot(slot)) {
          if (checkInputSlot(slot) && checkOutputSlot(slot)) {
            doSomething = true;
            neededPower += getPowerNeedForSlot(slot);
          } else {
            // We cannot, one of the preconditions is false
            return false;
          }
        }
      }
      // (2) Abort if there is nothing to copy or we don't have enough power
      if (!doSomething || !this.getEnergy().canUseEnergy(getCapKey(neededPower))) {
        CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.setBaseValue(CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.getDefaultBaseValue());
        return false;
      }
      // (3) Do the copy. Skip all the checks done above
      for (int slot = 0; slot < SLOTS; slot++) {
        final ItemStack ghostStack = getGhostSlotContents(slot);
        final ItemStack inputStack = getInputSlotContents(slot);
        if (!ghostStack.isEmpty() && !inputStack.isEmpty()) {
          final ItemStack outputStack = getOutputSlotContents(slot);
          if (!outputStack.isEmpty()) {
            final ItemStack result = outputStack.copy();
            result.grow(ghostStack.getCount());
            getInventory().getSlot(OUTPUT_SLOT + slot).set(result);
          } else {
            final ItemStack result = inputStack.copy();
            result.setCount(ghostStack.getCount());
            getInventory().getSlot(OUTPUT_SLOT + slot).set(result);
          }
          if (ghostStack.getCount() < inputStack.getCount()) {
            final ItemStack remainder = inputStack.copy();
            remainder.shrink(ghostStack.getCount());
            getInventory().getSlot(INPUT_SLOT + slot).set(remainder);
          } else {
            getInventory().getSlot(INPUT_SLOT + slot).set(ItemStack.EMPTY);
          }
        }
      }
      CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.setBaseValue(CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.getDefaultBaseValue());
      // playSound();
      return true;
    } else {
      return false;
    }
  }

}
