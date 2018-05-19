package crazypants.enderio.zoo.spawn.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeFilterAny extends AbstractBiomeFilter {

  @Override
  public Biome[] getMatchedBiomes() {

    if (types.isEmpty() && names.isEmpty()) {
      return new Biome[0];
    }
    Set<Biome> passedBiomes = new HashSet<Biome>();
    Iterator<Biome> it = Biome.REGISTRY.iterator();
    while(it.hasNext()) {
      Biome candidate = it.next();
      if (candidate != null && isMatchingBiome(candidate)) {
        passedBiomes.add(candidate);
      }
    }

    return passedBiomes.toArray(new Biome[passedBiomes.size()]);
  }

  @Override
  public boolean isMatchingBiome(Biome biome) {
    if (isExcluded(biome)) {
      return false;
    }
    if (names.contains(biome.getRegistryName())) {
      return true;
    }
    for (BiomeDictionary.Type type : types) {
      if (BiomeDictionary.hasType(biome, type)) {
        return true;
      }
    }
    return false;
  }

}
