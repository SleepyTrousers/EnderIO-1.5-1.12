package crazypants.enderio.conduit.render;

import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import crazypants.enderio.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.pipeline.QuadCollector;

public class BlockStateWrapperConduitBundle extends BlockStateWrapperBase {

  private final static Cache<Pair<Block, Long>, QuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(10, TimeUnit.MINUTES)
      .<Pair<Block, Long>, QuadCollector> build();

  public BlockStateWrapperConduitBundle(IBlockState state, IBlockAccess world, BlockPos pos, IBlockRenderMapper renderMapper) {
    super(state, world, pos, renderMapper);
  }

  public BlockStateWrapperConduitBundle(BlockStateWrapperBase parent, IBlockState state) {
    super(parent, state);
  }

  protected static Cache<Pair<Block, Long>, QuadCollector> getCache() {
    return cache;
  }

  public static void invalidate() {
    getCache().invalidateAll();
  }

}
