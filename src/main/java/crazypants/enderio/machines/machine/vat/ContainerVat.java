package crazypants.enderio.machines.machine.vat;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerVat extends AbstractMachineContainer<TileVat> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 2;
  public static int FIRST_INVENTORY_SLOT = 2 + 0 + 0; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerVat(@Nonnull InventoryPlayer playerInv, @Nonnull TileVat te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 56, 12));
    addSlotToContainer(new InventorySlot(getInv(), 1, 105, 12));
  }

}
