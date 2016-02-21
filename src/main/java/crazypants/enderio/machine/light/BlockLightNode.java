package crazypants.enderio.machine.light;

import java.util.Random;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLightNode extends BlockEio<TileLightNode> {

  public static BlockLightNode create() {
    BlockLightNode result = new BlockLightNode();
    result.init();
    return result;
  }

  public BlockLightNode() {
    super(ModObject.blockLightNode.unlocalisedName, TileLightNode.class, Material.air);
    setCreativeTab(null);
    setBlockBounds(0, 0, 0, 0, 0, 0);
    setTickRandomly(true);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isReplaceable(World worldIn, BlockPos pos) {
    return true;
  }

  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return false;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    return null;
  }

  @Override
  public int getRenderType() {  
    return -1;
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.onBlockRemoved();
    }
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
  IBlockState bs = world.getBlockState(pos);  
    return bs.getBlock().getMetaFromState(bs)  > 0 ? 15 : 0;
  }

  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {  
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighbourChanged();
    }
  }

  
  
  @Override
  public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {    
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.checkParent();
    }
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    blockIcon = iIconRegister.registerIcon("enderio:blockElectricLightFace");
//  }

  @Override
  public int quantityDropped(Random p_149745_1_) {
    return 0;
  }

}
