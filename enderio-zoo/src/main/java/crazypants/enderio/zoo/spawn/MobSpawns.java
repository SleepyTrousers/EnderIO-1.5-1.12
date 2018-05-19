package crazypants.enderio.zoo.spawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.enderio.zoo.EnderZoo;
import crazypants.enderio.zoo.Log;
import crazypants.enderio.zoo.config.Config;
import crazypants.enderio.zoo.config.SpawnConfig;
import crazypants.enderio.zoo.entity.MobInfo;
import crazypants.enderio.zoo.spawn.impl.SpawnEntry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class MobSpawns {

  private static final boolean PRINT_DETAIL = Config.spawnConfigPrintDetailedOutput;

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

    String mobname = entry.getMobName().replace("EnderZoo.","").toLowerCase();//To fix legacy config files that werent updated, for existing installs
    //but the config xml also has been updated too
    @SuppressWarnings("unchecked")
    Class<? extends EntityLiving> clz = (Class<? extends EntityLiving>) EntityList.getClass(new ResourceLocation(EnderZoo.MODID,mobname));
    if (clz == null) {
      Log.warn("Skipping spawn entry " + entry.getId() + " as mob " + entry.getMobName() + " is not registered");
      return;
    }
    
    if(MobInfo.isDisabled(clz)) {
      Log.info(entry.getMobName() + " is disabled");
      return;
    }
    
    if (entry.isRemove()) {
      if (PRINT_DETAIL) {
        //yeah, I know I could print them as debug messages but that is more painful to change...
        Log.info("EnderIO.MobSpawns.addSpawn: Removing spawns defined in entry: " + entry + " for biomes: ");
        System.out.print(" - ");
      }
      for (IBiomeFilter filter : entry.getFilters()) {
        Biome[] biomes = filter.getMatchedBiomes();
        if (PRINT_DETAIL) {
          printBiomeNames(biomes);
        }
        EntityRegistry.removeSpawn(clz, entry.getCreatureType(), biomes);
      }
      if (PRINT_DETAIL) {
        System.out.println();
      }
      return;
    }

    if (PRINT_DETAIL) {
      Log.info("MobSpawns.addSpawn: Adding spawns defined in entry: " + entry + " for biomes: ");
      System.out.print(" - ");
    }
    for (IBiomeFilter filter : entry.getFilters()) {
      Biome[] biomes = filter.getMatchedBiomes();
      if (PRINT_DETAIL) {
        printBiomeNames(biomes);
      }
      EntityRegistry.addSpawn(clz, entry.getRate(), entry.getMinGroupSize(), entry.getMaxGroupSize(), entry.getCreatureType(), biomes);
    }
    if (PRINT_DETAIL) {
      System.out.println();
    }

  }

  protected static void printBiomeNames(Biome[] biomes) {
    for (Biome biome : biomes) {
      if (biome != null) {
        System.out.print(biome.getBiomeName() + ", ");
      } else {
        System.out.print("null, ");
      }
    }
  }

  public Collection<ISpawnEntry> getEntries() {
    return spawnEntries;
  }

}
