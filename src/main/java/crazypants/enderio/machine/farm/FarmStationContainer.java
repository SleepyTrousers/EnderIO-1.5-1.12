package crazypants.enderio.machine.farm;

import java.awt.Point;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class FarmStationContainer extends AbstractMachineContainer {

  private static final int ROW_TOOLS = 19;
  private static final int ROW_IO = 44;

  private static final int COL_TOOLS = 44;
  private static final int COL_INPUT = 53;
  private static final int COL_OUTPUT = 116;

  private static final int SLOT_SIZE = 18;
  private static final int ONE   = 0 * SLOT_SIZE;
  private static final int TWO   = 1 * SLOT_SIZE;
  private static final int THREE = 2 * SLOT_SIZE;

  public FarmStationContainer(InventoryPlayer inventory, TileFarmStation te) {
    super(inventory,te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    Point[] points = new Point[] {
      new Point(COL_TOOLS + ONE,    ROW_TOOLS),
      new Point(COL_TOOLS + TWO,    ROW_TOOLS),
      new Point(COL_TOOLS + THREE,  ROW_TOOLS),

      new Point(COL_INPUT + ONE,    ROW_IO + ONE),
      new Point(COL_INPUT + TWO,    ROW_IO + ONE),
      new Point(COL_INPUT + ONE,    ROW_IO + TWO),
      new Point(COL_INPUT + TWO,    ROW_IO + TWO),

      new Point(COL_OUTPUT + ONE,   ROW_IO + ONE),
      new Point(COL_OUTPUT + TWO,   ROW_IO + ONE),
      new Point(COL_OUTPUT + THREE, ROW_IO + ONE),
      new Point(COL_OUTPUT + ONE,   ROW_IO + TWO),
      new Point(COL_OUTPUT + TWO,   ROW_IO + TWO),
      new Point(COL_OUTPUT + THREE, ROW_IO + TWO),
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
            int tier = ((TileFarmStation)tileEntity).getCapacitorType().ordinal();
            return (int) (16 * Math.pow(2, tier));
          }
          return 64;
        }
      });
    }

  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(8,87);
  }

  @Override
  public Point getUpgradeOffset() {
    return new Point(12,63);
  }

}
