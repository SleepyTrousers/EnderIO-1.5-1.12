package crazypants.enderio.machine.buffer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockItemBuffer extends ItemBlock {

  public BlockItemBuffer(Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }
  
  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return BufferType.values()[stack.getItemDamage()].getUnlocalizedName();
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {
    super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    
    if(newState.getBlock() == block) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileBuffer) {
        TileBuffer buffer = ((TileBuffer) te);        
        BufferType t = BufferType.values()[block.getMetaFromState(newState)];
        buffer.setHasInventory(t.hasInventory);
        buffer.setHasPower(t.hasPower);
        buffer.setCreative(t.isCreative);
      }
    }
    return true;
  }
}
