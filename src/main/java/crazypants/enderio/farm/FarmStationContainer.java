package crazypants.enderio.farm;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.AbstractMachineContainer;

public class FarmStationContainer extends AbstractMachineContainer {

  public FarmStationContainer(InventoryPlayer inventory, TileFarmStation te) {
    super(inventory,te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
