package crazypants.enderio.machine.crusher;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;

public class ContainerCrusher extends AbstractMachineContainer {

  public ContainerCrusher(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 54, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isStackValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 1, 107, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
  }


}
