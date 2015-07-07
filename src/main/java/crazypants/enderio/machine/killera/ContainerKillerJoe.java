package crazypants.enderio.machine.killera;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerKillerJoe extends AbstractMachineContainer<TileKillerJoe> {

  public ContainerKillerJoe(InventoryPlayer playerInv, TileKillerJoe te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 80, 25) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
  }

}
