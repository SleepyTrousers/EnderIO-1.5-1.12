package crazypants.enderio.machine.generator.combustion;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;

public class ContainerCombustionEngine extends AbstractMachineContainer {

  public ContainerCombustionEngine(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
