package crazypants.enderio.machines.machine.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPainter extends AbstractMachineContainer<TileEntityPainter> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 2;
  public static int FIRST_INVENTORY_SLOT = 2 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerPainter(@Nonnull InventoryPlayer playerInv, @Nonnull TileEntityPainter te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 67, 34));
    addSlotToContainer(new InventorySlot(getInv(), 1, 38, 34));
    addSlotToContainer(new InventorySlot(getInv(), 2, 121, 34));
  }

}
