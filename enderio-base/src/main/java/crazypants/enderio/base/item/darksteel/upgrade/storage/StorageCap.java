package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import crazypants.enderio.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StorageCap extends ItemStackHandler {

  private final @Nonnull ItemStack owner;

  public StorageCap(int size, @Nonnull ItemStack owner) {
    super(size);
    this.owner = owner;
    deserializeNBT(NbtValue.INVENTORY.getTag(owner));
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack == owner || stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null)
      return ItemStack.EMPTY;

    return super.insertItem(slot, stack, simulate);
  }

  @Override
  protected void onContentsChanged(int slot) {
    NbtValue.INVENTORY.setTag(owner, serializeNBT());
  }
}
