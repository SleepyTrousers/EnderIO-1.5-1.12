package crazypants.enderio.teleport.telepad;

import java.awt.Point;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.ContainerEnder;

import crazypants.enderio.machine.gui.IContainerWithTileEntity;

public class ContainerTelePad extends ContainerEnder<IInventory> implements IContainerWithTileEntity {

    private final TileTelePad te;

    public ContainerTelePad(InventoryPlayer playerInv, TileTelePad te) {
        super(playerInv, playerInv);
        this.te = te;
    }

    @Override
    public Point getPlayerInventoryOffset() {
        Point p = super.getPlayerInventoryOffset();
        p.translate(0, 34);
        return p;
    }

    @Override
    public TileEntity getTileEntity() {
        return te;
    }
}
