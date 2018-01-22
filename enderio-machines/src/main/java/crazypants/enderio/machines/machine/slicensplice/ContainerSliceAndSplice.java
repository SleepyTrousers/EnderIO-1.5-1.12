package crazypants.enderio.machines.machine.slicensplice;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSliceAndSplice extends AbstractMachineContainer<TileSliceAndSplice> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 6;
  public static int FIRST_INVENTORY_SLOT = 8 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  static private final Things slotItems1 = new Things().add(Items.WOODEN_AXE).add(Items.STONE_AXE).add(Items.IRON_AXE).add(Items.GOLDEN_AXE)
      .add(Items.DIAMOND_AXE).add(ModObject.itemDarkSteelAxe);
  static private final Things slotItems2 = new Things().add(Items.SHEARS).add(Items.SHEARS).add(Items.SHEARS).add(Items.SHEARS)
      .add(ModObject.itemDarkSteelShears);

  public static final Point[] INPUT_SLOTS = new Point[] { new Point(44, 40), new Point(62, 40), new Point(80, 40), new Point(44, 58), new Point(62, 58),
      new Point(80, 58), new Point(54, 16), new Point(72, 16) };

  public static final Point OUTPUT_SLOT = new Point(134, 49);

  public ContainerSliceAndSplice(@Nonnull InventoryPlayer playerInv, @Nonnull TileSliceAndSplice te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    for (int i = 0; i < INPUT_SLOTS.length; i++) {
      Point p = INPUT_SLOTS[i];
      addSlotToContainer(new InventorySlot(getInv(), i, p.x, p.y));
    }

    addSlotToContainer(new Slot(getInv(), 8, OUTPUT_SLOT.x, OUTPUT_SLOT.y) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }

      @Override
      public int getSlotStackLimit() {
        return getTe().getInventoryStackLimit(getSlotIndex());
      }
    });

  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : inventorySlots) {
      if (slot instanceof InvSlot) {
        if (slot.getSlotIndex() == TileSliceAndSplice.axeIndex) {
          slots.add(new GhostBackgroundItemSlot(slotItems1.getItemStacks(), slot));
        } else if (slot.getSlotIndex() == TileSliceAndSplice.shearsIndex) {
          slots.add(new GhostBackgroundItemSlot(slotItems2.getItemStacks(), slot));
        }
      }
    }
  }

}
