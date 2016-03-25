package crazypants.enderio.render;

public interface ICacheKey {
  ICacheKey addCacheKey(Object addlCacheKey);

  long getCacheKey();
}
