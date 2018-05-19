package crazypants.enderio.zoo.spawn.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeFilterAll extends AbstractBiomeFilter {

  @Override
  public Biome[] getMatchedBiomes() {

    if (types.isEmpty() && names.isEmpty()) {
      return new Biome[0];
    }
    
    Set<Biome> result = new HashSet<Biome>();
    Iterator<Biome> it = Biome.REGISTRY.iterator();
    while(it.hasNext()) {
      Biome candidate = it.next();
      if (candidate != null && isMatchingBiome(candidate)) {
        result.add(candidate);
      }
    }    
    return result.toArray(new Biome[result.size()]);
  }

  @Override
  public boolean isMatchingBiome(Biome biome) {

    if (isExcluded(biome)) {
      return false;
    }
    if (!names.isEmpty() && !names.contains(biome.getRegistryName())) {
      return false;
    }
    for (BiomeDictionary.Type type : types) {
      if (!BiomeDictionary.hasType(biome, type)) {
        return false;
      }
    }
    return true;
  }
}
