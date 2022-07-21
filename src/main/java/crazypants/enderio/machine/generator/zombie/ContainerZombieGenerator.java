package crazypants.enderio.machine.generator.zombie;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerZombieGenerator extends AbstractMachineContainer {

    public ContainerZombieGenerator(InventoryPlayer playerInv, AbstractMachineEntity te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {}
}
