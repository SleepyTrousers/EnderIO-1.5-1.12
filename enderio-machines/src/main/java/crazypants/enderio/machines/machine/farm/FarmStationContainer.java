package crazypants.enderio.machines.machine.farm;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FarmStationContainer extends AbstractMachineContainer<TileFarmStation> {

  private static final int ROW_TOOLS = 19;
  private static final int ROW_IO = 44;

  private static final int COL_TOOLS = 44;
  private static final int COL_INPUT = 53;
  private static final int COL_FERTILIZER = 116;
  private static final int COL_OUTPUT = 107;

  private static final int SLOT_SIZE = 18;
  private static final int ONE = 0 * SLOT_SIZE;
  private static final int TWO = 1 * SLOT_SIZE;
  private static final int THREE = 2 * SLOT_SIZE;

  private static final SlotPoint[] points = new SlotPoint[] { //

      new SlotPoint(COL_TOOLS + ONE, ROW_TOOLS, FarmersRegistry.slotItemsHoeTools), //
      new SlotPoint(COL_TOOLS + TWO, ROW_TOOLS, FarmersRegistry.slotItemsAxeTools), //
      new SlotPoint(COL_TOOLS + THREE, ROW_TOOLS, FarmersRegistry.slotItemsExtraTools),

      new SlotPoint(COL_FERTILIZER + ONE, ROW_TOOLS, FarmersRegistry.slotItemsFertilizer), //
      new SlotPoint(COL_FERTILIZER + TWO, ROW_TOOLS, FarmersRegistry.slotItemsFertilizer),

      new SlotPoint(COL_INPUT + ONE, ROW_IO + ONE, FarmersRegistry.slotItemsSeeds), //
      new SlotPoint(COL_INPUT + TWO, ROW_IO + ONE, FarmersRegistry.slotItemsSeeds), //
      new SlotPoint(COL_INPUT + ONE, ROW_IO + TWO, FarmersRegistry.slotItemsSeeds), //
      new SlotPoint(COL_INPUT + TWO, ROW_IO + TWO, FarmersRegistry.slotItemsSeeds),

      new SlotPoint(COL_OUTPUT + ONE, ROW_IO + ONE, FarmersRegistry.slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + TWO, ROW_IO + ONE, FarmersRegistry.slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + THREE, ROW_IO + ONE, FarmersRegistry.slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + ONE, ROW_IO + TWO, FarmersRegistry.slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + TWO, ROW_IO + TWO, FarmersRegistry.slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + THREE, ROW_IO + TWO, FarmersRegistry.slotItemsProduce), //
  };

  public FarmStationContainer(@Nonnull InventoryPlayer inventory, @Nonnull TileFarmStation te) {
    super(inventory, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    int i = 0;
    for (SlotPoint p : points) {
      final int slot = i;
      i++;
      addSlotToContainer(p.s = new Slot(getInv(), slot, p.x, p.y) {
        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return getInv().isItemValidForSlot(slot, itemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return getInv().getInventoryStackLimit();
        }
      });
    }

  }

  public void createGhostSlots(GhostSlotHandler slots) {
    for (SlotPoint p : points) {
      final Slot slot = p.s;
      if (slot != null) {
        slots.add(new GhostBackgroundItemSlot(p.ghosts.getItemStacks(), slot));
      }
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 87);
  }

  @Override
  public @Nonnull Point getUpgradeOffset() {
    return new Point(12, 63);
  }

  private static class SlotPoint {
    int x, y;
    @Nonnull
    Things ghosts;
    // It's a bit of a hack having the slot in a static field, but it is only used on the client, and there only one instance of the GUI can exist at any time,
    // so it works.
    Slot s = null;

    SlotPoint(int x, int y, @Nonnull Things ghosts) {
      this.x = x;
      this.y = y;
      this.ghosts = ghosts;
    }

  }

}
