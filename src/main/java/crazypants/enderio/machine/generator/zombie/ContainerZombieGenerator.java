package crazypants.enderio.machine.generator.zombie;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerZombieGenerator extends AbstractMachineContainer<AbstractInventoryMachineEntity> {

  public ContainerZombieGenerator(InventoryPlayer playerInv, AbstractInventoryMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }
  
}
