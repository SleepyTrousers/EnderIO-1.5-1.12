package crazypants.enderio.machines.machine.farm;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FarmStationContainer extends AbstractMachineContainer<TileFarmStation> {

  // TODO: This is a mess. Someone should make some nice, hand-selected lists of
  // what to put in here.

  static private final @Nonnull Things slotItemsStacks1 = new Things().add(Config.farmHoes).add(Items.WOODEN_HOE).add(Items.STONE_HOE).add(Items.IRON_HOE)
      .add(Items.GOLDEN_HOE).add(Items.DIAMOND_HOE);
  static private final @Nonnull Things slotItemsStacks2 = new Things().add(Items.WOODEN_AXE).add(Items.STONE_AXE).add(Items.IRON_AXE).add(Items.GOLDEN_AXE)
      .add(Items.DIAMOND_AXE).add(ModObject.itemDarkSteelAxe);
  static private final @Nonnull Things slotItemsStacks3 = new Things().add(Items.SHEARS).add(ModObject.itemDarkSteelShears)
      .add(FarmingTool.TREETAP.getThings());
  static public final @Nonnull Things slotItemsSeeds = new Things().add(Items.WHEAT_SEEDS).add(Items.CARROT).add(Items.POTATO).add(Blocks.RED_MUSHROOM)
      .add(Blocks.BROWN_MUSHROOM).add(Items.NETHER_WART).add(Blocks.SAPLING).add(Items.REEDS).add(Items.MELON_SEEDS).add(Items.PUMPKIN_SEEDS);
  static public final @Nonnull Things slotItemsProduce = new Things().add(new ItemStack(Blocks.LOG, 1, 0)).add(Blocks.WHEAT)
      .add(new ItemStack(Blocks.LEAVES, 1, 0)).add(Items.APPLE).add(Items.MELON).add(Blocks.PUMPKIN);
  static public final @Nonnull Things slotItemsFertilizer = new Things().add(new ItemStack(Items.DYE, 1, 15));

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

      new SlotPoint(COL_TOOLS + ONE, ROW_TOOLS, slotItemsStacks1), //
      new SlotPoint(COL_TOOLS + TWO, ROW_TOOLS, slotItemsStacks2), //
      new SlotPoint(COL_TOOLS + THREE, ROW_TOOLS, slotItemsStacks3),

      new SlotPoint(COL_FERTILIZER + ONE, ROW_TOOLS, slotItemsFertilizer), //
      new SlotPoint(COL_FERTILIZER + TWO, ROW_TOOLS, slotItemsFertilizer),

      new SlotPoint(COL_INPUT + ONE, ROW_IO + ONE, slotItemsSeeds), //
      new SlotPoint(COL_INPUT + TWO, ROW_IO + ONE, slotItemsSeeds), //
      new SlotPoint(COL_INPUT + ONE, ROW_IO + TWO, slotItemsSeeds), //
      new SlotPoint(COL_INPUT + TWO, ROW_IO + TWO, slotItemsSeeds),

      new SlotPoint(COL_OUTPUT + ONE, ROW_IO + ONE, slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + TWO, ROW_IO + ONE, slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + THREE, ROW_IO + ONE, slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + ONE, ROW_IO + TWO, slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + TWO, ROW_IO + TWO, slotItemsProduce), //
      new SlotPoint(COL_OUTPUT + THREE, ROW_IO + TWO, slotItemsProduce), //
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
