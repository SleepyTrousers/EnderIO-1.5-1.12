package crazypants.enderio.render.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.render.ICacheKey;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class CacheKey implements ICacheKey {

  private long cacheKey = 0;

  @Override
  public @Nonnull ICacheKey addCacheKey(@Nullable Object addlCacheKey) {
    addCacheKeyInternal(addlCacheKey != null ? addlCacheKey : 0);
    return this;
  }

  protected void addCacheKeyInternal(@Nonnull Object addlCacheKey) {
    if (addlCacheKey instanceof IBlockState) {
      // block states have no hashCode(), so we get the identity instead
      cacheKey = ((cacheKey << 7) | (cacheKey >>> 57)) ^ Block.BLOCK_STATE_IDS.get((IBlockState) addlCacheKey);
    } else {
      cacheKey = ((cacheKey << 7) | (cacheKey >>> 57)) ^ addlCacheKey.hashCode();
    }
  }

  @Override
  public long getCacheKey() {
    return cacheKey;
  }

  protected void resetCacheKeyInternal() {
    cacheKey = 0;
  }

}
