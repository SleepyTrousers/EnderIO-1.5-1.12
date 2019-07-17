package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.capability.ItemTools.MoveResult;
import crazypants.enderio.base.capability.LegacyMachineWrapper;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.base.te.ICap;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Storable
public abstract class AbstractInventoryMachineEntity extends AbstractMachineEntity {

  @Store({ NBTAction.SAVE, NBTAction.ITEM })
  protected @Nonnull ItemStack[] inventory;
  protected final @Nonnull SlotDefinition slotDefinition;

  public AbstractInventoryMachineEntity(@Nonnull SlotDefinition slotDefinition) {
    this.slotDefinition = slotDefinition;
    inventory = new ItemStack[slotDefinition.getNumSlots()];
    for (int i = 0; i < inventory.length; ++i) {
      inventory[i] = Prep.getEmpty();
    }
    addICap(ITEM_HANDLER_CAPABILITY, ICap.facedOnly(facingIn -> new LegacyMachineWrapper(this, facingIn)));
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    if (inventory.length < slotDefinition.getNumSlots()) {
      // This can happen if a machine was upgraded into a version that has more slots
      ItemStack[] tmp = inventory;
      inventory = new ItemStack[slotDefinition.getNumSlots()];
      for (int i = 0; i < tmp.length; i++) {
        inventory[i] = tmp[i];
      }
    }
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
    if (i < 0 || Prep.isInvalid(itemstack) || i >= slotDefinition.getNumSlots()) {
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
    if (dir == null || !shouldDoWorkThisTick(20) || !hasStuffToPush()) {
      return false;
    }
    return ItemTools.move(getPushLimit(), world, this, dir, getPos().offset(dir), dir.getOpposite()) == MoveResult.MOVED;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (dir == null || !shouldDoWorkThisTick(20) || !hasSpaceToPull()) {
      return false;
    }
    return ItemTools.move(getPullLimit(), world, getPos().offset(dir), dir.getOpposite(), this, dir) == MoveResult.MOVED;
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

  public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
    inventory[slot] = contents.copy();
    if (inventory[slot].getCount() > getInventoryStackLimit(slot)) {
      inventory[slot].setCount(getInventoryStackLimit(slot));
      contents.shrink(getInventoryStackLimit(slot));
      Block.spawnAsEntity(world, pos, contents);
    }
    markDirty();
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
      ItemStack item = getStackInSlot(index);
      if (Prep.isValid(item)) {
        if (item.getCount() <= count) {
          setInventorySlotContents(index, Prep.getEmpty());
          return item;
        }
        ItemStack split = item.splitStack(count);
        setInventorySlotContents(index, item);
        return split;
      }
      return Prep.getEmpty();
    }

    @Override
    public @Nonnull ItemStack removeStackFromSlot(int index) {
      ItemStack res = getStackInSlot(index);
      setInventorySlotContents(index, Prep.getEmpty());
      return res;
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
      for (int i = 0; i < getSizeInventory(); ++i) {
        setInventorySlotContents(i, Prep.getEmpty());
      }
    }

  }

}
