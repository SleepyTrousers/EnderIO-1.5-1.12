package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class BrokenSpawnerConfig {

  public static final IValueFactory F = BaseConfig.F.section("brokenspawner");

  public static final IValue<Double> brokenSpawnerDropChance = F.make("brokenSpawnerDropChance", 1.0, //
      "The chance a broken spawner will be dropped when a spawner is broken. 1 = 100% chance, 0 = 0% chance").setRange(0.0, 1.0).sync();

  public static final IValue<Things> brokenSpawnerToolBlacklist = F.make("brokenSpawnerToolBlacklist", new Things("item:rotarycraft:rotarycraft_item_bedpick"), //
      "When a spawner is broken with these tools it will not drop a broken spawner.").sync();

}
