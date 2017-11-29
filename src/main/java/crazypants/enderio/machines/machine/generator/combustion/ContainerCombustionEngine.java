package crazypants.enderio.machines.machine.generator.combustion;

import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCombustionEngine extends AbstractMachineContainer<AbstractInventoryMachineEntity> {

  public ContainerCombustionEngine(InventoryPlayer playerInv, AbstractInventoryMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
