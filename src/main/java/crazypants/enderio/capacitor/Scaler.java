package crazypants.enderio.capacitor;

import net.minecraft.util.math.MathHelper;

public interface Scaler {

  public float scaleValue(float idx);

  public static class IndexedScaler implements Scaler {
    private final float scale;
    private final float[] keyValues;

    public IndexedScaler(float scale, float... keyValues) {
      this.scale = scale;
      this.keyValues = keyValues;
    }

    public String store() {
      StringBuffer sb = new StringBuffer();
      sb.append("idx:");
      sb.append(scale);
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

  public enum Factory implements Scaler {
    IDENTITY(new Scaler() { // 1-2-3
          @Override
          public float scaleValue(float idx) {
            return Math.max(idx, 0);
          }
        }),
    LINEAR_0_8(new Scaler() { // 1-2-3
          @Override
          public float scaleValue(float idx) {
            return MathHelper.clamp(idx, 0, 8);
          }
        }),
    QUADRATIC(new Scaler() { // 1-2-4
          @Override
          public float scaleValue(float idx) {
            return (float) Math.pow(2, idx - 1);
          }
        }),
    QUADRATIC_1_8(new Scaler() { // 1-2-4
          @Override
          public float scaleValue(float idx) {
            return (float) MathHelper.clamp(Math.pow(2, idx - 1), 1, 8);
          }
        }),
    CUBIC(new Scaler() { // 1-3-9
          @Override
          public float scaleValue(float idx) {
            return (float) Math.pow(3, idx - 1);
          }
        }),
    OCTADIC_1_8(new IndexedScaler(.5f, 0, .5f, 1, 3, 2, 4, 8, 10, 16)),
    POWER(new IndexedScaler(1f, 0, 1, 3, 5, 8, 10)),
    SPEED(new IndexedScaler(1f, 100, 20, 10, 2, 1)),
    POWER10(new IndexedScaler(1f, 0, 1, 2, 10, 20)),
    RANGE(new IndexedScaler(1f, 0, 4, 6, 10)),
    FIXED_1(new Scaler() { // 1-1-1
          @Override
          public float scaleValue(float idx) {
            return 1;
          }
        }),
    SPAWNER(new IndexedScaler(1f, 0, 1, 5, 10, 20)),
    BURNTIME(new IndexedScaler(1f, 1, 1f / 2f, 1f / 1.5f, 1f / 1.5f, 1f / 1.25f, 1f / 1f)),
    ;

    private final Scaler scaler;

    private Factory(Scaler scaler) {
      this.scaler = scaler;
    }

    @Override
    public float scaleValue(float idx) {
      return scaler.scaleValue(idx);
    }

    public static String toString(Scaler scaler) {
      if (scaler instanceof Factory) {
        return ((Factory) scaler).name();
      }
      if (scaler instanceof Scaler.IndexedScaler) {
        return ((Scaler.IndexedScaler) scaler).store();
      }
      return null;
    }

    public static Scaler fromString(String s) {
      if (s == null) {
        return null;
      }
      if (s.startsWith("idx:")) {
        try {
          String[] split = s.split(":");
          float scale = 0;
          float[] values = new float[split.length - 2];
          int i = -2;
          for (String sub : split) {
            if (i >= -1) {
              Float value = Float.valueOf(sub);
              if (i == -1) {
                scale = value;
              } else {
                values[i] = value;
              }
            }
            i++;
          }
          return new Scaler.IndexedScaler(scale, values);
        } catch (NumberFormatException e) {
          return null;
        }
      }

      try {
        return Factory.valueOf(s);
      } catch (Exception e) {
        return null;
      }
    }

  }

}