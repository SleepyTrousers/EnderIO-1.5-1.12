package crazypants.enderio.machine.spawner;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerPoweredSpawner extends AbstractMachineContainer {

  private Slot slotInput;
  private Slot slotOutput;

  public ContainerPoweredSpawner(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    slotInput = addSlotToContainer(new Slot(tileEntity, 0, 54, 42) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
    slotOutput = addSlotToContainer(new Slot(tileEntity, 1, 105, 42) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return false;
      }
    });
  }

  public void setSlotVisibility(boolean visible) {
    slotInput.yDisplayPosition = visible ? 42 : -3000;
    slotOutput.yDisplayPosition = visible ? 42 : -3000;
  }

}
