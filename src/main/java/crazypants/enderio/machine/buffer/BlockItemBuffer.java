package crazypants.enderio.machine.buffer;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs tab, List list) {
    for (BufferType type : BufferType.values()) {
      list.add(new ItemStack(item, 1, type.ordinal()));
    }
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
        System.out.println("BlockItemBuffer.placeBlockAt: " + t);
        buffer.setHasInventory(t.hasInventory);
        buffer.setHasPower(t.hasPower);
        buffer.setCreative(t.isCreative);
      }
    }
    return true;
  }
}
