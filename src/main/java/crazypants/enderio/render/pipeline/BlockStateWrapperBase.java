package crazypants.enderio.render.pipeline;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import crazypants.enderio.Log;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;

public class BlockStateWrapperBase implements IBlockStateWrapper {

  private final static Cache<Pair<Block, Long>, IBakedModel> cache = CacheBuilder.newBuilder().maximumSize(500).<Pair<Block, Long>, IBakedModel> build();

  private final Block block;
  private final IBlockState state;
  private final IBlockAccess world;
  private final BlockPos pos;
  private long cacheKey = 0;
  private boolean doCaching;

  private IBakedModel model = null;

  public BlockStateWrapperBase(IBlockState state, IBlockAccess world, BlockPos pos) {
    this(state, world, pos, false);
  }

  public BlockStateWrapperBase(IBlockState state, IBlockAccess world, BlockPos pos, boolean doCaching) {
    this.block = state.getBlock();
    this.state = state;
    this.world = world;
    this.pos = pos;
    this.doCaching = doCaching;
  }

  protected BlockStateWrapperBase(BlockStateWrapperBase parent, IBlockState state) {
    this.block = parent.block;
    this.state = state;
    this.world = parent.world;
    this.pos = parent.pos;
    this.cacheKey = parent.cacheKey;
    this.doCaching = parent.doCaching;
    this.model = parent.model;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Collection<IProperty> getPropertyNames() {
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

  @SuppressWarnings("rawtypes")
  @Override
  public ImmutableMap<IProperty, Comparable> getProperties() {
    return state.getProperties();
  }

  @Override
  public Block getBlock() {
    return state.getBlock();
  }

  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public TileEntity getTileEntity() {
    return world.getTileEntity(pos);
  }

  @Override
  public IBlockAccess getWorld() {
    return world;
  }

  @Override
  public IBlockState getState() {
    return state;
  }

  public long getCacheKey() {
    return cacheKey;
  }

  @Override
  public IBlockStateWrapper addCacheKey(Object addlCacheKey) {
    addCacheKeyInternal(addlCacheKey);
    doCaching = true;
    return this;
  }

  protected void addCacheKeyInternal(Object addlCacheKey) {
    cacheKey = cacheKey ^ addlCacheKey.hashCode();
  }

  @Override
  public void bakeModel() {
    long start = crazypants.util.Profiler.client.start();

    boolean renderPaint = block instanceof IBlockPaintableBlock;

    if (block instanceof IWrenchHideablePaint) {
      renderPaint = renderPaint && !YetaUtil.shouldHeldItemHideFacades();
    }

    if (doCaching) {
      if (renderPaint) {
        IBlockState paintSource = ((IBlockPaintableBlock) block).getPaintSource(state, world, pos);
        if (paintSource != null) {
          addCacheKeyInternal(paintSource.getBlock());
          addCacheKeyInternal(paintSource.getBlock().getMetaFromState(paintSource));
        }
      }

      Pair<Block, Long> cachePair = Pair.of(block, cacheKey);
      model = cache.getIfPresent(cachePair);
      if (model != null) {
        crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (cached)");
        return;
      }
    }

    QuadCollector quads = new QuadCollector();

    IRenderMapper renderMapper = block instanceof ISmartRenderAwareBlock ? ((ISmartRenderAwareBlock) block).getRenderMapper() : null;

    bakeOverlayLayer(quads, renderMapper);

    if (!renderPaint || !bakePaintLayer(quads)) {
      bakeBlockLayer(quads, renderMapper);
    }

    model = new CollectedQuadBakedBlockModel(quads);

    if (doCaching) {
      Pair<Block, Long> cachePair = Pair.of(block, cacheKey);
      cache.put(cachePair, model);
    }

    crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName());
  }

  protected void bakeOverlayLayer(QuadCollector quads, IRenderMapper renderMapper) {
    if (renderMapper != null) {
      EnumWorldBlockLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      for (EnumWorldBlockLayer layer : quads.getBlockLayers()) {
        ForgeHooksClient.setRenderLayer(layer);
        if (renderMapper instanceof IRenderMapper.IRenderLayerAware || block.getBlockLayer() == MinecraftForgeClient.getRenderLayer()) {
          quads.addFriendlyBlockStates(layer, renderMapper.mapOverlayLayer(this, world, pos));
        }
      }
      ForgeHooksClient.setRenderLayer(oldRenderLayer);
    }
  }

  protected void bakeBlockLayer(QuadCollector quads, IRenderMapper renderMapper) {
    if (renderMapper != null) {
      EnumWorldBlockLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      for (EnumWorldBlockLayer layer : quads.getBlockLayers()) {
        ForgeHooksClient.setRenderLayer(layer);
        if (renderMapper instanceof IRenderMapper.IRenderLayerAware || block.getBlockLayer() == MinecraftForgeClient.getRenderLayer()) {
          Pair<List<IBlockState>, List<IBakedModel>> blockRender = renderMapper.mapBlockRender(this, world, pos);
          if (blockRender != null) {
            quads.addFriendlyBlockStates(layer, blockRender.getLeft());
          }
        }
      }
      ForgeHooksClient.setRenderLayer(oldRenderLayer);
    }
  }

  protected boolean bakePaintLayer(QuadCollector quads) {
    IBlockState paintSource = ((IPaintable) block).getPaintSource(state, world, pos);
    if (paintSource != null) {
      EnumWorldBlockLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
      boolean rendered = true;
      for (EnumWorldBlockLayer layer : quads.getBlockLayers()) {
        if (paintSource.getBlock().canRenderInLayer(layer)) {
          ForgeHooksClient.setRenderLayer(layer);
          rendered = rendered && PaintWrangler.wrangleBakedModel(world, pos, paintSource, quads);
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
        // Log.warn(block + " doesn't bake its model!");
      } else {
        Log.warn(block + "s model won't bake!");
      }
    }
    return model;
  }

}
