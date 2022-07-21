package crazypants.enderio.machine.crusher;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCrusher extends AbstractMachineContainer<TileCrusher> {

    public ContainerCrusher(InventoryPlayer playerInv, TileCrusher te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 80, 12) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 1, 122, 23) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(1, itemStack);
            }
        });

        addSlotToContainer(new Slot(getInv(), 2, 49, 59) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return false;
            }
        });
        addSlotToContainer(new Slot(getInv(), 3, 70, 59) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return false;
            }
        });
        addSlotToContainer(new Slot(getInv(), 4, 91, 59) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return false;
            }
        });
        addSlotToContainer(new Slot(getInv(), 5, 112, 59) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return false;
            }
        });
    }
}
