package crazypants.enderio.machine.buffer;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;

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

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(PainterUtil2.getTooltTipText(stack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (block instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) block).getPaintSource(block, stack);
      if (paintSource != null) {
        final ItemStack paintStack = new ItemStack(paintSource.getBlock(), 1, paintSource.getBlock().getMetaFromState(paintSource));
        return paintStack.getItem().getColorFromItemStack(paintStack, renderPass);

        // faster but less compatible:
        // return paintSource.getBlock().getRenderColor(paintSource);
      }
    }
    return super.getColorFromItemStack(stack, renderPass);
  }

}
