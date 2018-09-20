package crazypants.enderio.base.lang;

import javax.annotation.Nonnull;

import static crazypants.enderio.base.lang.Lang.TEMP_DEGC;

public final class LangTemperature {

  public static @Nonnull String degC(float degrees) {
    return TEMP_DEGC.get(LangPower.format(degrees));
  }

  public static @Nonnull String degK(float degrees) {
    return degC(degrees - 273.15f);
  }

}
