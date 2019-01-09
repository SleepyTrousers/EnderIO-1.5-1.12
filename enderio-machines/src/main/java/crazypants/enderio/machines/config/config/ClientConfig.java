package crazypants.enderio.machines.config.config;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class ClientConfig {

  public static final IValueFactory F = Config.F.section("client");

  public static final IValue<Boolean> jeiUseShortenedPainterRecipes = F.make("jeiUseShortenedPainterRecipes", true, //
      "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems "
          + "starting a world or logging into a server.");

  public static final IValue<Boolean> machineSoundsEnabled = PersonalConfig.machineSoundsEnabled;

  public static final IValue<Float> machineSoundVolume = PersonalConfig.machineSoundsVolume;

  private enum BloodType implements IValue<Boolean> {
    GREEN {
      @Override
      public Boolean get() {
        return false;
      }
    },
    RED {
      @Override
      public Boolean get() {
        return true;
      }
    },
    AUTO;

    private static boolean hasLogged = false;

    @Override
    public Boolean get() {
      final boolean germany = Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry());
      if (germany && !hasLogged) {
        Log.warn("Detected local country '" + Locale.getDefault().getCountry() + "', cencoring blood.");
        hasLogged = true;
      }
      return !germany;
    }
  }

  public static final IValue<Boolean> bloodEnabled = new IValue<Boolean>() {

    private final IValue<BloodType> bloodEnabledEnum = F.make("bloodColor", BloodType.AUTO, "Which color should blood have? (RED, GREEN, AUTO)");

    @Override
    public @Nonnull Boolean get() {
      return bloodEnabledEnum.get().get();
    }
  };

}
