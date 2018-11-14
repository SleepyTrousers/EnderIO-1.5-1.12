package crazypants.enderio.conduits.render;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ConduitTextureWrapper implements IConduitTexture {

  private static final @Nonnull Vector4f FULL = new Vector4f(0, 0, 1, 1);

  private final @Nonnull TextureAtlasSprite texture;
  private final @Nonnull Vector4f uv;

  public ConduitTextureWrapper(@Nonnull TextureAtlasSprite texture, @Nonnull Vector4f uv) {
    this.texture = texture;
    this.uv = uv;
  }

  public ConduitTextureWrapper(@Nonnull TextureAtlasSprite texture, int vOffset) {
    this(texture, new Vector4f(0 / 16f, (4 * checkOffset(vOffset)) / 16f, 13 / 16f, (4 * (1 + vOffset)) / 16f));
  }

  public ConduitTextureWrapper(@Nonnull TextureAtlasSprite texture) {
    this(texture, FULL);
  }

  private static int checkOffset(int vOffset) {
    if (vOffset < 0 || vOffset > 3) {
      throw new RuntimeException("Invalid vOffset: " + vOffset);
    }
    return vOffset;
  }

  @Override
  public @Nonnull TextureSupplier getTexture() {
    return new TextureSupplier() {
      @SuppressWarnings("unchecked")
      @Override
      @Nonnull
      public <T> T get(@Nonnull Class<T> clazz) {
        return (T) texture;
      }
    };
  }

  @Override
  public @Nonnull Vector4f getUv() {
    return uv;
  }

  @Override
  @Nonnull
  public TextureAtlasSprite getSprite() {
    return texture;
  }
}
