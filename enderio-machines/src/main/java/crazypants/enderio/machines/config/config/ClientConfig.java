package crazypants.enderio.machines.config.config;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class ClientConfig {

  public static final IValueFactory F = Config.F.section("client");

  public static final IValue<Boolean> jeiUseShortenedPainterRecipes = F.make("jeiUseShortenedPainterRecipes", true, //
      "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems "
          + "starting a world or logging into a server.");

  public static final IValue<Boolean> machineSoundsEnabled = PersonalConfig.machineSoundsEnabled;

  public static final IValue<Float> machineSoundVolume = PersonalConfig.machineSoundsVolume;

  public static final IValue<Boolean> bloodEnabled = new IValue<Boolean>() {
    private final IValue<Integer> bloodEnabledInt = F.make("bloodEnabled", 0, "Should blood be red or green? (-1=green, 0=auto, 1=red)").setRange(-1, 1);
    private boolean hasLogged = false;

    @Override
    public @Nonnull Boolean get() {
      final boolean overrideNeeded = Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry());
      final Integer value = bloodEnabledInt.get();
      if (overrideNeeded && value == 0) {
        if (!hasLogged) {
          Log.warn("Detected local country '" + Locale.getDefault().getCountry() + "', censoring blood.");
          hasLogged = true;
        }
        return false;
      } else {
        return value > 0;
      }
    }
  };

}
