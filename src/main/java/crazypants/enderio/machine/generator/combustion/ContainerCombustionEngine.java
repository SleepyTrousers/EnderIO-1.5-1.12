package crazypants.enderio.machine.generator.combustion;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerCombustionEngine extends AbstractMachineContainer {

  public ContainerCombustionEngine(InventoryPlayer playerInv, TileCombustionGenerator te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

}
