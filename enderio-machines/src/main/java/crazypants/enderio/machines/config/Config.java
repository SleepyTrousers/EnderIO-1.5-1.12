package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.config.config.CombustionGenConfig;
import crazypants.enderio.machines.config.config.ExperienceConfig;
import crazypants.enderio.machines.config.config.FarmConfig;
import crazypants.enderio.machines.config.config.InhibitorConfig;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.config.config.SoulBinderConfig;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.config.config.TankConfig;
import crazypants.enderio.machines.config.config.TranceiverConfig;
import crazypants.enderio.machines.config.config.VacuumConfig;
import crazypants.enderio.machines.config.config.VatConfig;
import crazypants.enderio.machines.config.config.WeatherConfig;
import crazypants.enderio.machines.config.config.ZombieGenConfig;
import crazypants.enderio.machines.machine.ihopper.ImpulseHopperConfig;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final Section sectionCapacitor = new Section("", "capacitor");

  public static final ValueFactory F = new ValueFactory(EnderIOMachines.MODID);

  public static final IValue<Float> explosionResistantBlockHardness = new IValue<Float>() {
    @Override
    public @Nonnull Float get() {
      return crazypants.enderio.base.config.Config.EXPLOSION_RESISTANT;
    }
  };

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    ClientConfig.F.getClass();
    CombustionGenConfig.F.getClass();
    ExperienceConfig.F.getClass();
    FarmConfig.F.getClass();
    ImpulseHopperConfig.F.getClass();
    InhibitorConfig.F.getClass();
    KillerJoeConfig.F.getClass();
    SolarConfig.F.getClass();
    SoulBinderConfig.F.getClass();
    SpawnerConfig.F.getClass();
    TankConfig.F.getClass();
    TranceiverConfig.F.getClass();
    VacuumConfig.F.getClass();
    VatConfig.F.getClass();
    WeatherConfig.F.getClass();
    ZombieGenConfig.F.getClass();
  }
}
