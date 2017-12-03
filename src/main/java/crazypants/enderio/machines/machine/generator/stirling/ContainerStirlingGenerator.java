package crazypants.enderio.machines.machine.generator.stirling;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStirlingGenerator<T extends TileStirlingGenerator> extends AbstractMachineContainer<T> {

  public ContainerStirlingGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 80, 34) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
  }

}
