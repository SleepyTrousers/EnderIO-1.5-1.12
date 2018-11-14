package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.render.util.LimitedTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface IConduitTexture {

  @Nonnull
  TextureSupplier getTexture();

  @Nonnull
  Vector4f getUv();

  default @Nonnull TextureAtlasSprite getSprite() {
    return getTexture().get(TextureAtlasSprite.class);
  }

  default @Nonnull TextureAtlasSprite getCroppedSprite() {
    return new LimitedTextureAtlasSprite(getSprite(), getUv());
  }

}
