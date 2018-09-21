package crazypants.enderio.base.lang;

import javax.annotation.Nonnull;

import static crazypants.enderio.base.lang.Lang.TEMP_DEGC;

public final class LangTemperature {

  public static @Nonnull String degC(float degrees) {
    return TEMP_DEGC.get(LangPower.format(degrees));
  }

  public static @Nonnull String degK(float degrees) {
    return degC(K2C(degrees));
  }

  public static final float C2K(float degrees) {
    return degrees + 273.15f;
  }

  public static final float K2C(float degrees) {
    return degrees - 273.15f;
  }

}
