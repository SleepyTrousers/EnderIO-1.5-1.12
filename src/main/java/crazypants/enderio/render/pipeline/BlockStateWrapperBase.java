package crazypants.enderio.render.pipeline;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

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

public class BlockStateWrapperBase implements IBlockStateWrapper {

  private final static Cache<Pair<Block, Long>, QuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500).<Pair<Block, Long>, QuadCollector> build();

  private final @Nonnull Block block;
  private final @Nonnull IBlockState state;
  private final @Nonnull IBlockAccess world;
  private final @Nonnull BlockPos pos;
  private final @Nonnull IRenderMapper renderMapper;
  private long cacheKey = 0;
  private boolean doCaching = false;

  private IBakedModel model = null;

  public BlockStateWrapperBase(IBlockState state, IBlockAccess world, BlockPos pos, IRenderMapper renderMapper) {
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
  public @Nonnull Block getBlock() {
    return block;
  }

  @Override
  public @Nonnull BlockPos getPos() {
    return pos;
  }

  @Override
  public @Nullable TileEntity getTileEntity() {
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

  public long getCacheKey() {
    return cacheKey;
  }

  @Override
  public IBlockStateWrapper addCacheKey(@Nullable Object addlCacheKey) {
    addCacheKeyInternal(addlCacheKey != null ? addlCacheKey : 0);
    doCaching = true;
    return this;
  }

  protected void addCacheKeyInternal(@Nonnull Object addlCacheKey) {
    if (addlCacheKey instanceof IBlockState) {
      // block states have no hashCode(), so we'd get the identity based default hash
      cacheKey = ((cacheKey << 7) | (cacheKey >>> 57)) ^ addlCacheKey.toString().hashCode();
    } else {
      cacheKey = ((cacheKey << 7) | (cacheKey >>> 57)) ^ addlCacheKey.hashCode();
    }
  }

  @Override
  public void bakeModel() {
    long start = crazypants.util.Profiler.client.start();
    QuadCollector quads = null;
    IBlockState paintSource = null;
    String cacheResult;

    if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !YetaUtil.shouldHeldItemHideFacades())) {
      paintSource = PaintWrangler.getDynamicBlockState(world, pos, ((IBlockPaintableBlock) block).getPaintSource(state, world, pos));
    }

    if (doCaching) {
      if (paintSource != null) {
        addCacheKeyInternal(paintSource);
      }
      quads = cache.getIfPresent(Pair.of(block, cacheKey));
      cacheResult = quads == null ? "miss" : "hit";
    } else {
      cacheResult = "not cachable";
    }

    if (quads == null) {
      quads = new QuadCollector();
      if (!bakePaintLayer(quads, paintSource)) {
        bakeBlockLayer(quads);
        paintSource = null;
      } else if (renderMapper instanceof IRenderMapper.IPaintAware) {
        bakeBlockLayer(quads);
      }

      if (doCaching) {
        cache.put(Pair.of(block, cacheKey), quads);
      }
    }

    model = new CollectedQuadBakedBlockModel(quads.combine(OverlayHolder.getOverlay(renderMapper.mapOverlayLayer(this, world, pos, paintSource != null))));

    crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (bake, cache=" + cacheResult + ")");
  }

  protected void bakeBlockLayer(QuadCollector quads) {
    if (renderMapper instanceof IRenderMapper.IRenderLayerAware) {
      for (EnumWorldBlockLayer layer : quads.getBlockLayers()) {
        quads.addFriendlyBlockStates(layer, renderMapper.mapBlockRender(this, world, pos, layer, quads));
      }
    } else {
      EnumWorldBlockLayer layer = block.getBlockLayer();
      quads.addFriendlyBlockStates(layer, renderMapper.mapBlockRender(this, world, pos, layer, quads));
    }
  }

  protected boolean bakePaintLayer(QuadCollector quads, IBlockState paintSource) {
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
        Log.warn(block + " doesn't bake its model!");
      } else {
        Log.warn(block + "'s model won't bake!");
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      }
    }
    return model;
  }

  private static final @Nonnull IRenderMapper nullRenderMapper = new IRenderMapper() {

    @Override
    public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
      return null;
    }

    @Override
    public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
      return null;
    }

    @Override
    public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
      return null;
    }

    @Override
    public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
        QuadCollector quadCollector) {
      return null;
    }

  };

}
