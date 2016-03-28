package crazypants.enderio.machine.light;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;

public class BlockLightNode extends BlockEio<TileLightNode> {

  public static BlockLightNode create() {
    BlockLightNode result = new BlockLightNode();
    result.init();
    return result;
  }

  public static final PropertyBool ACTIVE = PropertyBool.create("active");
  
  public BlockLightNode() {
    super(ModObject.blockLightNode.getUnlocalisedName(), TileLightNode.class, Material.air);
    setCreativeTab(null);
    setBlockBounds(0, 0, 0, 0, 0, 0);
    setTickRandomly(true);
    setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
  }
  
  @Override
  public BlockState createBlockState() {
    return new BlockState(this, ACTIVE);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(ACTIVE).booleanValue() ? 1 : 0;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {    
    return getDefaultState().withProperty(ACTIVE, meta > 0);
  }
  
  @Override
  public boolean isFullCube() {
    return false;
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
    if(bs.getBlock() != this) {
      return 0;
    }
    return bs.getValue(ACTIVE) ? 15 : 0;
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

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

}
