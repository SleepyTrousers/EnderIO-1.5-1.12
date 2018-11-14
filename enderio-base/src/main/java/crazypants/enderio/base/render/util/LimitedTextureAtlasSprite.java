package crazypants.enderio.base.render.util;

import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class LimitedTextureAtlasSprite extends TextureAtlasSprite {

  private final float minU, maxU, minV, maxV;

  public LimitedTextureAtlasSprite(TextureAtlasSprite parent, Vector4f uv) {
    super(parent.getIconName());
    copyFrom(parent);

    minU = super.getInterpolatedU(uv.x * 16);
    maxU = super.getInterpolatedU(uv.z * 16);
    minV = super.getInterpolatedV(uv.y * 16);
    maxV = super.getInterpolatedV(uv.w * 16);

  }

  @Override
  public float getMinU() {
    return this.minU;
  }

  @Override
  public float getMaxU() {
    return this.maxU;
  }

  @Override
  public float getInterpolatedU(double u) {
    float f = this.maxU - this.minU;
    return this.minU + f * (float) u / 16.0F;
  }

  @Override
  public float getUnInterpolatedU(float u) {
    float f = this.maxU - this.minU;
    return (u - this.minU) / f * 16.0F;
  }

  @Override
  public float getMinV() {
    return this.minV;
  }

  @Override
  public float getMaxV() {
    return this.maxV;
  }

  @Override
  public float getInterpolatedV(double v) {
    float f = this.maxV - this.minV;
    return this.minV + f * (float) v / 16.0F;
  }

  @Override
  public float getUnInterpolatedV(float p_188536_1_) {
    float f = this.maxV - this.minV;
    return (p_188536_1_ - this.minV) / f * 16.0F;
  }
}
