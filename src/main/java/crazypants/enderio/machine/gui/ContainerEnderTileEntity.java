package crazypants.enderio.machine.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.ContainerEnder;

public class ContainerEnderTileEntity<T extends TileEntity & IInventory> extends ContainerEnder<T>
        implements IContainerWithTileEntity {

    public ContainerEnderTileEntity(InventoryPlayer playerInv, T inv) {
        super(playerInv, inv);
    }

    @Override
    public TileEntity getTileEntity() {
        return getInv();
    }
}
