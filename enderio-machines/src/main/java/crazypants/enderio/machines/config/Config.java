package crazypants.enderio.machines.config;

import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.AlloySmelterConfig;
import crazypants.enderio.machines.config.config.AttractorConfig;
import crazypants.enderio.machines.config.config.ChargerConfig;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.config.config.CombustionGenConfig;
import crazypants.enderio.machines.config.config.ExperienceConfig;
import crazypants.enderio.machines.config.config.FarmConfig;
import crazypants.enderio.machines.config.config.ImpulseHopperConfig;
import crazypants.enderio.machines.config.config.InhibitorConfig;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import crazypants.enderio.machines.config.config.LavaGenConfig;
import crazypants.enderio.machines.config.config.NiardConfig;
import crazypants.enderio.machines.config.config.SliceAndSpliceConfig;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.config.config.SoulBinderConfig;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.config.config.TankConfig;
import crazypants.enderio.machines.config.config.TelePadConfig;
import crazypants.enderio.machines.config.config.TranceiverConfig;
import crazypants.enderio.machines.config.config.VacuumConfig;
import crazypants.enderio.machines.config.config.VatConfig;
import crazypants.enderio.machines.config.config.WeatherConfig;
import crazypants.enderio.machines.config.config.XPObeliskConfig;
import crazypants.enderio.machines.config.config.ZombieGenConfig;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOMachines.MODID);

  static {
    // force sub-configs to be classloaded with the main config
    AlloySmelterConfig.F.getClass();
    AttractorConfig.F.getClass();
    ClientConfig.F.getClass();
    CombustionGenConfig.F.getClass();
    ExperienceConfig.F.getClass();
    FarmConfig.F.getClass();
    ImpulseHopperConfig.F.getClass();
    InhibitorConfig.F.getClass();
    KillerJoeConfig.F.getClass();
    LavaGenConfig.F.getClass();
    NiardConfig.F.getClass();
    SliceAndSpliceConfig.F.getClass();
    SolarConfig.F.getClass();
    SoulBinderConfig.F.getClass();
    SpawnerConfig.F.getClass();
    TankConfig.F.getClass();
    TelePadConfig.F.getClass();
    TranceiverConfig.F.getClass();
    VacuumConfig.F.getClass();
    VatConfig.F.getClass();
    WeatherConfig.F.getClass();
    ChargerConfig.F.getClass();
    ZombieGenConfig.F.getClass();
    XPObeliskConfig.F.getClass();
  }
}
