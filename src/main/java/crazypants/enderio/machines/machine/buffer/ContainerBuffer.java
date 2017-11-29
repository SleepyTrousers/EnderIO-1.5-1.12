package crazypants.enderio.machines.machine.buffer;

import java.awt.Point;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerBuffer extends AbstractMachineContainer<TileBuffer> {

  public ContainerBuffer(InventoryPlayer playerInv, TileBuffer te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    if (((TileBuffer)getInv().getOwner()).hasInventory()) {
      Point point = new Point(((TileBuffer)getInv().getOwner()).hasPower() ? 96 : 62, 15);
      for (int i = 0; i < 9; i++) {
        addSlotToContainer(new Slot(this.getInv(), i, point.x + ((i % 3) * 18), point.y + ((i / 3) * 18)));
      }
    }
  }

}
