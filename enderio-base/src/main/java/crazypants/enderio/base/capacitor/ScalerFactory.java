package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.capacitor.Scaler;
import net.minecraft.util.math.MathHelper;

/**
 * This factory allows us to write scalers to the config file. All scalers used within Ender IO must be supported by the factors (i.e. either be an Enum
 * constant or an IndexedScaler), but addons may use their own scalers.
 *
 */
public enum ScalerFactory implements Scaler {
  INVALID(new Scaler() { // 0-0-0-...
    @Override
    public float scaleValue(float idx) {
      return 0;
    }
  }),
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
      // replace with identity
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
      // ab^x * x^c
      // a = 0.9999999999999999
      // b = 1
      // c = 1.584962500721159
    }
  }),
  OCTADIC_1_8(new IndexedScaler(.5f, 0, .5f, 1, 3, 2, 4, 8, 10, 16)),
  POWER(new IndexedScaler(1f, 0, 1, 3, 5, 8, 13, 18)),
  // indexed 0, 1, 3, 4, 5, 6.5, 8, 10.5, 13, 15.5, 18
  // better:
  // a + bx + cx^2 + dx^3 + ex^4
  // a -2.86111111110643E+00
  // b 5.16712962962717E+00
  // c -1.51296296296188E+00
  // d 2.16550925925753E-01
  // e -9.60648148147314E-03

  CHARGE(new IndexedScaler(1f, 1000, 100, 60, 20, 10, 1)),
  // ( a + bx ) / ( 1 + cx + dx^2 )
  // a 9.99987140430512E+02
  // b -9.78706412647100E+01
  // c 8.36064838806583E+00
  // d -5.96021930516349E-01
  SPEED(new IndexedScaler(1f, 100, 20, 10, 2, 1)),
  POWER10(new IndexedScaler(1f, 0, 1, 2, 10, 20, 40)),
  RANGE(new IndexedScaler(1f, 0, 4, 6, 10, 17, 24)),
  // 2 + 2x
  FIXED(new Scaler() { // 1-1-1
    @Override
    public float scaleValue(float idx) {
      return 1;
    }
  }),
  SPAWNER(new IndexedScaler(1f, 0, 1, 5, 10, 20, 40)),
  // a + bx + cx^2 + dx^3 + ex^4
  // a -6.62857142856527E+00
  // b 1.02380952380926E+01
  // c -3.04166666666589E+00
  // d 4.49404761904681E-01
  // e -1.72619047619040E-02
  BURNTIME(new IndexedScaler(1f, 0.8f, 1f, 1.25f, 1.5f, 1.5f, 2f, 2.5f) {
    @Override
    public float scaleValue(float idx) {
      return super.scaleValue(idx) / 100f; // Convert from percentage
    }
  }),
  CHEMICAL(new Scaler() { // (.75)-1-1.25-1.5-1.75-2...
    @Override
    public float scaleValue(float idx) {
      return 1 + (idx - 1f) * 0.25f;
    }
  }),
  DROPOFF(new IndexedScaler(1f, 1, 1, 4 / 3f, 2, 2.5f, 3f, 3.25f)), // Special case for stirling gen
  CENT(new Scaler() { // 0.01-0.01-0.01 (used for power loss)
    @Override
    public float scaleValue(float idx) {
      return 0.01f;
    }
  }),

  ;

  private final @Nonnull Scaler scaler;

  private ScalerFactory(@Nonnull Scaler scaler) {
    this.scaler = scaler;
  }

  @Override
  public float scaleValue(float idx) {
    return scaler.scaleValue(idx);
  }

  public static @Nullable String toString(@Nonnull Scaler scaler) {
    if (scaler instanceof ScalerFactory) {
      return ((ScalerFactory) scaler).name();
    }
    if (scaler instanceof IndexedScaler) {
      return ((IndexedScaler) scaler).store();
    }
    return null;
  }

  public static @Nullable Scaler fromString(@Nullable String s) {
    if (s == null) {
      return null;
    }
    if (s.startsWith("idx(")) {
      s = s.replace('(', ':').replace(')', ':').replaceAll("::", ":");
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
        return new IndexedScaler(scale, values);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    try {
      return ScalerFactory.valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }

}