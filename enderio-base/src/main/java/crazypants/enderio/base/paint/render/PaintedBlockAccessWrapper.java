package crazypants.enderio.base.paint.render;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.IBlockAccessWrapper;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.util.FacadeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class PaintedBlockAccessWrapper extends IBlockAccessWrapper {

  private static final ConcurrentHashMap<Block, Boolean> teBlackList = new ConcurrentHashMap<Block, Boolean>();

  private static final ThreadLocal<PaintedBlockAccessWrapper> factory = new ThreadLocal<PaintedBlockAccessWrapper>() {
    @Override
    protected PaintedBlockAccessWrapper initialValue() {
      return new PaintedBlockAccessWrapper();
    }
  };

  public static @Nonnull PaintedBlockAccessWrapper instance(@Nonnull IBlockAccess ba) {
    return factory.get().setWorld(ba);
  }

  @SuppressWarnings("null")
  private PaintedBlockAccessWrapper() {
    super(null);
  }

  public @Nonnull PaintedBlockAccessWrapper setWorld(@Nonnull IBlockAccess ba) {
    wrapped = ba;
    return this;
  }

  @SuppressWarnings("null")
  public void free() {
    wrapped = null;
  }

  @Override
  public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
    IBlockState paintSource = getPaintSource(wrapped.getBlockState(pos), pos);
    if (paintSource != null) {
      return paintSource.getBlock().isSideSolid(paintSource, this, pos, side);
    }
    return wrapped.isSideSolid(pos, side, _default);
  }

  @Override
  public TileEntity getTileEntity(@Nonnull BlockPos pos) {
    IBlockState state = wrapped.getBlockState(pos);
    IBlockState paintSource = getPaintSource(state, pos);
    if (paintSource != null && paintSource != state) {
      return createTileEntity(paintSource, pos.toImmutable());
    }
    return wrapped.getTileEntity(pos);
  }

  public TileEntity getRealTileEntity(@Nonnull BlockPos pos) {
    return wrapped.getTileEntity(pos);
  }

  @Override
  public @Nonnull IBlockState getBlockState(@Nonnull BlockPos pos) {
    IBlockState state = wrapped.getBlockState(pos);
    return NullHelper.first(getPaintSource(state, pos), state);
  }

  private IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull BlockPos pos) {
    if (state.getBlock() instanceof IPaintable.IBlockPaintableBlock) {
      return ((IPaintable.IBlockPaintableBlock) state.getBlock()).getPaintSource(state, wrapped, pos);
    }
    return FacadeUtil.instance.getFacade(state, wrapped, pos, null);
  }

  private final Map<Block, TileEntity> teCache = new HashMap<Block, TileEntity>();

  @SuppressWarnings("null")
  private TileEntity createTileEntity(IBlockState state, @Nonnull BlockPos pos) {
    Block block = state.getBlock();
    if (!block.hasTileEntity(state) || teBlackList.containsKey(block)) {
      return null;
    }
    if (teCache.containsKey(block)) {
      try {
        TileEntity tileEntity = teCache.get(block);
        tileEntity.setPos(pos);
        return tileEntity;
      } catch (Throwable t) {
        teCache.remove(block);
      }
    }
    try {
      TileEntity tileEntity = block.createTileEntity(null, state);
      tileEntity.setPos(pos);
      teCache.put(block, tileEntity);
      return tileEntity;
    } catch (Throwable t) {
      teBlackList.put(block, true);
    }
    return null;
  }

}
