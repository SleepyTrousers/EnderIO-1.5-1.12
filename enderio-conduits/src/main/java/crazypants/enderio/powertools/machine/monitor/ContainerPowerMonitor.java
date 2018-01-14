package crazypants.enderio.powertools.machine.monitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

class ContainerPowerMonitor extends AbstractMachineContainer<TilePowerMonitor> implements IPowerMonitorRemoteExec.Container {

  public ContainerPowerMonitor(@Nonnull InventoryPlayer playerInv, @Nonnull TilePowerMonitor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
  }

  @Override
  public IMessage doSetConfig(boolean engineControlEnabled, float startLevel, float stopLevel) {
    getTe().setEngineControlEnabled(engineControlEnabled);
    getTe().setStartLevel(startLevel);
    getTe().setStopLevel(stopLevel);
    return null;
  }

}
