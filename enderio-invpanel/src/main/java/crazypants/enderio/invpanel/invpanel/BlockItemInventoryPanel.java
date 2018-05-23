package crazypants.enderio.invpanel.invpanel;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemInventoryPanel extends ItemBlock {

  public BlockItemInventoryPanel(Block b) {
    super(b);
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {
    if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      teInvPanel.setFacing(side);
      teInvPanel.readCustomNBT(stack);
      if(!world.isRemote) {        
        IBlockState bs = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, bs, bs, 3);
      }
    }
    return true;
  }

}
