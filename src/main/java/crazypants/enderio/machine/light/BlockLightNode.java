package crazypants.enderio.machine.light;

import java.util.List;
import java.util.Random;

import crazypants.enderio.BlockEio;
import crazypants.enderio.machine.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLightNode extends BlockEio<TileLightNode> {

  public static BlockLightNode create() {
    BlockLightNode result = new BlockLightNode();
    result.init();
    return result;
  }

  public static final PropertyBool ACTIVE = PropertyBool.create("active");
  
  public BlockLightNode() {
    super(MachineObject.blockLightNode.getUnlocalisedName(), TileLightNode.class, Material.AIR);
    setCreativeTab(null);
    setTickRandomly(true);
    setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
  }
  
  
  
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return new AxisAlignedBB(0,0,0,0,0,0);
  }

  @Override
  public BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, ACTIVE);
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
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {    
    return false;
  }

  @Override
  public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
    return true;
  }

  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return false;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
    
    return null;
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState bs) {  
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.onBlockRemoved();
    }
  }

  @Override
  public int getLightValue(IBlockState bs, IBlockAccess world, BlockPos pos) {
    if(bs.getBlock() != this) {
      return 0;
    }
    return bs.getValue(ACTIVE) ? 15 : 0;
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {  
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
