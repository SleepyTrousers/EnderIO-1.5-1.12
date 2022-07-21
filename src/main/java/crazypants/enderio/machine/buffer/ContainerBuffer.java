package crazypants.enderio.machine.buffer;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import java.awt.Point;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerBuffer extends AbstractMachineContainer<TileBuffer> {

    public ContainerBuffer(InventoryPlayer playerInv, TileBuffer te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        TileBuffer buf = (TileBuffer) getInv();
        if (buf.hasInventory()) {
            Point point = new Point(buf.hasInventory() && buf.hasPower() ? 96 : 62, 15);
            for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(this.getInv(), i, point.x + ((i % 3) * 18), point.y + ((i / 3) * 18)));
            }
        }
    }

    @Override
    public Point getPlayerInventoryOffset() {
        return super.getPlayerInventoryOffset();
    }
}
