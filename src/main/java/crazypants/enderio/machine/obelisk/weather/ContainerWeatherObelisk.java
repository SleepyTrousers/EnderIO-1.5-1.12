package crazypants.enderio.machine.obelisk.weather;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerWeatherObelisk extends AbstractMachineContainer<TileWeatherObelisk> {

  public static final int MAX_SCALE = 31;
  
  public ContainerWeatherObelisk(InventoryPlayer playerInv, TileWeatherObelisk te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 80, 11) {
      @Override
      public boolean isItemValid(ItemStack p_75214_1_) {
        return getInv().isItemValidForSlot(0, p_75214_1_);
      }
    });
  }
}
