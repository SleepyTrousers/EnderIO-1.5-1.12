package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.machines.config.Config;

public final class SpawnerConfig {

  public static final IValueFactory F = Config.F.section("spawner");

  public static final IValue<Integer> powerSpawnerAddSpawnerCost = F.make("powerSpawnerAddSpawnerCost", 16, //
      "The number of levels it costs to add a broken spawner").setMin(1).sync();

  public static final IValue<Integer> poweredSpawnerDespawnTimeSeconds = F.make("poweredSpawnerDespawnTimeSeconds", 120, //
      "Number of seconds in which spawned entities are protected from despawning").setRange(0, Integer.MAX_VALUE / 20).sync();

  public static final IValue<Integer> poweredSpawnerSpawnRange = F.make("poweredSpawnerSpawnRange", 4, //
      "Spawning range in X/Z (vanilla=4)").setRange(1, 32).sync();

  public static final IValue<Integer> poweredSpawnerMinDelayTicks = F.make("poweredSpawnerMinDelayTicks", 200, //
      "Min tick delay between spawns for a non-upgraded spawner").setMin(1).sync();
  public static final IValue<Integer> poweredSpawnerMaxDelayTicks = F.make("poweredSpawnerMaxDelayTicks", 800, //
      "Max tick delay between spawns for a non-upgraded spawner").setMin(1).sync();

  public static final IValue<Integer> poweredSpawnerMaxPlayerDistance = F.make("poweredSpawnerMaxPlayerDistance", 0, //
      "Max distance of the closest player for the spawner to be active. A zero value will remove the player check").setRange(0, 64).sync();

  public static final IValue<Integer> poweredSpawnerSpawnCount = F.make("poweredSpawnerSpawnCount", 4, //
      "Number of entities to spawn each time").setRange(1, 16).sync();
  public static final IValue<Integer> poweredSpawnerMaxNearbyEntities = F.make("poweredSpawnerMaxNearbyEntities", 6, //
      "Max number of entities in the nearby area until no more are spawned. A zero value will remove this check").setMin(0).sync();
  public static final IValue<Integer> poweredSpawnerMaxSpawnTries = F.make("poweredSpawnerMaxSpawnTries", 3, //
      "Number of tries to find a suitable spawning location").setRange(1, 9).sync();
  public static final IValue<Boolean> poweredSpawnerUseVanillaSpawnChecks = F.make("poweredSpawnerUseVanillaSpawnChecks", false, //
      "If true, regular spawn checks such as lighting level and dimension will be made before spawning mobs").sync();

  public static final IValue<Boolean> poweredSpawnerAddAllMobsCreative = F.make("poweredSpawnerAddAllMobsCreative", false, //
      "If true, spawners for all mobs will be added to the creative menu. Otherwise only a handfull of samples are added. (Client setting.)");

  public static final IValueFactory C = F.section(".creative");

  public static final IValue<Boolean> homeZoneEnabled = F.make("homeZoneEnabled", true, //
      "If true, creative spawners sill set the homezone on spawned creatures. Note that bats, ghast, slimes and squid are not creatures.").sync();
  public static final IValue<Integer> homeZoneSize = F.make("homeZoneSize", 30, //
      "The size of the homezone. Radius in blocks. No effect if homeZoneEnabled is off.").sync();
  public static final IValue<Integer> homeZoneLimit = F.make("homeZoneLimit", 16, //
      "The maximum number of mobs allowed in the homezone. When there are more, no new ones will be spawned. Radius in blocks. No effect if homeZoneEnabled is off. "
          + "Note that poweredSpawnerMaxNearbyEntities still applies for mobs within the poweredSpawnerSpawnRange.")
      .sync();

}
