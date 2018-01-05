package crazypants.enderio.machines.config.config;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class ClientConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "client"));

  public static final IValue<Boolean> jeiUseShortenedPainterRecipes = F.make("jeiUseShortenedPainterRecipes", true, //
      "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems "
          + "starting a world or logging into a server.");

  public static final IValue<Boolean> machineSoundsEnabled = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.machineSoundsEnabled;
    }
  };

  public static final IValue<Float> machineSoundVolume = new IValue<Float>() {
    @Override
    public @Nonnull Float get() {
      return crazypants.enderio.base.config.Config.machineSoundVolume;
    }
  };

  public static final IValue<Boolean> bloodEnabled = new IValue<Boolean>() {
    private final IValue<Boolean> bloodEnabledInt = F.make("bloodEnabled", true, "Should blood be red or green?");

    @Override
    public @Nonnull Boolean get() {
      final boolean overrideNeeded = Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry());
      if (overrideNeeded) {
        Log.warn("Detected local country '" + Locale.getDefault().getCountry() + "', cencoring blood.");
        return false;
      } else {
        Log.info("Detected local country '" + Locale.getDefault().getCountry() + "'");
        return bloodEnabledInt.get();
      }
    }
  };

}
