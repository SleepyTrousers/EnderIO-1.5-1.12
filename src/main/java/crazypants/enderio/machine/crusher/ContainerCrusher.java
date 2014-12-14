package crazypants.enderio.machine.crusher;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerCrusher extends AbstractMachineContainer {

  public ContainerCrusher(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 80, 12) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 1, 122, 23) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(1, itemStack);
      }
    });

    addSlotToContainer(new Slot(tileEntity, 2, 49, 59) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(tileEntity, 3, 70, 59) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(tileEntity, 4, 91, 59) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(tileEntity, 5, 112, 59) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
  }

}
