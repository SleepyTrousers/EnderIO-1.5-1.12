package crazypants.enderio.machine.tank;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.fluid.Buckets;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ContainerTank extends AbstractMachineContainer<TileTank> {

  static private final Things slotItems = new Things().add(Items.WATER_BUCKET).add(Items.LAVA_BUCKET).add(Buckets.itemBucketNutrientDistillation)
      .add(Buckets.itemBucketHootch).add(Buckets.itemBucketRocketFuel).add(Buckets.itemBucketFireWater);
  static private final Things mendables = new Things("minecraft:iron_shovel", "minecraft:iron_pickaxe", "minecraft:iron_axe", "minecraft:iron_sword",
      "minecraft:iron_hoe", "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots", "minecraft:bow");

  // only used on client where there can only be one GUI open at any given time
  private static Slot inFull, inEmpty, outEmpty, outFull;

  public ContainerTank(InventoryPlayer playerInv, TileTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(inFull = new Slot(getInv(), 0, 44, 21) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(inEmpty = new Slot(getInv(), 1, 116, 21) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 10000, 10000) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(2, itemStack);
      }
    });    
    addSlotToContainer(outEmpty = new Slot(getInv(), 3, 44, 52) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(3, itemStack);
      }
    });
    addSlotToContainer(outFull = new Slot(getInv(), 4, 116, 52) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(4, itemStack);
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(slotItems.getItemStacks(), inFull));
    if (getInv().tank.isEmpty() || getInv().tank.getFluid().getFluid() != Fluids.fluidXpJuice) {
      slots.add(new GhostBackgroundItemSlot(Items.BUCKET, inEmpty));
    } else {
      slots.add(new GhostBackgroundItemSlot(mendables.getItemStacks(), inEmpty));
    }
    slots.add(new GhostBackgroundItemSlot(Items.BUCKET, outEmpty));
    slots.add(new GhostBackgroundItemSlot(slotItems.getItemStacks(), outFull));
  }

}
