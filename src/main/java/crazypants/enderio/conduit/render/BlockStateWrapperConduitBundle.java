package crazypants.enderio.conduit.render;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockStateWrapperConduitBundle extends BlockStateWrapperBase {

  private final static Cache<ConduitCacheKey, QuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(10, TimeUnit.MINUTES)
      .<ConduitCacheKey, QuadCollector> build();

  public BlockStateWrapperConduitBundle(IBlockState state, IBlockAccess world, BlockPos pos, IBlockRenderMapper renderMapper) {
    super(state, world, pos, renderMapper);
  }

  public BlockStateWrapperConduitBundle(BlockStateWrapperBase parent, IBlockState state) {
    super(parent, state);
  }

  private final @Nonnull ConduitCacheKey cachekey = new ConduitCacheKey();

  @Override
  protected void putIntoCache(QuadCollector quads) {
    cache.put(cachekey, quads);
  }

  @Override
  protected QuadCollector getFromCache() {
    return cache.getIfPresent(cachekey);
  }

  public static void invalidate() {
    cache.invalidateAll();
  }

  @Override
  protected void addCacheKeyInternal(@Nonnull Object addlCacheKey) {
    super.addCacheKeyInternal(addlCacheKey);
    if (addlCacheKey instanceof IConduitComponent) {
      ((IConduitComponent) addlCacheKey).hashCodeForModelCaching(this, cachekey);
    } else if (addlCacheKey instanceof IBlockState) {      
      cachekey.add(Block.BLOCK_STATE_IDS.get((IBlockState) addlCacheKey));
    } else {
      cachekey.add(addlCacheKey);
    }
  }

  @Override
  protected void resetCacheKeyInternal() {
    super.resetCacheKeyInternal();
    cachekey.reset();
  }

  public static class ConduitCacheKey {
    private int idx = 0, hashCode = 1;
    private int[] hashCodes = new int[16];

    public void reset() {
      idx = 0;
      hashCode = 1;
    }

    public void add(Object o) {
      add(o.hashCode());
    }

    public void add(int i) {
      assert hashCodes != null;
      if (idx == hashCodes.length) {
        hashCodes = Arrays.copyOf(hashCodes, hashCodes.length * 2);
      }
      hashCodes[idx++] = i;
      hashCode = 31 * hashCode + i;
    }

    public void addBoolean(Map<EnumFacing, Boolean> o) {
      assert EnumFacing.values().length <= 1 + 2 + 4;
      int i = 0;
      for (EnumFacing face : EnumFacing.values()) {
        Boolean b = o.get(face);
        i = (i << 1) | (b != null && b.booleanValue() ? 1 : 0);
      }
      add(i);
    }

    public <T extends Enum<?>> void addEnum(Map<EnumFacing, T> o) {
      int i = 0;
      for (EnumFacing face : EnumFacing.values()) {
        final T value = o.get(face);
        assert value == null || value.ordinal() < 1 + 2 + 4 + 8 + 16 : value.getClass();
        i = (i << 5) | (value == null ? 1 + 2 + 4 + 8 + 16 : value.ordinal());
      }
      add(i);
    }

    public void add(Set<EnumFacing> o1, Set<EnumFacing> o2, Map<EnumFacing, ConnectionMode> o3) {
      assert EnumFacing.values().length <= 1 + 2 + 4;
      int i = 0;
      for (EnumFacing face : EnumFacing.values()) {
        i = (i << 1) | (o1.contains(face) ? 1 : 0);
        i = (i << 1) | (o2.contains(face) ? 1 : 0);
        i = (i << 3) | (o3.containsKey(face) ? o3.get(face).ordinal() : 1 + 2 + 4);
      }
      add(i);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ConduitCacheKey && ((ConduitCacheKey) obj).idx == idx) {
        assert hashCodes != null;
        assert ((ConduitCacheKey) obj).hashCodes != null;
        for (int i = 0; i < idx; i++) {
          if (hashCodes[i] != ((ConduitCacheKey) obj).hashCodes[i]) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    @Override
    public String toString() {
      return "ConduitCacheKey [idx=" + idx + ", hashCode=" + hashCode + ", hashCodes=" + Arrays.toString(hashCodes) + "]";
    }

  }
}
