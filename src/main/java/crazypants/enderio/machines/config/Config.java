package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.config.config.ChargerConfig;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.config.config.EnchanterConfig;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.config.config.SoulBinderConfig;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.config.config.VacuumConfig;
import crazypants.enderio.machines.config.config.ZombieGenConfig;
import crazypants.enderio.machines.network.PacketHandler;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final Section sectionCapacitor = new Section("", "capacitor");

  public static final ValueFactory F = new ValueFactory(PacketHandler.INSTANCE);

  public static final IValue<Boolean> registerRecipes = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.registerRecipes;
    }
  };

  public static final IValue<Float> explosionResistantBlockHardness = new IValue<Float>() {
    @Override
    public @Nonnull Float get() {
      return crazypants.enderio.base.config.Config.EXPLOSION_RESISTANT;
    }
  };
  

  //

  static {
    // force sub-configs to be classloaded with the main config
    ChargerConfig.F.getClass();
    ClientConfig.F.getClass();
    EnchanterConfig.F.getClass();
    KillerJoeConfig.F.getClass();
    SolarConfig.F.getClass();
    SoulBinderConfig.F.getClass();
    SpawnerConfig.F.getClass();
    VacuumConfig.F.getClass();
    ZombieGenConfig.F.getClass();
  }
}
