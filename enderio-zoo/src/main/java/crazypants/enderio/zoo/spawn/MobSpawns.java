package crazypants.enderio.zoo.spawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.enderio.base.Log;
import crazypants.enderio.zoo.spawn.impl.SpawnEntry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class MobSpawns {

  public static final MobSpawns instance = new MobSpawns();

  private final List<ISpawnEntry> spawnEntries = new ArrayList<ISpawnEntry>();

  private MobSpawns() {
  }

  public void loadSpawnConfig() {
    List<SpawnEntry> entries = SpawnConfig.loadSpawnConfig();
    if (entries != null) {
      Log.info("Applying " + entries.size() + " spawn entries from config.");
      for (SpawnEntry entry : entries) {
        addSpawn(entry);
      }
    } else {
      Log.info("No spawn entries found in config.");
    }

  }

  public void addSpawn(ISpawnEntry entry) {

    if (entry == null) {
      return;
    }

    spawnEntries.add(entry);

    String mobname = entry.getMobName();
    if (mobname == null) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as mob name is null");
      return;
    }

    @SuppressWarnings("unchecked")
    Class<? extends EntityLiving> clz = (Class<? extends EntityLiving>) EntityList.getClass(new ResourceLocation(mobname));
    if (clz == null) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as mob " + entry.getMobName() + " is not registered");
      return;
    }

    if (entry.isRemove()) {
      Log.debug("EnderIO.MobSpawns.addSpawn: Removing spawns defined in entry: ", entry, " for biomes: ");
      for (IBiomeFilter filter : entry.getFilters()) {
        Biome[] biomes = filter.getMatchedBiomes();
        printBiomeNames(biomes);
        EntityRegistry.removeSpawn(clz, entry.getCreatureType(), biomes);
      }
      return;
    }

    Log.debug("MobSpawns.addSpawn: Adding spawns defined in entry: ", entry, " for biomes: ");
    for (IBiomeFilter filter : entry.getFilters()) {
      Biome[] biomes = filter.getMatchedBiomes();
      printBiomeNames(biomes);
      EntityRegistry.addSpawn(clz, entry.getRate(), entry.getMinGroupSize(), entry.getMaxGroupSize(), entry.getCreatureType(), biomes);
    }

  }

  protected static void printBiomeNames(Biome[] biomes) {
    for (Biome biome : biomes) {
      if (biome != null) {
        Log.debug(" - ", biome.getBiomeName());
      } else {
        Log.debug(" - null");
      }
    }
  }

  public Collection<ISpawnEntry> getEntries() {
    return spawnEntries;
  }

}
