package crazypants.enderio.machine.weather;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerWeatherObelisk extends AbstractMachineContainer {
  
  public ContainerWeatherObelisk(InventoryPlayer playerInv, TileWeatherObelisk te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 130, 80) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
  }

}
