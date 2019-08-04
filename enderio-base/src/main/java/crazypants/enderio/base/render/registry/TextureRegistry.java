package crazypants.enderio.base.render.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import net.minecraft.util.ResourceLocation;

public final class TextureRegistry {

  public interface TextureSupplier {
    @Nonnull
    <T extends Object> T get(@Nonnull Class<T> clazz);
  }

  private static final @Nonnull Map<ResourceLocation, Object> sprites = new HashMap<>();
  private static boolean locked = false;
  private static Function<ResourceLocation, Object> fallback = key -> {
    throw new UnsupportedOperationException("TextureRegistry: Texture is accessed too early (or on a dedicated server): " + key);
  };

  protected static @Nonnull Map<ResourceLocation, Object> getSprites() {
    return sprites;
  }

  protected static void lock(Function<ResourceLocation, Object> fallbackIn) {
    locked = true;
    TextureRegistry.fallback = fallbackIn;
  }

  public static @Nonnull TextureSupplier registerTexture(final @Nonnull String location, boolean prependDomain) {
    final ResourceLocation key = new ResourceLocation(prependDomain ? EnderIO.DOMAIN + ":" + location : location);
    Log.debug("registerTexture ", key);
    if (locked) {
      throw new UnsupportedOperationException("TextureRegistry: Texture is registered too late: " + key);
    }
    if (!sprites.containsKey(key)) {
      sprites.put(key, null);
    }
    return new TextureSupplier() {
      @Override
      @Nonnull
      public <T> T get(@Nonnull Class<T> clazz) {
        return first(clazz, key, sprites::get, fallback, unused -> {
          throw new UnsupportedOperationException("TextureRegistry can only supply TextureAtlasSprite");
        });
      }
    };
  }

  @SuppressWarnings({ "unchecked", "null" })
  @SafeVarargs
  private final static @Nonnull <P> P first(@Nonnull Class<P> clazz, @Nonnull ResourceLocation key, @Nonnull Function<ResourceLocation, ?/* super P */>... o) {
    for (Function<ResourceLocation, ?> on : o) {
      Object p = on.apply(key);
      if (clazz.isInstance(p)) {
        return (P) p;
      }
    }
    throw new NullPointerException();
  }

  public static @Nonnull TextureSupplier registerTexture(final @Nonnull String location) {
    return registerTexture(location, true);
  }

}
