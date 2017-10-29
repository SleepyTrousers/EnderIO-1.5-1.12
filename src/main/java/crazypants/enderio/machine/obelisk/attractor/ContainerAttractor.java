package crazypants.enderio.machine.obelisk.attractor;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

import static crazypants.enderio.machine.MachineObject.itemSoulVessel;

public class ContainerAttractor extends AbstractMachineContainer<TileAttractor> {

  private static class BottleSlot extends Slot {

    private BottleSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack itemStack) {
      return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
    }
  }

  public ContainerAttractor(InventoryPlayer playerInv, TileAttractor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
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
        slots.add(new GhostBackgroundItemSlot(itemSoulVessel.getItem(), slot));
      }
    }
  }

}
