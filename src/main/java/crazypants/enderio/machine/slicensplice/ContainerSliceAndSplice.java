package crazypants.enderio.machine.slicensplice;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import java.awt.Point;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerSliceAndSplice extends AbstractMachineContainer<TileSliceAndSplice> {

    private static final Item[] slotItems1 = {
        Items.wooden_axe,
        Items.stone_axe,
        Items.iron_axe,
        Items.golden_axe,
        Items.diamond_axe,
        DarkSteelItems.itemDarkSteelAxe
    };
    private static final Item[] slotItems2 = {
        Items.shears, Items.shears, Items.shears, Items.shears, DarkSteelItems.itemDarkSteelShears
    };
    private static final Random rand = new Random();

    public static final Point[] INPUT_SLOTS = new Point[] {
        new Point(44, 40),
        new Point(62, 40),
        new Point(80, 40),
        new Point(44, 58),
        new Point(62, 58),
        new Point(80, 58),
        new Point(54, 16),
        new Point(72, 16)
    };

    public static final Point OUTPUT_SLOT = new Point(134, 49);

    public ContainerSliceAndSplice(InventoryPlayer playerInv, TileSliceAndSplice te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {

        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            Point p = INPUT_SLOTS[i];
            final int slot = i;
            addSlotToContainer(new Slot(getInv(), i, p.x, p.y) {
                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    return getInv().isItemValidForSlot(slot, itemStack);
                }
            });
        }

        addSlotToContainer(new Slot(getInv(), 8, OUTPUT_SLOT.x, OUTPUT_SLOT.y) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return false;
            }
        });
    }

    public void createGhostSlots(List<GhostSlot> slots) {
        slots.add(new GhostBackgroundItemSlot(slotItems1[rand.nextInt(slotItems1.length)], 54, 16));
        slots.add(new GhostBackgroundItemSlot(slotItems2[rand.nextInt(slotItems2.length)], 72, 16));
    }
}
