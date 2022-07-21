package crazypants.enderio.machine.invpanel;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemInventoryPanel extends ItemBlock {

    public BlockItemInventoryPanel(Block b) {
        super(b);
    }

    @Override
    public boolean placeBlockAt(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ,
            int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileInventoryPanel) {
            TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
            teInvPanel.setFacing((short) side);
            teInvPanel.readFromItemStack(stack);
            if (!world.isRemote) {
                world.markBlockForUpdate(x, y, z);
            }
        }
        return true;
    }
}
