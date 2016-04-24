package crazypants.enderio.machine.tank;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.fluid.Buckets;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerTank extends AbstractMachineContainer<TileTank> {

  static private final Item[] slotItems = { Items.water_bucket, Items.lava_bucket, Buckets.itemBucketNutrientDistillation,
      Buckets.itemBucketHootch, Buckets.itemBucketRocketFuel, Buckets.itemBucketFireWater };
  static private final Random rand = new Random();

  // only used on client where there can only be one GUI open at any given time
  private static Slot inFull, inEmpty, outEmpty, outFull;

  public ContainerTank(InventoryPlayer playerInv, TileTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(inFull = new Slot(getInv(), 0, 44, 21) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(inEmpty = new Slot(getInv(), 1, 116, 21) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 10000, 10000) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(2, itemStack);
      }
    });    
    addSlotToContainer(outEmpty = new Slot(getInv(), 3, 44, 52) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(3, itemStack);
      }
    });
    addSlotToContainer(outFull = new Slot(getInv(), 4, 116, 52) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(4, itemStack);
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(slotItems[rand.nextInt(slotItems.length)], inFull));
    slots.add(new GhostBackgroundItemSlot(Items.bucket, inEmpty));
    slots.add(new GhostBackgroundItemSlot(Items.bucket, outEmpty));
    slots.add(new GhostBackgroundItemSlot(slotItems[rand.nextInt(slotItems.length)], outFull));
  }

}
