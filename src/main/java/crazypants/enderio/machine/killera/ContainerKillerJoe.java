package crazypants.enderio.machine.killera;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerKillerJoe extends AbstractMachineContainer<TileKillerJoe> {

  public ContainerKillerJoe(InventoryPlayer playerInv, TileKillerJoe te) {
    super(playerInv, te);
  }

  private int slotno;

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    slotno = inventorySlots.indexOf(addSlotToContainer(new Slot(getInv(), 0, 48, 24) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    }));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(TileKillerJoe.WEAPONS.getItemStacks(), inventorySlots.get(slotno)));
  }

}
