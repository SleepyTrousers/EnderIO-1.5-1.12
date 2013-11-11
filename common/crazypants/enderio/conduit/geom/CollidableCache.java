package crazypants.enderio.conduit.geom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;

public class CollidableCache {

  public static final CollidableCache instance = new CollidableCache();

  private final Map<CacheKey, Collection<CollidableComponent>> cache = new HashMap<CollidableCache.CacheKey, Collection<CollidableComponent>>();

  public CacheKey createKey(Class<? extends IConduit> baseType, Offset offset, ForgeDirection dir, boolean isStub) {
    return new CacheKey(baseType, offset, dir, isStub);
  }

  public Collection<CollidableComponent> getCollidables(CacheKey key, IConduit conduit) {
    Collection<CollidableComponent> result = cache.get(key);
    if(result == null) {
      result = conduit.createCollidables(key);
      cache.put(key, result);
    }
    return result;
  }

  public static class CacheKey {

    public final Class<? extends IConduit> baseType;
    public final String className; // used to generate reliable equals /
                                   // hashcode
    public final Offset offset;
    public final ForgeDirection dir;
    public final boolean isStub;

    public CacheKey(Class<? extends IConduit> baseType, Offset offset, ForgeDirection dir, boolean isStub) {
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
      result = prime * result + ((className == null) ? 0 : className.hashCode());
      result = prime * result + ((dir == null) ? 0 : dir.hashCode());
      result = prime * result + (isStub ? 1231 : 1237);
      result = prime * result + ((offset == null) ? 0 : offset.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      CacheKey other = (CacheKey) obj;
      if(className == null) {
        if(other.className != null) {
          return false;
        }
      } else if(!className.equals(other.className)) {
        return false;
      }
      if(dir != other.dir) {
        return false;
      }
      if(isStub != other.isStub) {
        return false;
      }
      if(offset != other.offset) {
        return false;
      }
      return true;
    }

  }

}
