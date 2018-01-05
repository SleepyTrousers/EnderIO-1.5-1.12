package crazypants.enderio.base.conduit.geom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public class CollidableCache {

  public static final CollidableCache instance = new CollidableCache();

  private final Map<CacheKey, Collection<CollidableComponent>> cache = new HashMap<CollidableCache.CacheKey, Collection<CollidableComponent>>();

  public CacheKey createKey(@Nonnull Class<? extends IConduit> baseType, @Nonnull Offset offset, @Nonnull EnumFacing dir, boolean isStub) {
    return new CacheKey(baseType, offset, dir, isStub);
  }

  public Collection<CollidableComponent> getCollidables(@Nonnull CacheKey key, @Nonnull IConduit conduit) {
    Collection<CollidableComponent> result = cache.get(key);
    if (result == null) {
      result = conduit.createCollidables(key);
      cache.put(key, result);
    }
    return result;
  }

  public static class CacheKey {

    public final @Nonnull Class<? extends IConduit> baseType;
    public final @Nonnull String className; // used to generate reliable equals / hashcode
    public final @Nonnull Offset offset;
    public final @Nonnull EnumFacing dir;
    public final boolean isStub;

    public CacheKey(@Nonnull Class<? extends IConduit> baseType, @Nonnull Offset offset, @Nonnull EnumFacing dir, boolean isStub) {
      this.baseType = baseType;
      className = baseType.getCanonicalName();
      this.offset = offset;
      this.dir = dir;
      this.isStub = isStub;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + className.hashCode();
      result = prime * result + dir.hashCode();
      result = prime * result + (isStub ? 1231 : 1237);
      result = prime * result + offset.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      CacheKey other = (CacheKey) obj;
      if (!className.equals(other.className)) {
        return false;
      }
      if (dir != other.dir) {
        return false;
      }
      if (isStub != other.isStub) {
        return false;
      }
      if (offset != other.offset) {
        return false;
      }
      return true;
    }

  }

}
