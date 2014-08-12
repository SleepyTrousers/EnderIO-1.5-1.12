package crazypants.enderio.machine.killera;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;

public class ContainerKillerJoe extends AbstractMachineContainer {

  public ContainerKillerJoe(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 80, 25) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
  }

}
