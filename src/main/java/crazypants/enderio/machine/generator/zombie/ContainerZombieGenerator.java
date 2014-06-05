package crazypants.enderio.machine.generator.zombie;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;

public class ContainerZombieGenerator extends AbstractMachineContainer {

  public ContainerZombieGenerator(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }
  
}
