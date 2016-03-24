package crazypants.enderio.conduit.facade;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;

public class BlockConduitFacade extends BlockEio<TileEntityPaintedBlock> implements IPaintable.IBlockPaintableBlock, ISmartRenderAwareBlock {

  public static BlockConduitFacade create() {
    BlockConduitFacade result = new BlockConduitFacade();
    result.init();
    return result;
  }

  private BlockConduitFacade() {
    super(ModObject.blockConduitFacade.unlocalisedName, TileEntityPaintedBlock.class, ItemConduitFacade.class, new Material(MapColor.stoneColor));
    setStepSound(Block.soundTypeStone);
    setCreativeTab(EnderIOTab.tabEnderIO);
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO)
        .withProperty(EnumFacadeType.TYPE, EnumFacadeType.BASIC));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumRenderMode.RENDER, EnumFacadeType.TYPE });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(EnumFacadeType.TYPE, EnumFacadeType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return EnumFacadeType.getMetaFromType(state.getValue(EnumFacadeType.TYPE));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  public int damageDropped(IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
    for (EnumFacadeType type : EnumFacadeType.values()) {
      list.add(new ItemStack(item, 1, type.ordinal()));
    }
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, getRenderMapper());
      blockStateWrapper.addCacheKey(0);
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return getPaintSource(getDefaultState(), world, pos);
  }

  @Override
  public IRenderMapper getRenderMapper() {
    return RenderMappers.FRONT_MAPPER_NO_IO;
  }

}
