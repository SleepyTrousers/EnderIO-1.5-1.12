package crazypants.enderio.machine.obelisk.inhibitor;

import net.minecraft.entity.player.InventoryPlayer;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerInhibitorObelisk extends AbstractMachineContainer {

    public ContainerInhibitorObelisk(InventoryPlayer playerInv, AbstractMachineEntity te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {}
}
