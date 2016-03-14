package crazypants.enderio.material.fusedQuartz;

import java.util.List;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFusedQuartz extends BlockEio<TileEntityPaintedBlock> implements ISmartRenderAwareBlock {
  
  @SideOnly(Side.CLIENT)
  private static FusedQuartzRenderMapper RENDER_MAPPER;

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.unlocalisedName, TileEntityPaintedBlock.class, ItemFusedQuartz.class, Material.glass);
    setStepSound(Block.soundTypeGlass);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, FusedQuartzType.KIND });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(FusedQuartzType.KIND, FusedQuartzType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return FusedQuartzType.getMetaFromType(state.getValue(FusedQuartzType.KIND));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return getRenderMapper().getExtendedState(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public FusedQuartzRenderMapper getRenderMapper() {
    if (RENDER_MAPPER == null) {
      RENDER_MAPPER = new FusedQuartzRenderMapper();
    }
    return RENDER_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.SOLID;
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity par1Entity, Explosion explosion) {   
    if (world.getBlockState(pos).getValue(FusedQuartzType.KIND).isBlastResistant()) {
      return 2000;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getLightOpacity(IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    if(bs.getBlock() != this) {
      return super.getLightOpacity(world, pos);
    }
    return bs.getValue(FusedQuartzType.KIND).getLightOpacity();
  }
  
  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    return world.getBlockState(pos).getValue(FusedQuartzType.KIND).isEnlightened() ? 15 : super.getLightValue(world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (FusedQuartzType fqt : FusedQuartzType.values()) {
      par3List.add(new ItemStack(par1, 1, fqt.ordinal()));
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState otherState = world.getBlockState(pos);
    Block otherBlock = otherState.getBlock();
    if (otherBlock == this) {
      BlockPos here = pos.offset(side.getOpposite());
      IBlockState ourState = world.getBlockState(here);
      return !ourState.getValue(FusedQuartzType.KIND).connectTo(otherState.getValue(FusedQuartzType.KIND));
    }
    return true;
  }

  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {  
    return true;
  }

  
  @Override
  public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {  
    if(side == EnumFacing.UP) { //stop drips
      return false;
    }
    return true;
  }

  @Override
  public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Override
  protected boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return false;
  }

  @Override
  public boolean isFullCube() {
    return false;
  }

}
