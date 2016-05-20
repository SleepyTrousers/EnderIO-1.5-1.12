package crazypants.enderio.machine.slicensplice;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerSliceAndSplice extends AbstractMachineContainer<TileSliceAndSplice> {

  static private final Item[] slotItems1 = { Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE,
      Items.DIAMOND_AXE, DarkSteelItems.itemDarkSteelAxe };
  static private final Item[] slotItems2 = { Items.SHEARS, Items.SHEARS, Items.SHEARS, Items.SHEARS,
      DarkSteelItems.itemDarkSteelShears };
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
      addSlotToContainer(new Slot(getInv(), i, p.x, p.y) {
        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return getInv().isItemValidForSlot(slot, itemStack);
        }
      });
    }
    
    
    addSlotToContainer(new Slot(getInv(), 8, OUTPUT_SLOT.x, OUTPUT_SLOT.y) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (Slot slot : inventorySlots) {
      if (slot.getSlotIndex() == TileSliceAndSplice.axeIndex) {
        slots.add(new GhostBackgroundItemSlot(slotItems1[rand.nextInt(slotItems1.length)], slot));
      } else if (slot.getSlotIndex() == TileSliceAndSplice.shearsIndex) {
        slots.add(new GhostBackgroundItemSlot(slotItems2[rand.nextInt(slotItems2.length)], slot));
      }
    }
  }

}
