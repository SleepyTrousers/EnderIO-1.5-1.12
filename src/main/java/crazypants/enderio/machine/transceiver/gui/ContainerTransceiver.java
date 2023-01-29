package crazypants.enderio.machine.transceiver.gui;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machine.transceiver.TileTransceiver;

public class ContainerTransceiver extends AbstractMachineContainer<TileTransceiver> {

    public static final int GUI_WIDTH = 256;

    static final Point PLAYER_INV_OFFSET = new Point(47, 86);

    static final Point ITEM_INV_OFFSET = new Point(54, 30);

    static final Point HIDDEN_OFFSET = new Point(-3000, -3000);

    static final Point FILTER_OFFSET = new Point(PLAYER_INV_OFFSET.x, 30);

    public ContainerTransceiver(InventoryPlayer inventory, TileTransceiver te) {
        super(inventory, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {

        int i;
        for (i = 0; i < 8; i++) {
            addSlotToContainer(new Slot(getInv(), i, 0, 0) {

                @Override
                public boolean isItemValid(ItemStack itemstack) {
                    return getInv().isItemValidForSlot(getSlotIndex(), itemstack);
                    // return true;
                }
            });
        }
        for (; i < 16; i++) {
            addSlotToContainer(new Slot(getInv(), i, 0, 0) {

                @Override
                public boolean isItemValid(ItemStack p_75214_1_) {
                    return false;
                }
            });
        }
        setItemSlotLocations(getItemInventoryOffset());
    }

    public void setPlayerInventoryVisible(boolean visible) {
        Set<Entry<Slot, Point>> entries = playerSlotLocations.entrySet();
        for (Entry<Slot, Point> entry : entries) {
            entry.getKey().xDisplayPosition = visible ? entry.getValue().x : -3000;
            entry.getKey().yDisplayPosition = visible ? entry.getValue().y : -3000;
        }
    }

    public void setBufferSlotsVisible(boolean visible) {
        Point itemOffset = visible ? getItemInventoryOffset() : HIDDEN_OFFSET;
        setItemSlotLocations(itemOffset);
    }

    private void setItemSlotLocations(Point offset) {
        int i;
        int x = offset.x;
        int y = offset.y;
        for (i = 0; i < 4; i++) {
            ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
            ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
            x += 18;
        }
        x = offset.x;
        y = offset.y + 18;
        for (; i < 8; i++) {
            ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
            ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
            x += 18;
        }

        x = offset.x + (18 * 4) + getItemBufferSpacing();
        // y = offset.y + 18 + getItemRowSpacing();
        y = offset.y;
        for (; i < 12; i++) {
            ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
            ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
            x += 18;
        }
        x = offset.x + (18 * 4) + getItemBufferSpacing();
        y = offset.y + 18;
        for (; i < 16; i++) {
            ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
            ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
            x += 18;
        }
    }

    @Override
    public Point getPlayerInventoryOffset() {
        return PLAYER_INV_OFFSET;
    }

    public Point getItemInventoryOffset() {
        return ITEM_INV_OFFSET;
    }

    public Point getFilterOffset() {
        return FILTER_OFFSET;
    }

    public int getItemBufferSpacing() {
        return 5;
    }

    @Override
    protected int getIndexOfFirstPlayerInvSlot(SlotDefinition slotDef) {
        return slotDef.getNumSlots();
    }
}
