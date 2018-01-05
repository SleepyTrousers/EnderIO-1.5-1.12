package crazypants.enderio.base.render.pipeline;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.base.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.base.paint.YetaUtil.YetaDisplayMode;
import crazypants.enderio.base.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.model.CollectedQuadBakedBlockModel;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.Profiler;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStateWrapperBase extends CacheKey implements IBlockStateWrapper {

  private final static Cache<Pair<Block, Long>, QuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500).<Pair<Block, Long>, QuadCollector> build();

  protected final @Nonnull Block block;
  protected final @Nonnull IBlockState state;
  protected final @Nonnull IBlockAccess world;
  protected final @Nonnull BlockPos pos;
  protected final @Nonnull IRenderMapper.IBlockRenderMapper renderMapper;
  protected boolean doCaching = false;

  protected IBakedModel model = null;

  @Nonnull
  private final YetaDisplayMode yetaDisplayMode = YetaUtil.getYetaDisplayMode();

  public BlockStateWrapperBase(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, IRenderMapper.IBlockRenderMapper renderMapper) {
    this.state = state;
    this.block = state.getBlock();
    this.world = world;
    this.pos = pos;
    this.renderMapper = renderMapper != null ? renderMapper : nullRenderMapper;
  }

  protected BlockStateWrapperBase(@Nonnull BlockStateWrapperBase parent, @Nonnull IBlockState state) {
    this.block = parent.block;
    this.state = state;
    this.world = parent.world;
    this.pos = parent.pos;
    this.renderMapper = parent.renderMapper;
    this.doCaching = parent.doCaching;
    this.model = parent.model;
  }

  protected void putIntoCache(@Nonnull QuadCollector quads) {
    cache.put(Pair.of(block, getCacheKey()), quads);
  }

  protected QuadCollector getFromCache() {
    return cache.getIfPresent(Pair.of(block, getCacheKey()));
  }

  public static void invalidate() {
    cache.invalidateAll();
  }

  @Override
  public @Nonnull <T extends Comparable<T>> T getValue(@Nonnull IProperty<T> property) {
    return state.getValue(property);
  }

  @Override
  public @Nonnull <T extends Comparable<T>, V extends T> IBlockState withProperty(@Nonnull IProperty<T> property, @Nonnull V value) {
    return new BlockStateWrapperBase(this, state.withProperty(property, value));
  }

  @Override
  public @Nonnull <T extends Comparable<T>> IBlockState cycleProperty(@Nonnull IProperty<T> property) {
    return new BlockStateWrapperBase(this, state.cycleProperty(property));
  }

  @Override
  public @Nonnull ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
    return state.getProperties();
  }

  @Override
  public @Nonnull Block getBlock() {
    return block;
  }

  @Override
  public @Nonnull BlockPos getPos() {
    return pos;
  }

  @Override
  public @Nullable TileEntity getTileEntity() {
    if (world instanceof ChunkCache) {
      return ((ChunkCache) world).getTileEntity(pos, EnumCreateEntityType.CHECK);
    }
    return world.getTileEntity(pos);
  }

  @Override
  public @Nonnull IBlockAccess getWorld() {
    return world;
  }

  @Override
  public @Nonnull IBlockState getState() {
    return state;
  }

  @Override
  public @Nonnull IBlockStateWrapper addCacheKey(@Nullable Object addlCacheKey) {
    super.addCacheKey(addlCacheKey);
    doCaching = !(world instanceof PaintedBlockAccessWrapper);
    return this;
  }

  @Override
  public void bakeModel() {
    long start = Profiler.instance.start();
    QuadCollector quads = null;
    QuadCollector overlayQuads = null;
    @Nonnull
    QuadCollector paintQuads = new QuadCollector();
    boolean hasPaintRendered = false;
    String cacheResult;

    if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !getYetaDisplayMode().isHideFacades())) {
      hasPaintRendered = PaintWrangler.wrangleBakedModel(world, pos, ((IBlockPaintableBlock) block).getPaintSource(state, world, pos), paintQuads);
    }

    boolean fromCache = doCaching && MinecraftForgeClient.getRenderLayer() != null;
    if (!hasPaintRendered || renderMapper instanceof IRenderMapper.IBlockRenderMapper.IRenderLayerAware.IPaintAware) {
      if (fromCache) {
        quads = getFromCache();
        cacheResult = quads == null ? "miss" : "hit";
      } else {
        cacheResult = "not cachable";
      }
      if (quads == null) {
        quads = new QuadCollector();
        bakeBlockLayer(quads);
        if (fromCache) {
          putIntoCache(quads);
        }
      }
    } else {
      cacheResult = "paint only";
    }

    overlayQuads = OverlayHolder.getOverlay(renderMapper.mapOverlayLayer(this, world, pos, hasPaintRendered));

    model = new CollectedQuadBakedBlockModel(paintQuads.combine(overlayQuads).combine(quads));

    Profiler.instance.stop(start, state.getBlock().getLocalizedName() + " (bake, cache=" + cacheResult + ")");
  }

  private static final BlockRenderLayer BREAKING = null;

  protected void bakeBlockLayer(QuadCollector quads) {
    if (renderMapper == nullRenderMapper) {
      IBakedModel missingModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      for (BlockRenderLayer layer : quads.getBlockLayers()) {
        quads.addUnfriendlybakedModel(layer, missingModel, state, 0);
      }
    } else if (renderMapper instanceof IRenderMapper.IBlockRenderMapper.IRenderLayerAware) {
      for (BlockRenderLayer layer : quads.getBlockLayers()) {
        quads.addFriendlyBlockStates(layer, renderMapper.mapBlockRender(this, world, pos, layer, quads));
      }
    } else {
      BlockRenderLayer layer = block.getBlockLayer();
      quads.addFriendlyBlockStates(layer, renderMapper.mapBlockRender(this, world, pos, layer, quads));
      quads.addFriendlyBlockStates(BREAKING, renderMapper.mapBlockRender(this, world, pos, BREAKING, quads));
    }
  }

  public IBakedModel getModel() {
    if (model == null) {
      bakeModel();
      if (model != null) {
        Log.warn(block + " doesn't bake its model!");
      } else {
        Log.warn(block + "'s model won't bake!");
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      }
    }
    return model;
  }

  private static final @Nonnull IRenderMapper.IBlockRenderMapper nullRenderMapper = new IRenderMapper.IBlockRenderMapper() {

    @Override
    @SideOnly(Side.CLIENT)
    public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
        boolean isPainted) {
      return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
        @Nonnull QuadCollector quadCollector) {
      return null;
    }

  };

  @Override
  public @Nonnull YetaDisplayMode getYetaDisplayMode() {
    return yetaDisplayMode;
  }

  // And here comes the stupid "we pipe most calls to Block though BlockState" stuff

  @Override
  public @Nonnull Material getMaterial() {
    return state.getMaterial();
  }

  @Override
  public boolean isFullBlock() {
    return state.isFullBlock();
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getLightOpacity() {
    return state.getLightOpacity();
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockAccess world1, @Nonnull BlockPos pos1) {
    return state.getLightOpacity(world1, pos1);
  }

  @SuppressWarnings("deprecation")  
  @Override
  public int getLightValue() {
    return state.getLightValue();
  }

  @Override
  public int getLightValue(@Nonnull IBlockAccess world1, @Nonnull BlockPos pos1) {
    return state.getLightValue(world1, pos1);
  }

  @Override
  public boolean isTranslucent() {
    return state.isTranslucent();
  }

  @Override
  public boolean useNeighborBrightness() {
    return state.useNeighborBrightness();
  }

  @Override
  public @Nonnull MapColor getMapColor() {
    return state.getMapColor();
  }

  @Override
  public @Nonnull IBlockState withRotation(@Nonnull Rotation rot) {
    return state.withRotation(rot);
  }

  @Override
  public @Nonnull IBlockState withMirror(@Nonnull Mirror mirrorIn) {
    return state.withMirror(mirrorIn);
  }

  @Override
  public boolean isFullCube() {
    return state.isFullCube();
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType() {
    return state.getRenderType();
  }

  @Override
  public int getPackedLightmapCoords(@Nonnull IBlockAccess source, @Nonnull BlockPos pos1) {
    return state.getPackedLightmapCoords(source, pos1);
  }

  @Override
  public float getAmbientOcclusionLightValue() {
    return state.getAmbientOcclusionLightValue();
  }

  @Override
  public boolean isBlockNormalCube() {
    return state.isBlockNormalCube();
  }

  @Override
  public boolean isNormalCube() {
    return state.isNormalCube();
  }

  @Override
  public boolean canProvidePower() {
    return state.canProvidePower();
  }

  @Override
  public int getWeakPower(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos1, @Nonnull EnumFacing side) {
    return state.getWeakPower(blockAccess, pos1, side);
  }

  @Override
  public boolean hasComparatorInputOverride() {
    return state.hasComparatorInputOverride();
  }

  @Override
  public int getComparatorInputOverride(@Nonnull World worldIn, @Nonnull BlockPos pos1) {
    return state.getComparatorInputOverride(worldIn, pos1);
  }

  @Override
  public float getBlockHardness(@Nonnull World worldIn, @Nonnull BlockPos pos1) {
    return state.getBlockHardness(worldIn, pos1);
  }

  @Override
  public float getPlayerRelativeBlockHardness(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos1) {
    return state.getPlayerRelativeBlockHardness(player, worldIn, pos1);
  }

  @Override
  public int getStrongPower(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos1, @Nonnull EnumFacing side) {
    return state.getStrongPower(blockAccess, pos1, side);
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag() {
    return state.getMobilityFlag();
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos1) {
    return state.getActualState(blockAccess, pos1);
  }

  @Override
  public boolean shouldSideBeRendered(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos1, @Nonnull EnumFacing facing) {
    return state.shouldSideBeRendered(blockAccess, pos1, facing);
  }

  @Override
  public boolean isOpaqueCube() {
    return state.isOpaqueCube();
  }

  @Override
  public @Nonnull AxisAlignedBB getSelectedBoundingBox(@Nonnull World worldIn, @Nonnull BlockPos pos1) {
    return state.getSelectedBoundingBox(worldIn, pos1);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos1) {
    return state.getBoundingBox(blockAccess, pos1);
  }

  @Override
  public @Nonnull RayTraceResult collisionRayTrace(@Nonnull World worldIn, @Nonnull BlockPos pos1, @Nonnull Vec3d start, @Nonnull Vec3d end) {
    return state.collisionRayTrace(worldIn, pos1, start, end);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isFullyOpaque() {
    return state.isFullyOpaque();
  }

  @Override
  public boolean doesSideBlockRendering(@Nonnull IBlockAccess world1, @Nonnull BlockPos pos1, @Nonnull EnumFacing side) {
    return state.doesSideBlockRendering(world1, pos1, side);
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockAccess world1, @Nonnull BlockPos pos1, @Nonnull EnumFacing side) {
    return state.isSideSolid(world1, pos1, side);
  }

  @Override
  public boolean onBlockEventReceived(@Nonnull World worldIn, @Nonnull BlockPos pos1, int id, int param) {
    return state.onBlockEventReceived(worldIn, pos1, id, param);
  }

  @Override
  public @Nonnull Collection<IProperty<?>> getPropertyKeys() {
    return state.getPropertyKeys();
  }

  @Override
  public void neighborChanged(@Nonnull World worldIn, @Nonnull BlockPos pos1, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    state.neighborChanged(worldIn, pos1, blockIn, fromPos);
  }

  @Override
  public boolean canEntitySpawn(@Nonnull Entity entityIn) {
    return state.canEntitySpawn(entityIn);
  }

  @Override
  public boolean hasCustomBreakingProgress() {
    return state.hasCustomBreakingProgress();
  }

  @Override
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos1) {
    return state.getCollisionBoundingBox(worldIn, pos1);
  }

  @Override
  public void addCollisionBoxToList(@Nonnull World worldIn, @Nonnull BlockPos pos1, @Nonnull AxisAlignedBB entityBox,
      @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185908_6_) {
    state.addCollisionBoxToList(worldIn, pos1, entityBox, collidingBoxes, entityIn, p_185908_6_);
  }

  @Override
  public @Nonnull Vec3d getOffset(@Nonnull IBlockAccess access, @Nonnull BlockPos pos1) {
    return state.getOffset(access, pos1);
  }

  @Override
  public boolean causesSuffocation() {
    return state.causesSuffocation();
  }

}
