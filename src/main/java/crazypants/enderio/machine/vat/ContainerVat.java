package crazypants.enderio.machine.vat;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVat extends AbstractMachineContainer<TileVat> {

    public ContainerVat(InventoryPlayer playerInv, TileVat te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 56, 12) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 1, 105, 12) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(1, itemStack);
            }
        });
    }
}
