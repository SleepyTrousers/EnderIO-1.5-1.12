package crazypants.enderio.machines.machine.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSagMill extends AbstractMachineContainer<TileSagMill> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 1;
  public static int FIRST_INVENTORY_SLOT = 2 + 4 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerSagMill(@Nonnull InventoryPlayer playerInv, @Nonnull TileSagMill te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 80, 12) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 122, 23) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });

    addSlotToContainer(new Slot(getInv(), 2, 49, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 70, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 4, 91, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 5, 112, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
  }

}
