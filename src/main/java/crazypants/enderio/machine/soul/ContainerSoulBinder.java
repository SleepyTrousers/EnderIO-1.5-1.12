package crazypants.enderio.machine.soul;


import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSoulBinder  extends AbstractMachineContainer<TileSoulBinder> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 2;
  public static int FIRST_INVENTORY_SLOT = 2 + 2 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerSoulBinder(InventoryPlayer playerInv, TileSoulBinder te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 38, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }

      @Override
      public void putStack(@Nullable ItemStack stack) {
        if (stack == null || stack.getCount() <= getItemStackLimit(stack)) {
          super.putStack(stack);
        } else {
          throw new RuntimeException("Invalid stacksize. " + stack.getCount() + " is more than the allowed limit of " + getItemStackLimit(stack)
              + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
        }
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 59, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }      

      @Override
      public void putStack(@Nullable ItemStack stack) {
        if (stack == null || stack.getCount() <= getItemStackLimit(stack)) {
          super.putStack(stack);
        } else {
          throw new RuntimeException("Invalid stacksize. " + stack.getCount() + " is more than the allowed limit of " + getItemStackLimit(stack)
              + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
        }
      }
    });    
    addSlotToContainer(new Slot(getInv(), 2, 112, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 134, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(ModObject.itemSoulVial.getItem(), getSlotFromInventory(getInv(), 0)));
    slots.add(new GhostBackgroundItemSlot(ModObject.itemBrokenSpawner.getItem(), getSlotFromInventory(getInv(), 1)));
  }

}
