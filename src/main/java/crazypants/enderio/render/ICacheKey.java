package crazypants.enderio.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICacheKey {

  @Nonnull
  ICacheKey addCacheKey(@Nullable Object addlCacheKey);

  long getCacheKey();

}
