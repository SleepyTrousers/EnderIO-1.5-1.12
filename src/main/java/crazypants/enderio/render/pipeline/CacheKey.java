package crazypants.enderio.render.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import crazypants.enderio.render.ICacheKey;

public class CacheKey implements ICacheKey {

  private long cacheKey = 0;

  @Override
  public ICacheKey addCacheKey(@Nullable Object addlCacheKey) {
    addCacheKeyInternal(addlCacheKey != null ? addlCacheKey : 0);
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
  public long getCacheKey() {
    return cacheKey;
  }

}
