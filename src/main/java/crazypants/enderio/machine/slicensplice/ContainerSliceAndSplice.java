package crazypants.enderio.machine.slicensplice;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerSliceAndSplice extends AbstractMachineContainer<TileSliceAndSplice> {

  private class InvSlot extends Slot {
    private final int slot;

    private InvSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, int slot) {
      super(inventoryIn, index, xPosition, yPosition);
      this.slot = slot;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack itemStack) {
      return getInv().isItemValidForSlot(slot, itemStack);
    }

    @Override
    public void putStack(@Nullable ItemStack stack) {
      if (stack == null || stack.stackSize <= getItemStackLimit(stack)) {
        super.putStack(stack);
      } else {
        throw new RuntimeException("Invalid stacksize. " + stack.stackSize + " is more than the allowed limit of " + getItemStackLimit(stack)
            + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
      }
    }
  }

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 6;
  public static int FIRST_INVENTORY_SLOT = 8 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  static private final Item[] slotItems1 = { Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE,
      Items.DIAMOND_AXE, ModObject.itemDarkSteelAxe };
  static private final Item[] slotItems2 = { Items.SHEARS, Items.SHEARS, Items.SHEARS, Items.SHEARS,
      ModObject.itemDarkSteelShears };
  static private final Random rand = new Random();

  public static final Point[] INPUT_SLOTS = new Point[] {      
      new Point(44,40),
      new Point(62,40),
      new Point(80,40),
      new Point(44,58),
      new Point(62,58),
      new Point(80,58),
      new Point(54,16),
      new Point(72,16)
  };
  
  public static final Point OUTPUT_SLOT = new Point(134, 49); 
  
  public ContainerSliceAndSplice(InventoryPlayer playerInv, TileSliceAndSplice te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) { 
    for(int i=0;i<INPUT_SLOTS.length;i++) {
      Point p = INPUT_SLOTS[i];
      final int slot = i; 
      addSlotToContainer(new InvSlot(getInv(), i, p.x, p.y, slot));
    }
    
    
    addSlotToContainer(new Slot(getInv(), 8, OUTPUT_SLOT.x, OUTPUT_SLOT.y) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }
    });
    
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : inventorySlots) {
      if (slot instanceof InvSlot) {
        if (slot.getSlotIndex() == TileSliceAndSplice.axeIndex) {
          slots.add(new GhostBackgroundItemSlot(slotItems1[rand.nextInt(slotItems1.length)], slot));
        } else if (slot.getSlotIndex() == TileSliceAndSplice.shearsIndex) {
          slots.add(new GhostBackgroundItemSlot(slotItems2[rand.nextInt(slotItems2.length)], slot));
        }
      }
    }
  }

}
