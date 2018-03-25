package crazypants.enderio.machines.machine.ihopper;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack;
import net.minecraft.item.ItemStack;

@Storable
public class TileImpulseHopper extends AbstractCapabilityPoweredMachineEntity implements IRedstoneModeControlable {

  public static final String OUTPUT_SLOT = "OUTPUT";
  public static final String INPUT_SLOT = "INPUT";
  public static final int SLOTS = 6;
  public static final int BASE_TICK_RATE = 20;

  @Store
  private boolean isOutputLocked = false;

  private class PredicateItemStackMatch extends PredicateItemStack {

    private final int slot;

    PredicateItemStackMatch(int slot) {
      this.slot = slot;
    }

    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.isItemEqual(items.get(slot));
    }

  }

  @Store(handler = HandleItemStack.HandleItemStackNNList.class)
  private final @Nonnull NNList<ItemStack> items;

  public TileImpulseHopper() {
    super(CapacitorKey.IMPULSE_HOPPER_POWER_INTAKE, CapacitorKey.IMPULSE_HOPPER_POWER_BUFFER, CapacitorKey.IMPULSE_HOPPER_POWER_USE);

    for (int i = 0; i < SLOTS; i++) {
      PredicateItemStackMatch predicate = new PredicateItemStackMatch(i);
      getInventory().add(Type.INPUT, INPUT_SLOT + i, new InventorySlot(predicate, Filters.ALWAYS_TRUE));
      getInventory().add(Type.OUTPUT, OUTPUT_SLOT + i, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
    }

    items = new NNList<ItemStack>(SLOTS, ItemStack.EMPTY);
    redstoneControlMode = RedstoneControlMode.IGNORE;
  }

  @Override
  public void setGhostSlotContents(int slot, @Nonnull ItemStack stack, int realsize) {
    super.setGhostSlotContents(slot, stack, realsize);
    items.set(slot, stack);
    forceUpdatePlayers();
  }

  @Nonnull
  public NNList<ItemStack> getGhostSlotItems() {
    return items;
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  public boolean checkGhostSlot(int slot) {
    return !getGhostSlotContents(slot).isEmpty();
  }

  public boolean checkInputSlot(int slot) {
    final ItemStack ghostStack = getGhostSlotContents(slot);
    final ItemStack inputStack = getInputSlotContents(slot);
    return !ghostStack.isEmpty() && !inputStack.isEmpty() && ItemUtil.areStacksEqual(ghostStack, inputStack) && ghostStack.getCount() <= inputStack.getCount();
  }

  public boolean checkOutputSlot(int slot) {
    final ItemStack ghostStack = getGhostSlotContents(slot);
    final ItemStack outputStack = getOutputSlotContents(slot);
    return outputStack.isEmpty() || (!ghostStack.isEmpty() && ItemUtil.areStackMergable(ghostStack, outputStack)
        && outputStack.getCount() + ghostStack.getCount() <= outputStack.getMaxStackSize() && !isOutputLocked);
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

  public boolean isOutputLocked() {
    return isOutputLocked;
  }

  public void setOutputLocked(boolean isOutputLocked) {
    this.isOutputLocked = isOutputLocked;
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
    if (shouldDoWorkThisTick() && redstoneCheck) {
      if (getEnergy().useEnergy()) {
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
        if (!doSomething || !this.getEnergy().useEnergy(getCapKey(neededPower))) {
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
        getEnergy().useEnergy();
        getEnergy().useEnergy(getCapKey(neededPower));
        CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.setBaseValue(CapacitorKey.IMPULSE_HOPPER_POWER_USE_PER_ITEM.getDefaultBaseValue());
        // playSound();
        return super.processTasks(redstoneCheck);
      } else {
        return false;
      }
    }
    return false;
  }

  private boolean shouldDoWorkThisTick() {
    int impulseHopperSpeedScaled = CapacitorKey.IMPULSE_HOPPER_SPEED.get(getCapacitorData());
    if (impulseHopperSpeedScaled > 0) {
      return shouldDoWorkThisTick(BASE_TICK_RATE / impulseHopperSpeedScaled);
    }
    return false;
  }

}
