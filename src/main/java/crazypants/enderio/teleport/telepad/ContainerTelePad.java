package crazypants.enderio.teleport.telepad;

import com.enderio.core.common.ContainerEnder;
import java.awt.Point;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public class ContainerTelePad extends ContainerEnder<IInventory> {

    public ContainerTelePad(InventoryPlayer playerInv) {
        super(playerInv, playerInv);
    }

    @Override
    public Point getPlayerInventoryOffset() {
        Point p = super.getPlayerInventoryOffset();
        p.translate(0, 34);
        return p;
    }
}
