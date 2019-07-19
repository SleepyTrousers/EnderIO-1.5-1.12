package crazypants.enderio.base.machine.base.te;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.View;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.capability.ItemTools.MoveResult;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.power.EnergyTank;
import crazypants.enderio.base.power.IEnergyTank;
import crazypants.enderio.base.power.NullEnergyTank;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Storable
public abstract class AbstractCapabilityMachineEntity extends AbstractMachineEntity {

  @Store({ NBTAction.SAVE, NBTAction.ITEM })
  private final @Nonnull EnderInventory inventory = new EnderInventory();
  private final @Nonnull EnderInventory inventoryDelegate;
  private final @Nonnull View upgradeSlots, inputSlots, outputSlots;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  // TODO 1.14: remove here and store to nbt in EnergyLogic
  private final @Nonnull NullEnergyTank energy; // NullEnergyTank not IEnergyTank because @Store!
  private final @Nonnull IEnergyLogic energyLogic;

  protected AbstractCapabilityMachineEntity() {
    this(null);
  }

  protected AbstractCapabilityMachineEntity(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this(() -> subclassInventory, owner -> new EnergyLogic(owner, new EnergyTank(owner, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed)));
  }

  protected AbstractCapabilityMachineEntity(@Nullable EnderInventory subclassInventory) {
    this(() -> subclassInventory, owner -> null);
  }

  /**
   * If an inventory is given, it will NOT be stored to nbt/client/save. The subclass must handle that itself.
   */
  protected AbstractCapabilityMachineEntity(@Nonnull Supplier<EnderInventory> inventorySupplier,
      @Nonnull Function<AbstractCapabilityMachineEntity, IEnergyLogic> logicSupplier) {
    this.inventoryDelegate = NullHelper.first(inventorySupplier.get(), this.inventory);
    upgradeSlots = inventoryDelegate.getView(EnderInventory.Type.UPGRADE);
    inputSlots = inventoryDelegate.getView(EnderInventory.Type.INPUT);
    outputSlots = inventoryDelegate.getView(EnderInventory.Type.OUTPUT);
    inventoryDelegate.setOwner(this);
    energyLogic = NullHelper.first(logicSupplier.apply(this), NullEnergyLogic.INSTANCE);
    energy = (NullEnergyTank) energyLogic.getEnergy(); // sic
    addICap(ITEM_HANDLER_CAPABILITY, ICap.facedOnly(facingIn -> getIoMode(facingIn).canInputOrOutput() ? new Side(facingIn) : null));
  }

  /////////////////////////////////////////////////////////////////////////
  // INVENTORY
  /////////////////////////////////////////////////////////////////////////

  public @Nonnull EnderInventory getInventory() {
    return inventoryDelegate;
  }

  public boolean isValidUpgrade(@Nonnull ItemStack itemstack) {
    for (InventorySlot slot : upgradeSlots) {
      if (slot.isItemValidForSlot(itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidInput(@Nonnull ItemStack itemstack) {
    for (InventorySlot slot : inputSlots) {
      if (slot.isItemValidForSlot(itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidOutput(@Nonnull ItemStack itemstack) {
    for (InventorySlot slot : outputSlots) {
      if (slot.isItemValidForSlot(itemstack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (dir == null || outputSlots.getSlots() <= 0 || !shouldDoWorkThisTick(20)) {
      return false;
    }
    return ItemTools.move(getPushLimit(), world, this, dir, getPos().offset(dir), dir.getOpposite()) == MoveResult.MOVED;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (dir == null || inputSlots.getSlots() <= 0 || !shouldDoWorkThisTick(20) || !hasSpaceToPull()) {
      return false;
    }
    return ItemTools.move(getPullLimit(), world, getPos().offset(dir), dir.getOpposite(), this, dir) == MoveResult.MOVED;
  }

  protected boolean hasSpaceToPull() {
    for (InventorySlot slot : inputSlots) {
      ItemStack stack = slot.getStackInSlot(0);
      if (Prep.isInvalid(stack) || stack.getCount() < Math.min(stack.getMaxStackSize(), slot.getMaxStackSize())) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean mergeOutput(@Nonnull ItemStack stack) {
    InventorySlot empty = null;
    for (InventorySlot slot : outputSlots) {
      ItemStack oldOutput = slot.get();
      if (oldOutput.isEmpty()) {
        if (empty == null && slot.isItemValidForSlot(stack)) {
          empty = slot;
        }
      } else if (!ItemUtil.isStackFull(oldOutput) && ItemUtil.areStackMergable(oldOutput, stack)) {
        oldOutput.grow(stack.splitStack(Math.min(oldOutput.getMaxStackSize() - oldOutput.getCount(), stack.getCount())).getCount());
        slot.set(oldOutput);
        return stack.isEmpty();
      }
    }
    if (empty != null) {
      empty.set(stack);
      return true;
    }

    return false;
  }

  /////////////////////////////////////////////////////////////////////////
  // TICKING
  /////////////////////////////////////////////////////////////////////////

  @Override
  public void doUpdate() {
    super.doUpdate();
    getEnergyLogic().serverTick();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    getEnergyLogic().processTasks(redstoneCheck);
    return false;
  }

  /////////////////////////////////////////////////////////////////////////
  // ENERGY
  /////////////////////////////////////////////////////////////////////////

  /**
   * @return <code>true</code> if the machine has a energy buffer
   */
  public boolean displayPower() {
    return getEnergyLogic().displayPower();
  }

  /**
   * @return <code>true</code> if the machine has energy in its buffer
   */
  public boolean hasPower() {
    return getEnergyLogic().hasPower();
  }

  /**
   * 
   * @return an energy buffer, possibly one that cannot held energy---check {@link #displayPower()} first)
   */
  public @Nonnull IEnergyTank getEnergy() {
    return getEnergyLogic().getEnergy();
  }

  /**
   * 
   * @return an energy logic, possibly one that cannot do anything---check {@link #displayPower()} first)
   */
  public IEnergyLogic getEnergyLogic() {
    return energyLogic;
  }

  /////////////////////////////////////////////////////////////////////////
  // CAPACITOR
  /////////////////////////////////////////////////////////////////////////

  /**
   * 
   * @return a capacitor data holder. Note that "no capacitor in the slot" also has a valid data holder. And for machines without capacitor slot, that's the
   *         only data holder they will return.
   */
  public @Nonnull ICapacitorData getCapacitorData() {
    return getEnergyLogic().getCapacitorData();
  }

  /**
   * Callback that is called every time the capacitor data holder (see {@link #getCapacitorData()}) changes.
   */
  protected void onCapacitorDataChange() {
  };

  /////////////////////////////////////////////////////////////////////////
  // NBT
  /////////////////////////////////////////////////////////////////////////

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    getEnergyLogic().readCustomNBT(stack);
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    getEnergyLogic().writeCustomNBT(stack);
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    getEnergyLogic().updateCapacitorFromSlot(); // TODO trace out if we need this or if the inventory calls onChange() for this
  }

  /////////////////////////////////////////////////////////////////////////
  // CAPABILITIES
  /////////////////////////////////////////////////////////////////////////

  private class Side implements IItemHandler {

    private final EnumFacing side;

    protected Side(EnumFacing side) {
      this.side = side;
    }

    private @Nonnull IItemHandler getView() {
      if (side == null) {
        return getInventory().getView(EnderInventory.Type.INTERNAL);
      }
      switch (getIoMode(side)) {
      case NONE:
      case PUSH_PULL:
        return getInventory().getView(EnderInventory.Type.INOUT);
      case PULL:
        return getInventory().getView(EnderInventory.Type.INPUT);
      case PUSH:
        return getInventory().getView(EnderInventory.Type.OUTPUT);
      case DISABLED:
      default:
        return EnderInventory.OFF;
      }
    }

    @Override
    public int getSlots() {
      return getView().getSlots();
    }

    @Override
    public @Nonnull ItemStack getStackInSlot(int slot) {
      return getView().getStackInSlot(slot);
    }

    @Override
    public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (!Prep.isInvalid(stack)) {
        IoMode mode = getIoMode(side);
        if (mode.canRecieveInput()) {
          return getInventory().getView(EnderInventory.Type.INPUT).insertItem(slot, stack, simulate);
        }
      }
      return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
      IoMode mode = getIoMode(side);
      if (mode.canOutput()) {
        return getInventory().getView(EnderInventory.Type.OUTPUT).extractItem(slot, amount, simulate);
      }
      return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
      return getView().getSlotLimit(slot);
    }

  }
}
