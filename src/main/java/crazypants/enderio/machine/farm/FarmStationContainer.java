package crazypants.enderio.machine.farm;

import java.awt.Point;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class FarmStationContainer extends AbstractMachineContainer {

  public FarmStationContainer(InventoryPlayer inventory, TileFarmStation te) {
    super(inventory,te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    Point[] points = new Point[] {
      new Point(44, 12),
      new Point(62, 12),
      new Point(80, 12),

      new Point(53, 37),
      new Point(71, 37),
      new Point(53, 55),
      new Point(71, 55),

      new Point(116, 37),
      new Point(134, 37),
      new Point(116, 55),
      new Point(134, 55),
    };

    int i=0;
    for(Point p : points) {
      final int slot = i;
      i++;
      addSlotToContainer(new Slot(tileEntity, slot, p.x, p.y) {
        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return tileEntity.isItemValidForSlot(slot, itemStack);
        }

        @Override
        public int getSlotStackLimit() {             
          if(slot > 2 && slot < 7) {
            int tier = ((TileFarmStation)tileEntity).tier;
            return (int) (16 * Math.pow(2, tier));
          }
          return 64;
        }
      });
    }

  }

}
