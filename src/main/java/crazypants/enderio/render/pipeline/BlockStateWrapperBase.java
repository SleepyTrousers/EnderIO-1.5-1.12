package crazypants.enderio.render.pipeline;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import crazypants.enderio.Log;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStateWrapperBase extends CacheKey implements IBlockStateWrapper {

  private final static Cache<Pair<Block, Long>, QuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500).<Pair<Block, Long>, QuadCollector> build();

  private final @Nonnull Block block;
  private final @Nonnull IBlockState state;
  private final @Nonnull IBlockAccess world;
  private final @Nonnull BlockPos pos;
  private final @Nonnull IRenderMapper.IBlockRenderMapper renderMapper;
  private boolean doCaching = false;

  private IBakedModel model = null;

  protected static Cache<Pair<Block, Long>, QuadCollector> getCache() {
    return cache;
  }

  public BlockStateWrapperBase(IBlockState state, IBlockAccess world, BlockPos pos, IRenderMapper.IBlockRenderMapper renderMapper) {
    this.state = notnull(state);
    this.block = notnull(state.getBlock());
    this.world = notnull(world);
    this.pos = notnull(pos);
    this.renderMapper = renderMapper != null ? renderMapper : nullRenderMapper;
  }

  @Nonnull
  private static <X> X notnull(@Nullable X x) {
    if (x == null) {
      throw new NullPointerException();
    }
    return x;
  }

  protected BlockStateWrapperBase(BlockStateWrapperBase parent, IBlockState state) {
    this.block = parent.block;
    this.state = notnull(state);
    this.world = parent.world;
    this.pos = parent.pos;
    this.renderMapper = parent.renderMapper;
    this.doCaching = parent.doCaching;
    this.model = parent.model;
  }

  public static void invalidate() {
    getCache().invalidateAll();
  }

  @Override
  public Collection<IProperty<?>> getPropertyNames() {
    return state.getPropertyNames();
  }

  @Override
  public <T extends Comparable<T>> T getValue(IProperty<T> property) {
    return state.getValue(property);
  }

  @Override
  public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
    return new BlockStateWrapperBase(this, state.withProperty(property, value));
  }

  @Override
  public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
    return new BlockStateWrapperBase(this, state.cycleProperty(property));
  }

  @Override
  public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
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
      return ((ChunkCache) world).func_190300_a(pos, EnumCreateEntityType.CHECK);
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
    doCaching = true;
    return this;
  }

  @Override
  public void bakeModel() {
    long start = crazypants.util.Profiler.client.start();
    QuadCollector quads = null;
    IBlockState rawPaintSource = null;
    IBlockState paintSource = null;
    String cacheResult;

    if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !YetaUtil.shouldHeldItemHideFacades())) {
      rawPaintSource = PaintWrangler.getActualBlockState(world, pos, ((IBlockPaintableBlock) block).getPaintSource(state, world, pos));
      paintSource = PaintWrangler.getDynamicBlockState(world, pos, rawPaintSource);
      if (doCaching && paintSource != null && paintSource.isOpaqueCube() && !YetaUtil.shouldHeldItemHideFacades()) {
        // assume that a solid paint makes it so the Blocks/TEs state doesn't matter for rendering
        resetCacheKeyInternal();
      }
    }

    if (doCaching) {
      if (paintSource != null) {
        addCacheKeyInternal(paintSource);
      }
      quads = getCache().getIfPresent(Pair.of(block, getCacheKey()));
      cacheResult = quads == null ? "miss" : "hit";
    } else {
      cacheResult = "not cachable";
    }

    if (quads == null) {
      quads = new QuadCollector();
      if (!bakePaintLayer(quads, rawPaintSource, paintSource)) {
        bakeBlockLayer(quads);
        paintSource = null;
      } else if (renderMapper instanceof IRenderMapper.IBlockRenderMapper.IRenderLayerAware.IPaintAware) {
        bakeBlockLayer(quads);
      }

      if (doCaching) {
        getCache().put(Pair.of(block, getCacheKey()), quads);
      }
    }

    model = new CollectedQuadBakedBlockModel(quads.combine(OverlayHolder.getOverlay(renderMapper.mapOverlayLayer(this, world, pos, paintSource != null))));

    crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (bake, cache=" + cacheResult + ")");
  }

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
    }
  }

  protected boolean bakePaintLayer(QuadCollector quads, IBlockState rawPaintSource, IBlockState paintSource) {
    if (paintSource != null) {
      BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      boolean rendered = true;
      for (BlockRenderLayer layer : quads.getBlockLayers()) {
        if (paintSource.getBlock().canRenderInLayer(paintSource, layer)) {
          ForgeHooksClient.setRenderLayer(layer);
          rendered = rendered && PaintWrangler.wrangleBakedModel(world, pos, rawPaintSource, paintSource, quads);
        }
      }
      ForgeHooksClient.setRenderLayer(oldRenderLayer);
      return rendered;
    } else {
      return false;
    }
  }

  protected IBakedModel getModel() {
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
    public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
      return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
        QuadCollector quadCollector) {
      return null;
    }

  };

  // And here comes the stupid "we pipe most calls to Block though BlockState" stuff

  @Override
  public Material getMaterial() {
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
  public int getLightOpacity(IBlockAccess world, BlockPos pos) {
    return state.getLightOpacity(world, pos);
  }

  @SuppressWarnings("deprecation")  
  @Override
  public int getLightValue() {
    return state.getLightValue();
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    return state.getLightValue(world, pos);
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
  public MapColor getMapColor() {
    return state.getMapColor();
  }

  @Override
  public IBlockState withRotation(Rotation rot) {
    return state.withRotation(rot);
  }

  @Override
  public IBlockState withMirror(Mirror mirrorIn) {
    return state.withMirror(mirrorIn);
  }

  @Override
  public boolean isFullCube() {
    return state.isFullCube();
  }

  @Override
  public EnumBlockRenderType getRenderType() {
    return state.getRenderType();
  }

  @Override
  public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos) {
    return state.getPackedLightmapCoords(source, pos);
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
  public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return state.getWeakPower(blockAccess, pos, side);
  }

  @Override
  public boolean hasComparatorInputOverride() {
    return state.hasComparatorInputOverride();
  }

  @Override
  public int getComparatorInputOverride(World worldIn, BlockPos pos) {
    return state.getComparatorInputOverride(worldIn, pos);
  }

  @Override
  public float getBlockHardness(World worldIn, BlockPos pos) {
    return state.getBlockHardness(worldIn, pos);
  }

  @Override
  public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos) {
    return state.getPlayerRelativeBlockHardness(player, worldIn, pos);
  }

  @Override
  public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return state.getStrongPower(blockAccess, pos, side);
  }

  @Override
  public EnumPushReaction getMobilityFlag() {
    return state.getMobilityFlag();
  }

  @Override
  public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos) {
    return state.getActualState(blockAccess, pos);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos) {
    return state.getCollisionBoundingBox(worldIn, pos);
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing) {
    return state.shouldSideBeRendered(blockAccess, pos, facing);
  }

  @Override
  public boolean isOpaqueCube() {
    return state.isOpaqueCube();
  }

  @Override
  public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
    return state.getSelectedBoundingBox(worldIn, pos);
  }

  @Override
  public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_, @Nullable Entity p_185908_5_) {
    state.addCollisionBoxToList(worldIn, pos, p_185908_3_, p_185908_4_, p_185908_5_);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos) {
    return state.getBoundingBox(blockAccess, pos);
  }

  @Override
  public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
    return state.collisionRayTrace(worldIn, pos, start, end);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean isFullyOpaque() {
    return state.isFullyOpaque();
  }

  @Override
  public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return state.doesSideBlockRendering(world, pos, side);
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return state.isSideSolid(world, pos, side);
  }

  @Override
  public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {    
    return state.onBlockEventReceived(worldIn, pos, id, param);
  }

  @Override
  public void neighborChanged(World worldIn, BlockPos pos, Block p_189546_3_) {
    state.neighborChanged(worldIn, pos, p_189546_3_);    
  }

  @Override
  public boolean func_189884_a(Entity p_189884_1_) {
    return state.func_189884_a(p_189884_1_);
  }

}
