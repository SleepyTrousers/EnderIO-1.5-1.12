package crazypants.enderio.machine.farm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerIC2;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class FarmStationContainer extends AbstractMachineContainer<TileFarmStation> {

  // TODO: This is a mess. Someone should make some nice, hand-selected lists of
  // what to put in here.

  static private final Item[] slotItems1 = { Items.wooden_hoe, Items.stone_hoe, Items.iron_hoe, Items.golden_hoe, Items.diamond_hoe };
  static private final List<ItemStack> slotItemsStacks1 = new ArrayList<ItemStack>();
  static {
    for (Item item : slotItems1) {
      slotItemsStacks1.add(new ItemStack(item));
    }
    slotItemsStacks1.addAll(Config.farmHoes);
  }
  static private final Item[] slotItems2 = { Items.wooden_axe, Items.stone_axe, Items.iron_axe, Items.golden_axe,
      Items.diamond_axe, DarkSteelItems.itemDarkSteelAxe };
  static private final Item[] slotItems3 = RubberTreeFarmerIC2.treeTap != null ? new Item[] { Items.shears,
      DarkSteelItems.itemDarkSteelShears, GameRegistry.findItem("IC2", "itemTreetap") } : new Item[] { Items.shears,
      DarkSteelItems.itemDarkSteelShears };
  static public final List<ItemStack> slotItemsSeeds = new ArrayList<ItemStack>();
  static {
    slotItemsSeeds.add(new ItemStack(Items.wheat_seeds));
    slotItemsSeeds.add(new ItemStack(Blocks.carrots));
    slotItemsSeeds.add(new ItemStack(Blocks.potatoes));
    slotItemsSeeds.add(new ItemStack(Blocks.red_mushroom));
    slotItemsSeeds.add(new ItemStack(Blocks.brown_mushroom));
    slotItemsSeeds.add(new ItemStack(Blocks.nether_wart));
    slotItemsSeeds.add(new ItemStack(Blocks.sapling));
    slotItemsSeeds.add(new ItemStack(Items.reeds));
  }
  static public final List<ItemStack> slotItemsProduce = new ArrayList<ItemStack>();
  static {
    slotItemsProduce.add(new ItemStack(Blocks.log, 1, 0));
    slotItemsProduce.add(new ItemStack(Blocks.wheat));
    slotItemsProduce.add(new ItemStack(Blocks.leaves, 1, 0));
    slotItemsProduce.add(new ItemStack(Items.apple));
  }
  static public final List<ItemStack> slotItemsFertilizer = new ArrayList<ItemStack>();
  static {
    slotItemsFertilizer.add(new ItemStack(Items.dye, 1, 15));
  }

  static private final Random rand = new Random();

  private static final int ROW_TOOLS = 19;
  private static final int ROW_IO = 44;

  private static final int COL_TOOLS = 44;
  private static final int COL_INPUT = 53;
  private static final int COL_FERTILIZER = 116;
  private static final int COL_OUTPUT = 107;

  private static final int SLOT_SIZE = 18;
  private static final int ONE   = 0 * SLOT_SIZE;
  private static final int TWO   = 1 * SLOT_SIZE;
  private static final int THREE = 2 * SLOT_SIZE;

  private static final Point[] points = new Point[] {
      new Point(COL_TOOLS + ONE,    ROW_TOOLS),
      new Point(COL_TOOLS + TWO,    ROW_TOOLS),
      new Point(COL_TOOLS + THREE,  ROW_TOOLS),

      new Point(COL_FERTILIZER + ONE,   ROW_TOOLS),
      new Point(COL_FERTILIZER + TWO,   ROW_TOOLS),

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

  public FarmStationContainer(InventoryPlayer inventory, TileFarmStation te) {
    super(inventory, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    int i=0;
    for(Point p : points) {
      final int slot = i;
      i++;
      addSlotToContainer(new Slot(getInv(), slot, p.x, p.y) {
        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return getInv().isItemValidForSlot(slot, itemStack);
        }

        @Override
        public int getSlotStackLimit() {             
          return ((TileFarmStation)getInv()).getInventoryStackLimit(slot);
        }
      });
    }

  }

  private static void clean(List list) {
    Iterator iterator = list.iterator();
    while (iterator.hasNext()) {
      final Object o = iterator.next();
      if (o == null || (o instanceof ItemStack && ((ItemStack) o).getItem() == null)) {
        iterator.remove();
      }
    }
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    clean(slotItemsStacks1);
    clean(slotItemsFertilizer);
    clean(slotItemsSeeds);
    clean(slotItemsProduce);

    slots.add(new GhostBackgroundItemSlot(slotItemsStacks1.get(rand.nextInt(slotItemsStacks1.size())), points[0].x, points[0].y));
    slots.add(new GhostBackgroundItemSlot(slotItems2[rand.nextInt(slotItems2.length)], points[1].x, points[1].y));
    slots.add(new GhostBackgroundItemSlot(slotItems3[rand.nextInt(slotItems3.length)], points[2].x, points[2].y));

    slots.add(new GhostBackgroundItemSlot(slotItemsFertilizer.get(rand.nextInt(slotItemsFertilizer.size())), points[3].x,
        points[3].y));
    slots.add(new GhostBackgroundItemSlot(slotItemsFertilizer.get(rand.nextInt(slotItemsFertilizer.size())), points[4].x,
        points[4].y));

    for (int i = 0; i < 4; i++) {
      slots.add(new GhostBackgroundItemSlot(slotItemsSeeds.get(rand.nextInt(slotItemsSeeds.size())), points[5 + i].x,
          points[5 + i].y));
    }

    for (int i = 0; i < 6; i++) {
      slots.add(new GhostBackgroundItemSlot(slotItemsProduce.get(rand.nextInt(slotItemsProduce.size())), points[9 + i].x,
          points[9 + i].y));
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
