package crazypants.enderio.capability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

//TODO 1.11 - use ec version
public class EnderSlot extends SlotItemHandler {

  public EnderSlot(InventorySlot itemHandler, int xPosition, int yPosition) {
    super(itemHandler, 0, xPosition, yPosition);
  }

  public EnderSlot(EnderInventory enderInventory, String ident, int xPosition, int yPosition) {
    super(enderInventory.getSlot(ident), 0, xPosition, yPosition);
  }

  public EnderSlot(EnderInventory enderInventory, Enum<?> ident, int xPosition, int yPosition) {
    super(enderInventory.getSlot(ident), 0, xPosition, yPosition);
  }

  public static List<EnderSlot> create(EnderInventory enderInventory, EnderInventory.Type type, int xPosition, int yPosition, int cols, int rows) {
    return create(enderInventory, type, xPosition, yPosition, 18, 18, cols, rows);
  }

  public static List<EnderSlot> create(EnderInventory enderInventory, EnderInventory.Type type, int xPosition, int yPosition, int xOffset, int yOffset,
      int cols, int rows) {
    List<EnderSlot> result = new ArrayList<EnderSlot>();
    int x = 0, y = 0;
    EnderInventory.View view = enderInventory.getView(type);
    for (int i = 0; i < view.getSlots(); i++) {
      InventorySlot slot = view.getSlot(i);
      if (slot != null) {
        result.add(new EnderSlot(slot, xPosition + x * xOffset, yPosition + y * yOffset));
        x++;
        if (x >= cols) {
          y++;
          x = 0;
          if (y >= rows) {
            return result;
          }
        }
      }
    }

    return result;
  }

  @Override
  public void putStack(@Nonnull ItemStack stack) {
    ((InventorySlot) getItemHandler()).set(stack);
    this.onSlotChanged();
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return ((InventorySlot) getItemHandler()).isItemValidForSlot(stack);
  }

  @Override
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return getSlotStackLimit();
  }

  @Override
  public int getSlotStackLimit() {
    return ((InventorySlot) getItemHandler()).getMaxStackSize();
  }

  @Override
  public boolean isSameInventory(Slot other) {
    return other instanceof EnderSlot && ((InventorySlot) ((EnderSlot) other).getItemHandler()).getOwner() == ((InventorySlot) getItemHandler()).getOwner();
  }

}
