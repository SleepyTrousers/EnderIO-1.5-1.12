package crazypants.enderio.machine.soul;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerSoulBinder  extends AbstractMachineContainer {

  public ContainerSoulBinder(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 38, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 1, 59, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(1, itemStack);
      }      
    });    
    addSlotToContainer(new Slot(tileEntity, 2, 112, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(tileEntity, 3, 134, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
  }

}
