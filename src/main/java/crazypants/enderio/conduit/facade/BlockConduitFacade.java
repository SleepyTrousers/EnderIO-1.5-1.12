package crazypants.enderio.conduit.facade;

import java.util.List;

import javax.annotation.Nullable;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockConduitFacade extends BlockEio<TileEntityPaintedBlock> implements IPaintable.IBlockPaintableBlock, ISmartRenderAwareBlock {

  public static BlockConduitFacade create() {
    BlockConduitFacade result = new BlockConduitFacade();
    result.init();
    return result;
  }

  private BlockConduitFacade() {
    super(ModObject.blockConduitFacade.getUnlocalisedName(), TileEntityPaintedBlock.class, new Material(MapColor.STONE));
    setSoundType(SoundType.STONE);
    setCreativeTab(EnderIOTab.tabEnderIO);
    initDefaultState();
  }
  
  

  @Override
  protected ItemBlock createItemBlock() {
    return new ItemConduitFacade(this, name);
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
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, EnumFacadeType.TYPE });
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
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, RenderMappers.FRONT_MAPPER_NO_IO);
      blockStateWrapper.addCacheKey(0);
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = getTileEntitySafe(world, pos);
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
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER_NO_IO;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
