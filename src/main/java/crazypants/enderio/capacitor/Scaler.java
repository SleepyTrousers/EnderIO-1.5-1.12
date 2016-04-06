package crazypants.enderio.capacitor;

import net.minecraft.util.MathHelper;

public interface Scaler {

  public float scaleValue(float idx);

  public static class IndexedScaler implements Scaler {
    private final float scale;
    private final float[] keyValues;

    public IndexedScaler(float scale, float... keyValues) {
      this.scale = scale;
      this.keyValues = keyValues;
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

  public static final Scaler IDENTITY = new Scaler() { // 1-2-3
    @Override
    public float scaleValue(float idx) {
      return Math.max(idx, 0);
    }
  };
  public static final Scaler LINEAR_0_8 = new Scaler() { // 1-2-3
    @Override
    public float scaleValue(float idx) {
      return MathHelper.clamp_float(idx, 0, 8);
    }
  };
  public static final Scaler QUADRATIC = new Scaler() { // 1-2-4
    @Override
    public float scaleValue(float idx) {
      return (float) Math.pow(2, idx - 1);
    }
  };
  public static final Scaler QUADRATIC_1_8 = new Scaler() { // 1-2-4
    @Override
    public float scaleValue(float idx) {
      return (float) MathHelper.clamp_double(Math.pow(2, idx - 1), 1, 8);
    }
  };
  public static final Scaler CUBIC = new Scaler() { // 1-3-9
    @Override
    public float scaleValue(float idx) {
      return (float) Math.pow(3, idx - 1);
    }
  };
  public static final Scaler OCTADIC_1_8 = new IndexedScaler(.5f, 0, .5f, 1, 3, 2, 4, 8, 10, 16); // 1-2-8
  public static final Scaler POWER = new IndexedScaler(1f, 0, 1, 3, 5, 8, 10); // 1-3-5
  public static final Scaler SPEED = new IndexedScaler(1f, 100, 20, 10, 2, 1); // 20-10-1
  public static final Scaler POWER10 = new IndexedScaler(1f, 0, 1, 2, 10, 20); // 1-2-10
  public static final Scaler RANGE = new IndexedScaler(1f, 0, 4, 6, 10); // 4-6-10
  public static final Scaler FIXED_1 = new Scaler() { // 1-1-1
    @Override
    public float scaleValue(float idx) {
      return 1;
    }
  };
  public static final Scaler SPAWNER = new IndexedScaler(1f, 0, 1, 5, 10, 20); // 1-5-10

}