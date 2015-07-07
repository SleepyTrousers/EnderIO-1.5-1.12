package crazypants.enderio.machine.tank;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerTank extends AbstractMachineContainer<TileTank> {

  public ContainerTank(InventoryPlayer playerInv, TileTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 44, 21) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 116, 21) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 44, 52) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(2, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 116, 52) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(3, itemStack);
      }
    });
    
  }

}
