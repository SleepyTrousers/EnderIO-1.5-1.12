package crazypants.enderio.machine.vacuum;

import java.awt.Point;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import crazypants.enderio.EnderIO;

public class ContainerVacuumChest extends ContainerEnder<TileVacuumChest> {

    private Slot filterSlot;
    private Runnable filterChangedCB;

    public ContainerVacuumChest(EntityPlayer player, InventoryPlayer inventory, final TileVacuumChest te) {
        super(inventory, te);
    }

    @Override
    protected void addSlots(InventoryPlayer playerInv) {
        filterSlot = new FilterSlot(new InventoryFilterUpgrade(getInv()));
        addSlotToContainer(filterSlot);

        int x = 8;
        int y = 18;
        int index = -1;
        for (int i = 0; i < TileVacuumChest.ITEM_ROWS; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(getInv(), ++index, x + j * 18, y + i * 18));
            }
        }
    }

    public void createGhostSlots(List<GhostSlot> slots) {
        slots.add(new GhostBackgroundItemSlot(EnderIO.itemBasicFilterUpgrade, filterSlot));
    }

    @Override
    public Point getPlayerInventoryOffset() {
        Point p = super.getPlayerInventoryOffset();
        p.translate(0, 40);
        return p;
    }

    void setFilterChangedCB(Runnable filterChangedCB) {
        this.filterChangedCB = filterChangedCB;
    }

    void filterChanged() {
        if (filterChangedCB != null) {
            filterChangedCB.run();
        }
    }

    class FilterSlot extends Slot {

        InventoryFilterUpgrade inv;

        FilterSlot(InventoryFilterUpgrade inv) {
            super(inv, 0, 8, 86);
            this.inv = inv;
        }

        @Override
        public void onSlotChanged() {
            filterChanged();
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return inv.isItemValidForSlot(0, stack);
        }
    }
}
