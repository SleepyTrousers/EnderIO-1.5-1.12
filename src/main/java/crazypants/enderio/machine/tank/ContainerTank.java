package crazypants.enderio.machine.tank;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerTank extends AbstractMachineContainer<TileTank> {

    private static final Item[] slotItems = { Items.water_bucket, Items.lava_bucket,
            EnderIO.itemBucketNutrientDistillation, EnderIO.itemBucketHootch, EnderIO.itemBucketRocketFuel,
            EnderIO.itemBucketFireWater };
    private static final Random rand = new Random();

    public ContainerTank(InventoryPlayer playerInv, TileTank te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 44, 21) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 1, 116, 21) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(1, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 2, 10000, 10000) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(2, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 3, 44, 52) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(3, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 4, 116, 52) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(4, itemStack);
            }
        });
    }

    public void createGhostSlots(List<GhostSlot> slots) {
        slots.add(new GhostBackgroundItemSlot(slotItems[rand.nextInt(slotItems.length)], 44, 21));
        slots.add(new GhostBackgroundItemSlot(Items.bucket, 116, 21));
        slots.add(new GhostBackgroundItemSlot(Items.bucket, 44, 52));
        slots.add(new GhostBackgroundItemSlot(slotItems[rand.nextInt(slotItems.length)], 116, 52));
    }
}
