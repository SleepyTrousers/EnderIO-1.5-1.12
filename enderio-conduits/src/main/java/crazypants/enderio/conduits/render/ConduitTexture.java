package crazypants.enderio.conduits.render;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;

public class ConduitTexture implements IConduitTexture {

  public static final @Nonnull Vector4f FULL = new Vector4f(0, 0, 1, 1);
  public static final @Nonnull Vector4f CORE = new Vector4f(2 / 16f, 14 / 16f, 14 / 16f, 2 / 16f);

  private final @Nonnull TextureSupplier texture;
  private final @Nonnull Vector4f uv;

  public ConduitTexture(@Nonnull TextureSupplier texture, @Nonnull Vector4f uv) {
    this.texture = texture;
    this.uv = uv;
  }

  public ConduitTexture(@Nonnull TextureSupplier texture, int vOffset) {
    this(texture, new Vector4f(0 / 16f, (4 * checkOffset(vOffset)) / 16f, 13 / 16f, (4 * (1 + vOffset)) / 16f));
  }

  public ConduitTexture(@Nonnull TextureSupplier texture) {
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
    return texture;
  }

  @Override
  public @Nonnull Vector4f getUv() {
    return uv;
  }

}
