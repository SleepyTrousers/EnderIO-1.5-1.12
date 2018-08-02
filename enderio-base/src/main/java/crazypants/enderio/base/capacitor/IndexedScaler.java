package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.Scaler;

/**
 * The IndexedScaler is s scaler that interpolates linearly between a number of points. Those points are at fixed intervals on the x-axis (one every 'scale'
 * units).
 *
 * The points are at (scale * n; keyValues[n]) for n in 0...keyValues.length-1
 *
 * Again, plotting it out is helpful.
 */
public class IndexedScaler implements Scaler {
  private final float scale;
  private final float[] keyValues;

  public IndexedScaler(float scale, float... keyValues) {
    this.scale = scale;
    this.keyValues = keyValues;
  }

  public @Nonnull String store() {
    StringBuffer sb = new StringBuffer();
    sb.append("idx(");
    sb.append(scale);
    sb.append(")");
    for (float f : keyValues) {
      sb.append(":");
      sb.append(f);
    }
    return sb.toString();
  }

  @Override
  public float scaleValue(float idx) {
    float idx_scaled = idx / scale;
    int idxi = (int) idx_scaled;
    float idxf = idx_scaled - idxi;
    if (idxi < 0) {
      return keyValues[0];
    }
    if (idxi >= keyValues.length - 1) {
      return keyValues[keyValues.length - 1];
    }
    return (1 - idxf) * keyValues[idxi] + idxf * keyValues[idxi + 1];
  }
}