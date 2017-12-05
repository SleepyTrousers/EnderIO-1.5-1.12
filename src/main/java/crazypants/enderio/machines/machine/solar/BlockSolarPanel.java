package crazypants.enderio.machines.machine.solar;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSolarPanel extends BlockEio<TileEntitySolarPanel> implements IResourceTooltipProvider, ISmartRenderAwareBlock {

  public static BlockSolarPanel create(@Nonnull IModObject modObject) {
    BlockSolarPanel result = new BlockSolarPanel(modObject);
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 2.5f / 16f;

  private BlockSolarPanel(@Nonnull IModObject modObject) {
    super(modObject, TileEntitySolarPanel.class);
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO).withProperty(SolarType.KIND,
        SolarType.SIMPLE));
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemSolarPanel(this));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, SolarType.KIND });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(SolarType.KIND, SolarType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return SolarType.getMetaFromType(state.getValue(SolarType.KIND));
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    SolarBlockRenderMapper renderMapper = new SolarBlockRenderMapper(state, world, pos);
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
    blockStateWrapper.addCacheKey(state.getValue(SolarType.KIND));
    blockStateWrapper.addCacheKey(renderMapper);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return SolarItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  public int damageDropped(@Nonnull IBlockState bs) {
    return getMetaFromState(bs);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  // @Override
  // public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity
  // entityIn) {
  // setBlockBoundsBasedOnState(worldIn, pos);
  // super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
  // }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean doesSideBlockRendering(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return face == EnumFacing.DOWN;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    NNList.of(SolarType.class).apply(new Callback<SolarType>() {
      @Override
      public void apply(@Nonnull SolarType solarType) {
        list.add(new ItemStack(itemIn, 1, SolarType.getMetaFromType(solarType)));
      }
    });
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    if (state.getValue(SolarType.KIND) == SolarType.VIBRANT) {
      TileEntity te = getTileEntity(world, pos);
      if (te instanceof TileEntitySolarPanel) {
        TileEntitySolarPanel solar = (TileEntitySolarPanel) te;
        if (solar.canSeeSun() && solar.calculateLightRatio() / 3 > rand.nextFloat()) {
          double d0 = pos.getX() + 0.5D + (Math.random() - 0.5D) * 0.5D;
          double d1 = pos.getY() + BLOCK_HEIGHT;
          double d2 = pos.getZ() + 0.5D + (Math.random() - 0.5D) * 0.5D;
          world.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0x47 / 255d, 0x9f / 255d, 0xa3 / 255d);
        }
      }
    }
  }

}
