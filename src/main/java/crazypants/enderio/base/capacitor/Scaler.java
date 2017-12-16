package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.MathHelper;

/**
 * A scaler translates a raw capacitor level into a factor that can be used to scale a dynamic value of a machine. The input for a scaler is the capacitor
 * level, a float between 0.0 and ~5.2. There's no hard upper limit, but results tend to explode outside this range. The output of the scaler is multiplied with
 * the base value of the CapacitorKey, so it should stay in a 0 to 20 range to prevent absurd results.
 * 
 * Note: A scaler is a "y = f(x)" function, which you can plot out on graph paper. Try it!
 *
 */
public interface Scaler {

  public float scaleValue(float idx);

  /**
   * The IndexedScaler is s scaler that interpolates linearly between a number of points. Those points are at fixed intervals on the x-axis (one every 'scale'
   * units).
   *
   * The points are at (scale * n; keyValues[n]) for n in 0...keyValues.length-1
   *
   * Again, plotting it out is helpful.
   */
  public static class IndexedScaler implements Scaler {
    private final float scale;
    private final float[] keyValues;

    public IndexedScaler(float scale, float... keyValues) {
      this.scale = scale;
      this.keyValues = keyValues;
    }

    public @Nonnull String store() {
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

  /**
   * This factory allows us to write scalers to the config file. All scalers used within Ender IO must be supported by the factors (i.e. either be an Enum
   * constant or an IndexedScaler), but addons may use their own scalers.
   *
   */
  public enum Factory implements Scaler {
    IDENTITY(new Scaler() { // 1-2-3-...
      @Override
      public float scaleValue(float idx) {
        return Math.max(idx, 0);
      }
    }),
    LINEAR_0_8(new Scaler() { // 1-2-3-4-5-6-7-8-8-8
      @Override
      public float scaleValue(float idx) {
        return MathHelper.clamp(idx, 0, 8);
      }
    }),
    QUADRATIC(new Scaler() { // 1-2-4-8-16-...
      @Override
      public float scaleValue(float idx) {
        return (float) Math.pow(2, idx - 1);
      }
    }),
    QUADRATIC_1_8(new Scaler() { // 1-2-4-8-8-8
      @Override
      public float scaleValue(float idx) {
        return (float) MathHelper.clamp(Math.pow(2, idx - 1), 1, 8);
      }
    }),
    CUBIC(new Scaler() { // 1-3-9-...
      @Override
      public float scaleValue(float idx) {
        return (float) Math.pow(3, idx - 1);
      }
    }),
    OCTADIC_1_8(new IndexedScaler(.5f, 0, .5f, 1, 3, 2, 4, 8, 10, 16)),
    POWER(new IndexedScaler(1f, 0, 1, 3, 5, 8, 13)),
    SPEED(new IndexedScaler(1f, 100, 20, 10, 2, 1)),
    POWER10(new IndexedScaler(1f, 0, 1, 2, 10, 20)),
    RANGE(new IndexedScaler(1f, 0, 4, 6, 10, 13)),
    FIXED_1(new Scaler() { // 1-1-1
      @Override
      public float scaleValue(float idx) {
        return 1;
      }
    }),
    SPAWNER(new IndexedScaler(1f, 0, 1, 5, 10, 20)),
    BURNTIME(new IndexedScaler(1f, 1, 1f / 2f, 1f / 1.5f, 1f / 1.5f, 1f / 1.25f, 1f / 1f)),;

    private final @Nonnull Scaler scaler;

    private Factory(@Nonnull Scaler scaler) {
      this.scaler = scaler;
    }

    @Override
    public float scaleValue(float idx) {
      return scaler.scaleValue(idx);
    }

    public static @Nullable String toString(@Nonnull Scaler scaler) {
      if (scaler instanceof Factory) {
        return ((Factory) scaler).name();
      }
      if (scaler instanceof Scaler.IndexedScaler) {
        return ((Scaler.IndexedScaler) scaler).store();
      }
      return null;
    }

    public static @Nullable Scaler fromString(@Nullable String s) {
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