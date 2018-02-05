package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockPaintedSlab extends BlockSlab implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper {

  public static class BlockPaintedHalfSlab extends BlockPaintedSlab {
    public BlockPaintedHalfSlab(@Nonnull IModObject modObject, @Nonnull Material material, @Nonnull SoundType sound) {
      super(modObject, material, null);
      setSoundType(sound);
    }

    @Override
    public boolean isDouble() {
      return false;
    }

  }

  public static class BlockPaintedDoubleSlab extends BlockPaintedSlab {
    public BlockPaintedDoubleSlab(@Nonnull IModObject modObject, @Nonnull Material material, Block halfVariant, @Nonnull SoundType sound) {
      super(modObject, material, halfVariant);
      setSoundType(sound);
    }

    @Override
    public boolean isDouble() {
      return true;
    }

  }

  private final @Nonnull Block halfVariant;

  public BlockPaintedSlab(@Nonnull IModObject modObject, @Nonnull Material material, @Nullable Block halfVariant) {
    super(material);
    IBlockState iblockstate = this.blockState.getBaseState();

    if (!this.isDouble()) {
      iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
    }

    this.setDefaultState(iblockstate);
    Prep.setNoCreativeTab(this);
    modObject.apply(this);
    this.halfVariant = halfVariant != null ? halfVariant : this;
    useNeighborBrightness = true;
  }

  protected void init(@Nonnull IModObject modObject) {
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("slab_lo", new ResourceLocation("minecraft", "block/half_slab_stone"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("slab_hi", new ResourceLocation("minecraft", "block/upper_slab_stone"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public @Nonnull Comparable<?> getTypeForItem(@Nonnull ItemStack stack) {
    throw new NotImplementedException("This method is only used by ItemSlab for vanilla slabs.");
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TileEntityTwicePaintedBlock();
  }

  @Override
  public @Nonnull String getUnlocalizedName(int meta) {
    return getUnlocalizedName();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    setPaintSource(state, world, pos, PaintUtil.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(bs, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(@Nonnull World worldIn, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public @Nonnull List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    boolean first = true;
    for (ItemStack drop : drops) {
      if (first || !isDouble()) {
        PaintUtil.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource(state, world, pos));
        first = false;
      } else {
        PaintUtil.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource2(state, world, pos));
      }
    }
    return drops;
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState bs, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(bs, target, world, pos, player);
    if (!isDouble()) {
      PaintUtil.setSourceBlock(pickBlock, getPaintSource(bs, world, pos));
    } else {
      if ((target.hitVec.y - (int) target.hitVec.y) > 0.5) {
        PaintUtil.setSourceBlock(pickBlock, getPaintSource2(bs, world, pos));
      } else {
        PaintUtil.setSourceBlock(pickBlock, getPaintSource(bs, world, pos));
      }
    }
    return pickBlock;
  }

  @Override
  public @Nonnull Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
    return Item.getItemFromBlock(halfVariant);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ItemStack getItem(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return new ItemStack(Item.getItemFromBlock(halfVariant));
  }

  @Override
  public @Nonnull IProperty<?> getVariantProperty() {
    throw new RuntimeException("no such property");
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    if (this.isDouble()) {
      return getDefaultState();
    } else {
      return getDefaultState().withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
    }
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    if (this.isDouble()) {
      return 0;
    } else {
      return state.getValue(HALF) == EnumBlockHalf.TOP ? 8 : 0;
    }
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return this.isDouble() ? new BlockStateContainer(this, new IProperty[] {}) : new BlockStateContainer(this, new IProperty[] { HALF });
  }

  public void setPaintSource2(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityTwicePaintedBlock) {
      ((TileEntityTwicePaintedBlock) te).setPaintSource2(paintSource);
    }
  }

  public IBlockState getPaintSource2(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    if (isDouble()) {
      TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
      if (te instanceof TileEntityTwicePaintedBlock) {
        return ((TileEntityTwicePaintedBlock) te).getPaintSource2();
      }
      return null;
    } else {
      return getPaintSource(state, world, pos);
    }
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
    if (isDouble()) {
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(getPaintSource2(state, world, pos));
    } else {
      blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(HALF));
    }
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return this;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey.addCacheKey(getPaintSource(block, stack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBakedModel> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    IBlockState stdOverlay = ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "slab_lo", paintSource, null);
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "slab_lo", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION3);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    list.add(model2);
    return list;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    IBlockState bs = world.getBlockState(pos);
    return bs.getMaterial() == Material.WOOD ? 20 : super.getFlammability(world, pos, face);
  }

  @Override
  public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return world.getBlockState(pos).getMaterial() == Material.WOOD ? 5 : super.getFireSpreadSpeed(world, pos, face);
  }

  @Override
  public boolean doesSideBlockRendering(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(@Nonnull IBlockState ourBlockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos here, @Nonnull EnumFacing side) {
    BlockPos there = here.offset(side);
    IBlockState blockState2 = worldIn.getBlockState(there);
    Block block2 = blockState2.getBlock();
    if (block2 instanceof BlockPaintedSlab) {
      BlockPaintedSlab otherBlock = (BlockPaintedSlab) block2;
      if (side == EnumFacing.UP) {
        if (!isDouble() && ourBlockState.getValue(HALF) == EnumBlockHalf.BOTTOM) {
          return true;
        }
        if (!otherBlock.isDouble() && blockState2.getValue(HALF) == EnumBlockHalf.TOP) {
          return true;
        }
        IBlockState ourPaint = isDouble() ? getPaintSource2(ourBlockState, worldIn, here) : getPaintSource(ourBlockState, worldIn, here);
        IBlockState otherPaint = getPaintSource(blockState2, worldIn, there);
        return ourPaint != otherPaint;
      } else if (side == EnumFacing.DOWN) {
        if (!isDouble() && ourBlockState.getValue(HALF) == EnumBlockHalf.TOP) {
          return true;
        }
        if (!otherBlock.isDouble() && blockState2.getValue(HALF) == EnumBlockHalf.BOTTOM) {
          return true;
        }
        IBlockState ourPaint = getPaintSource(ourBlockState, worldIn, here);
        IBlockState otherPaint = otherBlock.isDouble() ? getPaintSource2(blockState2, worldIn, there) : getPaintSource(blockState2, worldIn, there);
        return ourPaint != otherPaint;
      } else {
        if (isDouble()) {
          if (!otherBlock.isDouble()) {
            return true;
          } else {
            return getPaintSource(ourBlockState, worldIn, here) != getPaintSource(blockState2, worldIn, there)
                || getPaintSource2(ourBlockState, worldIn, here) != getPaintSource2(blockState2, worldIn, there);
          }
        } else {
          if (!otherBlock.isDouble() && blockState2.getValue(HALF) != ourBlockState.getValue(HALF)) {
            return true;
          }
          IBlockState paintSource = getPaintSource(ourBlockState, worldIn, here);
          if (otherBlock.isDouble() && ourBlockState.getValue(HALF) == EnumBlockHalf.TOP) {
            return paintSource != getPaintSource2(blockState2, worldIn, there);
          }
          return paintSource != getPaintSource(blockState2, worldIn, there);
        }
      }
    }
    return super.shouldSideBeRendered(ourBlockState, worldIn, here, side);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == EnderIOTab.tabNoTab) {
      super.getSubBlocks(tab, list);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    for (BlockSlab.EnumBlockHalf half : BlockSlab.EnumBlockHalf.values()) {
      if (isDouble() || half == state.getValue(HALF)) {
        boolean isTop = half == BlockSlab.EnumBlockHalf.TOP;
        IBlockState paintSource = isTop ? getPaintSource2(state, world, pos) : getPaintSource(state, world, pos);
        if (blockLayer == null || PaintUtil.canRenderInLayer(paintSource, blockLayer)) {
          quadCollector.addFriendlybakedModel(blockLayer, PaintRegistry.getModel(IBakedModel.class, isTop ? "slab_hi" : "slab_lo", paintSource, null),
              paintSource, MathHelper.getPositionRandom(pos));
        }
      }
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}