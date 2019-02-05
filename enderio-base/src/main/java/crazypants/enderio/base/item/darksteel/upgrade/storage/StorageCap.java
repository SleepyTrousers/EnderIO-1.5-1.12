package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * An itemstack nbt-based inventory. Mostly using Forge's implementation with 3 small additions:
 * <p>
 * <ol>
 * <li>The size is fixed, even if the nbt has a different size. Extra existing slots are hidden, missing slots are added or faked with slots that are forced
 * empty.
 * <li>The data is read/saved from/to an itemstack
 * <li>Certain dangerous items are not allowed in.
 * </ol>
 * 
 * @author Henry Loenwind
 *
 */
public class StorageCap extends ItemStackHandler {

  private final @Nonnull EntityEquipmentSlot equipmentSlot;
  private final @Nonnull ItemStack owner;
  private final int size;

  public StorageCap(@Nonnull EntityEquipmentSlot equipmentSlot, int size, @Nonnull ItemStack owner) {
    super(size);
    this.equipmentSlot = equipmentSlot;
    this.owner = size > 0 ? owner : Prep.getEmpty();
    this.size = size;
    deserializeNBT(NbtValue.INVENTORY.getTag(owner));
    while (size > super.getSlots()) {
      stacks.add(Prep.getEmpty());
    }
  }

  public StorageCap(@Nonnull EntityEquipmentSlot equipmentSlot, int size) {
    super(size);
    this.equipmentSlot = equipmentSlot;
    this.owner = Prep.getEmpty();
    this.size = size;
  }

  public @Nonnull EntityEquipmentSlot getEquipmentSlot() {
    return equipmentSlot;
  }

  @Override
  public int getSlots() {
    return size;
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int slot) {
    if (fakeEmptySlot(slot)) {
      return ItemStack.EMPTY;
    }
    return super.getStackInSlot(slot);
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (fakeEmptySlot(slot) || stack == owner || stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null) {
      return ItemStack.EMPTY;
    }

    return super.insertItem(slot, stack, simulate);
  }

  @Override
  @Nonnull
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (fakeEmptySlot(slot)) {
      return ItemStack.EMPTY;
    }
    return super.extractItem(slot, amount, simulate);
  }

  @Override
  protected void onContentsChanged(int slot) {
    NbtValue.INVENTORY.setTag(owner, serializeNBT());
  }

  protected boolean fakeEmptySlot(int slot) {
    return slot < size && slot >= super.getSlots();
  }

}
