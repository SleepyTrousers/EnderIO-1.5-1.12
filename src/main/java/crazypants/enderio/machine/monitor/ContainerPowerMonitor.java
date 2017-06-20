package crazypants.enderio.machine.monitor;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

class ContainerPowerMonitor extends AbstractMachineContainer<TilePowerMonitor> {

  public ContainerPowerMonitor(InventoryPlayer playerInv, TilePowerMonitor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
