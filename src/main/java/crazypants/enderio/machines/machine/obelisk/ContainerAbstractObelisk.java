package crazypants.enderio.machines.machine.obelisk;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAbstractObelisk extends AbstractMachineContainer<AbstractRangedTileEntity> {

  public ContainerAbstractObelisk(@Nonnull InventoryPlayer playerInv, @Nonnull AbstractRangedTileEntity te) {
    super(playerInv, te);
  }

  private static class BottleSlot extends Slot {

    private BottleSlot(@Nonnull IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack itemStack) {
      return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
    }
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    int x;
    int y = 10;
    for (int row = 0; row < 3; row++) {
      x = 62;
      for (int col = 0; col < 4; col++) {
        final int index = (row * 4) + col;
        addSlotToContainer(new BottleSlot(getInv(), index, x, y));
        x += 18;
      }
      y += 18;
    }
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : inventorySlots) {
      if (slot instanceof BottleSlot) {
        slots.add(new GhostBackgroundItemSlot(ModObject.itemSoulVial.getItemNN(), slot));
      }
    }
  }
}
