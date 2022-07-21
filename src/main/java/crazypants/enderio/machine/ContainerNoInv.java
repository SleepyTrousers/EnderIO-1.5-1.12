package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerNoInv extends Container {

    private IInventory inv;

    public ContainerNoInv(IInventory inv) {
        super();
        this.inv = inv;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return inv.isUseableByPlayer(player);
    }
}
