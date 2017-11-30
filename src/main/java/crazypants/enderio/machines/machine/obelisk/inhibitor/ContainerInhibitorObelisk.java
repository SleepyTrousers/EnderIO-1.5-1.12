package crazypants.enderio.machines.machine.obelisk.inhibitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerInhibitorObelisk extends AbstractMachineContainer<AbstractInventoryMachineEntity> {

  public ContainerInhibitorObelisk(@Nonnull InventoryPlayer playerInv, @Nonnull AbstractInventoryMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
  }

}
