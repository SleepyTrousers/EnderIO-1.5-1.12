package crazypants.enderio.machine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.capability.EnderInventory;
import crazypants.enderio.capability.EnderInventory.View;
import crazypants.enderio.capability.InventorySlot;
import crazypants.enderio.capability.ItemTools;
import crazypants.enderio.capability.ItemTools.MoveResult;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

@Storable
public abstract class AbstractCapabilityMachineEntity extends AbstractMachineEntity {

  @Store
  private final EnderInventory inventory = new EnderInventory();
  private final EnderInventory inventoryDelegate;
  private final View upgradeSlots, inputSlots, outputSlots;

  protected AbstractCapabilityMachineEntity() {
    this(null);
  }

  /**
   * If an inventory is given, it will NOT be stored to nbt/client/save. The subclass must handle that itself.
   */
  protected AbstractCapabilityMachineEntity(EnderInventory subclassInventory) {
    super();
    this.inventoryDelegate = subclassInventory != null ? subclassInventory : this.inventory;
    upgradeSlots = inventoryDelegate.getView(EnderInventory.Type.UPGRADE);
    inputSlots = inventoryDelegate.getView(EnderInventory.Type.INPUT);
    outputSlots = inventoryDelegate.getView(EnderInventory.Type.OUTPUT);
    inventoryDelegate.setOwner(this);
  }

  public EnderInventory getInventory() {
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
    MoveResult res = ItemTools.move(getPushLimit(), worldObj, getPos(), dir, getPos().offset(dir), dir.getOpposite());
    if (res == MoveResult.MOVED) {
      return true;
    }
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (dir == null || inputSlots.getSlots() <= 0 || !shouldDoWorkThisTick(20) || !hasSpaceToPull()) {
      return false;
    }
    MoveResult res = ItemTools.move(getPullLimit(), worldObj, getPos().offset(dir), dir.getOpposite(), getPos(), dir);
    if (res == MoveResult.MOVED) {
      return true;
    }
    return false;
  }

  protected boolean hasSpaceToPull() {
    for (InventorySlot slot : inputSlots) {
      ItemStack stack = slot.getStackInSlot(0);
      if (Prep.isInvalid(stack) || stack.stackSize < Math.min(stack.getMaxStackSize(), slot.getMaxStackSize())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facingIn == null) {
        return (T) getInventory().getView(EnderInventory.Type.INTERNAL);
      }
      switch (getIoMode(facingIn)) {
      case NONE:
      case PUSH_PULL:
        return (T) getInventory().getView(EnderInventory.Type.INOUT);
      case PULL:
        return (T) getInventory().getView(EnderInventory.Type.INPUT);
      case PUSH:
        return (T) getInventory().getView(EnderInventory.Type.OUTPUT);
      case DISABLED:
      default:
        return null;
      }
    }
    return super.getCapability(capability, facingIn);
  }

}
