package crazypants.enderio.machine.generator.stirling;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class StirlingGeneratorContainer extends AbstractMachineContainer<TileEntityStirlingGenerator> {

    public StirlingGeneratorContainer(InventoryPlayer playerInv, TileEntityStirlingGenerator te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 80, 34) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
    }
}
