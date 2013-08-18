package crazypants.enderio.machine.generator;

import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class StirlingGeneratorContainer extends AbstractMachineContainer {

  public StirlingGeneratorContainer(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 80, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isStackValidForSlot(0, itemStack);
      }
    });   
  }

}
