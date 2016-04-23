package crazypants.enderio.machine.monitor;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

class ContainerPowerMonitor extends AbstractMachineContainer<TilePowerMonitor> {

  public ContainerPowerMonitor(InventoryPlayer playerInv, TilePowerMonitor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
