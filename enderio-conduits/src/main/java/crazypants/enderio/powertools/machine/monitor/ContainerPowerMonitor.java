package crazypants.enderio.powertools.machine.monitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

class ContainerPowerMonitor extends AbstractMachineContainer<TilePowerMonitor> {

  public ContainerPowerMonitor(@Nonnull InventoryPlayer playerInv, @Nonnull TilePowerMonitor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
  }

}
