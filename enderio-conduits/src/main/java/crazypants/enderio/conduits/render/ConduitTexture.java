package crazypants.enderio.conduits.render;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;

public class ConduitTexture implements IConduitTexture {

  public static final @Nonnull Vector4f FULL = new Vector4f(0, 0, 1, 1);
  private static final @Nonnull Vector4f CORE = new Vector4f(2 / 16f, 14 / 16f, 14 / 16f, 2 / 16f);
  private static final @Nonnull Vector4f CORE0 = new Vector4f(0 / 16f, 8 / 16f, 8 / 16f, 0 / 16f);
  private static final @Nonnull Vector4f CORE1 = new Vector4f(8 / 16f, 8 / 16f, 16 / 16f, 0 / 16f);
  private static final @Nonnull Vector4f CORE2 = new Vector4f(0 / 16f, 16 / 16f, 8 / 16f, 8 / 16f);
  private static final @Nonnull Vector4f CORE3 = new Vector4f(8 / 16f, 16 / 16f, 16 / 16f, 8 / 16f);

  public static @Nonnull Vector4f core(int idx) {
    switch (idx) {
    case 0:
      return ConduitTexture.CORE0;
    case 1:
      return ConduitTexture.CORE1;
    case 2:
      return ConduitTexture.CORE2;
    case 3:
      return ConduitTexture.CORE3;
    default:
      return ConduitTexture.CORE;
    }
  }

  public static @Nonnull Vector4f core() {
    return core(-1);
  }

  public static @Nonnull Vector4f arm(int idx) {
    if (idx < 0 || idx > 3) {
      throw new RuntimeException("Invalid vOffset: " + idx);
    }
    return new Vector4f(0 / 16f, (4 * idx) / 16f, 13 / 16f, (4 * (1 + idx)) / 16f);
  }

  private final @Nonnull TextureSupplier texture;
  private final @Nonnull Vector4f uv;

  public ConduitTexture(@Nonnull TextureSupplier texture, @Nonnull Vector4f uv) {
    this.texture = texture;
    this.uv = uv;
  }

  public ConduitTexture(@Nonnull TextureSupplier texture) {
    this(texture, FULL);
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
