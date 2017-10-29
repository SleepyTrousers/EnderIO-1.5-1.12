package crazypants.enderio.machine.baselegacy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.capability.ItemTools;
import crazypants.enderio.capability.ItemTools.MoveResult;
import crazypants.enderio.capability.LegacyMachineWrapper;
import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

@Storable
public abstract class AbstractInventoryMachineEntity extends AbstractMachineEntity {

  @Store
  protected @Nonnull ItemStack[] inventory;
  protected final @Nonnull SlotDefinition slotDefinition;

  public AbstractInventoryMachineEntity(@Nonnull SlotDefinition slotDefinition) {
    this.slotDefinition = slotDefinition;
    inventory = new ItemStack[slotDefinition.getNumSlots()];
    clear();
  }

  public @Nonnull SlotDefinition getSlotDefinition() {
    return slotDefinition;
  }

  public boolean isValidUpgrade(@Nonnull ItemStack itemstack) {
    for (int i = slotDefinition.getMinUpgradeSlot(); i <= slotDefinition.getMaxUpgradeSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidInput(@Nonnull ItemStack itemstack) {
    for (int i = slotDefinition.getMinInputSlot(); i <= slotDefinition.getMaxInputSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidOutput(@Nonnull ItemStack itemstack) {
    for (int i = slotDefinition.getMinOutputSlot(); i <= slotDefinition.getMaxOutputSlot(); i++) {
      if (isItemValidForSlot(i, itemstack)) {
        return true;
      }
    }
    return false;
  }

  public final boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    if (Prep.isInvalid(itemstack)) {
      return false;
    }
    if (slotDefinition.isUpgradeSlot(i)) {
      return CapacitorHelper.isValidUpgrade(itemstack);
    }
    return isMachineItemValidForSlot(i, itemstack);
  }

  public abstract boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack);

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (dir == null || slotDefinition.getNumOutputSlots() <= 0 || !shouldDoWorkThisTick(20) || !hasStuffToPush()) {
      return false;
    }
    MoveResult res = ItemTools.move(getPushLimit(), world, getPos(), dir, getPos().offset(dir), dir.getOpposite());
    if (res == MoveResult.MOVED) {
      return true;
    }
    return false;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (dir == null || slotDefinition.getNumInputSlots() <= 0 || !shouldDoWorkThisTick(20) || !hasSpaceToPull()) {
      return false;
    }
    MoveResult res = ItemTools.move(getPullLimit(), world, getPos().offset(dir), dir.getOpposite(), getPos(), dir);
    if (res == MoveResult.MOVED) {
      return true;
    }
    return false;
  }

  protected boolean hasStuffToPush() {
    for (int slot = slotDefinition.minOutputSlot; slot <= slotDefinition.maxOutputSlot; slot++) {
      final ItemStack itemStack = inventory[slot];
      if (itemStack != null && Prep.isValid(itemStack)) {
        return true;
      }
    }
    return false;
  }

  protected boolean hasSpaceToPull() {
    boolean hasSpace = false;
    for (int slot = slotDefinition.minInputSlot; slot <= slotDefinition.maxInputSlot && !hasSpace; slot++) {
      final ItemStack itemStack = inventory[slot];
      hasSpace = (itemStack == null || Prep.isInvalid(itemStack)) ? true
          : itemStack.getCount() < Math.min(itemStack.getMaxStackSize(), getInventoryStackLimit(slot));
    }
    return hasSpace;
  }

  // ---- Inventory
  // ------------------------------------------------------------------------------

  public boolean isUseableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public final boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing1) {
    return getCapability(capability, facing1) != null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing1) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing1 != null) {
      return (T) new LegacyMachineWrapper(this, facing1);
    }
    return super.getCapability(capability, facing1);
  }

  public int getSizeInventory() {
    return slotDefinition.getNumSlots();
  }

  public int getInventoryStackLimit(int slot) {
    return getInventoryStackLimit();
  }

  public int getInventoryStackLimit() {
    return 64;
  }

  public @Nonnull ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inventory.length) {
      return Prep.getEmpty();
    }
    final ItemStack itemStack = inventory[slot];
    return itemStack == null ? Prep.getEmpty() : itemStack;
  }

  public @Nonnull ItemStack decrStackSize(int slot, int amount) {
    ItemStack item = inventory[slot];
    if (item != null && !item.isEmpty()) {
      if (item.getCount() <= amount) {
        ItemStack result = item;
        inventory[slot] = Prep.getEmpty();
        markDirty();
        return result;
      }
      ItemStack split = item.splitStack(amount);
      markDirty();
      return split;
    }
    return Prep.getEmpty();
  }

  public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
    if (Prep.isInvalid(contents)) {
      inventory[slot] = Prep.getEmpty();
    } else {
      inventory[slot] = contents.copy();
      if (inventory[slot].getCount() > getInventoryStackLimit(slot)) {
        inventory[slot].setCount(getInventoryStackLimit(slot));
        contents.shrink(getInventoryStackLimit(slot));
        Block.spawnAsEntity(world, pos, contents);
      }
    }
    markDirty();
  }

  public void clear() {
    for (int i = 0; i < inventory.length; ++i) {
      inventory[i] = Prep.getEmpty();
    }
    markDirty();
  }

  public @Nonnull ItemStack removeStackFromSlot(int index) {
    ItemStack res = inventory[index];
    inventory[index] = Prep.getEmpty();
    markDirty();
    return res == null ? Prep.getEmpty() : res;
  }

  public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
    if (isSideDisabled(side) || !slotDefinition.isInputSlot(slot)) {
      return false;
    }
    ItemStack existing = inventory[slot];
    if (existing != null && Prep.isValid(existing)) {
      // no point in checking the recipes if an item is already in the slot
      // worst case we get more of the wrong item - but that doesn't change
      // anything
      return existing.isStackable() && existing.getCount() < existing.getMaxStackSize() && existing.isItemEqual(itemstack);
    }
    // no need to call isItemValidForSlot as upgrade slots are not input slots
    return isMachineItemValidForSlot(slot, itemstack);
  }

  public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
    if (isSideDisabled(side)) {
      return false;
    }
    if (!slotDefinition.isOutputSlot(slot)) {
      return false;
    }
    return canExtractItem(slot, itemstack);
  }

  protected boolean canExtractItem(int slot, @Nonnull ItemStack itemstack) {
    if (inventory[slot] == null || inventory[slot].getCount() < itemstack.getCount()) {
      return false;
    }
    return itemstack.getItem() == inventory[slot].getItem();
  }

  public @Nonnull InventoryWrapper getAsInventory() {
    return new InventoryWrapper();
  }

  public class InventoryWrapper implements IInventory {
    
    public AbstractInventoryMachineEntity getOwner() {
      return AbstractInventoryMachineEntity.this;
    }

    @Override
    public @Nonnull String getName() {
      return getMachineName();
    }

    @Override
    public boolean hasCustomName() {
      return false;
    }

    @Override
    public @Nonnull ITextComponent getDisplayName() {
      return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
    }

    @Override
    public int getSizeInventory() {
      return AbstractInventoryMachineEntity.this.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
      for (int i = 0; i < getSizeInventory(); i++) {
        if (Prep.isValid(getStackInSlot(i))) {
          return false;
        }
      }
      return true;
    }

    @Override
    public @Nonnull ItemStack getStackInSlot(int index) {
      return AbstractInventoryMachineEntity.this.getStackInSlot(index);
    }

    @Override
    public @Nonnull ItemStack decrStackSize(int index, int count) {
      return AbstractInventoryMachineEntity.this.decrStackSize(index, count);
    }

    @Override
    public @Nonnull ItemStack removeStackFromSlot(int index) {
      return AbstractInventoryMachineEntity.this.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
      AbstractInventoryMachineEntity.this.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
      return AbstractInventoryMachineEntity.this.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
      AbstractInventoryMachineEntity.this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
      return AbstractInventoryMachineEntity.this.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
      return AbstractInventoryMachineEntity.this.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
      return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
      return 0;
    }

    @Override
    public void clear() {
      AbstractInventoryMachineEntity.this.clear();
    }

  }

}
