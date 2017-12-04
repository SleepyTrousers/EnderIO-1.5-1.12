package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.config.config.EnchanterConfig;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import crazypants.enderio.machines.config.config.ZombieGenConfig;
import crazypants.enderio.machines.network.PacketHandler;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final Section CLIENT = new Section("", "client");
  public static final ValueFactory F = new ValueFactory(PacketHandler.INSTANCE);

  public static final IValue<Boolean> registerRecipes = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.registerRecipes;
    }
  };

  

  //

  static {
    // force sub-configs to be classloaded with the main config
    EnchanterConfig.F.getClass();
    ZombieGenConfig.F.getClass();
    KillerJoeConfig.F.getClass();
    ClientConfig.F.getClass();
  }
}
