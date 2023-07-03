package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import crazypants.enderio.machine.gui.IContainerWithTileEntity;

public class ContainerNoInv extends Container implements IContainerWithTileEntity {

    private final AbstractMachineEntity tile;

    public ContainerNoInv(AbstractMachineEntity tile) {
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }
}
