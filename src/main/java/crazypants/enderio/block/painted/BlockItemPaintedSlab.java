package crazypants.enderio.block.painted;

import crazypants.enderio.paint.PainterUtil2;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemPaintedSlab extends ItemBlock {
  private final BlockPaintedSlab singleSlab;
  private final BlockPaintedSlab doubleSlab;

  public BlockItemPaintedSlab(BlockPaintedSlab singleSlab, BlockPaintedSlab doubleSlab, String regName) {
    super(singleSlab);
    this.singleSlab = singleSlab;
    this.doubleSlab = doubleSlab;
    this.setMaxDamage(0);
    this.setHasSubtypes(false);
    setRegistryName(regName);
  }

  @Override
  public int getMetadata(int damage) {
    return 0;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return this.singleSlab.getUnlocalizedName(stack.getMetadata());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
    BlockPos blockpos = pos;
    IBlockState iblockstate = worldIn.getBlockState(pos);

    if (iblockstate.getBlock() == this.singleSlab) {
      boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

      if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag)) {
        return true;
      }
    }

    pos = pos.offset(side);
    IBlockState iblockstate1 = worldIn.getBlockState(pos);
    return iblockstate1.getBlock() == this.singleSlab ? true : super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
  }

  @Override
  public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {    
    if (stack.stackSize == 0) {
      return EnumActionResult.FAIL;
    } else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
      return EnumActionResult.FAIL;
    } else {
      IBlockState iblockstate = worldIn.getBlockState(pos);

      if (iblockstate.getBlock() == this.singleSlab) {
        BlockSlab.EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(BlockSlab.HALF);

        if ((side == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN
            && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP)) {
          tryPlace(stack, worldIn, pos);
          return EnumActionResult.SUCCESS;
        }
      }

      return this.tryPlace(stack, worldIn, pos.offset(side)) ? EnumActionResult.SUCCESS : super.onItemUse(stack, playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
    }
  }

  private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos) {
    IBlockState iblockstate = worldIn.getBlockState(pos);

    if (iblockstate.getBlock() == this.singleSlab) {
      BlockSlab.EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
      IBlockState iblockstate1 = this.doubleSlab.getDefaultState();
      IBlockState paintSource = singleSlab.getPaintSource(iblockstate, worldIn, pos);
      IBlockState paintSource1 = PainterUtil2.getSourceBlock(stack);

      
      if (worldIn.checkNoEntityCollision(iblockstate1.getCollisionBoundingBox(worldIn, pos)) && worldIn.setBlockState(pos, iblockstate1, 3)) {
        if (blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM) {
          doubleSlab.setPaintSource(iblockstate1, worldIn, pos, paintSource);
          doubleSlab.setPaintSource2(iblockstate1, worldIn, pos, paintSource1);
        } else {
          doubleSlab.setPaintSource(iblockstate1, worldIn, pos, paintSource1);
          doubleSlab.setPaintSource2(iblockstate1, worldIn, pos, paintSource);
        }
        worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.doubleSlab.getSoundType().getPlaceSound(), SoundCategory.BLOCKS,
            (this.doubleSlab.getSoundType().getVolume() + 1.0F) / 2.0F, this.doubleSlab.getSoundType().getPitch() * 0.8F, true);
        --stack.stackSize;
      }

      return true;
    }

    return false;
  }

}
