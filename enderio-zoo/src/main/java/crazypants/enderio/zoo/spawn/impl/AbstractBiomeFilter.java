package crazypants.enderio.zoo.spawn.impl;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.base.Log;
import crazypants.enderio.zoo.spawn.IBiomeDescriptor;
import crazypants.enderio.zoo.spawn.IBiomeFilter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public abstract class AbstractBiomeFilter implements IBiomeFilter {

  protected final List<BiomeDictionary.Type> types = new ArrayList<BiomeDictionary.Type>();
  protected final List<BiomeDictionary.Type> typeExcludes = new ArrayList<BiomeDictionary.Type>();

  protected final List<ResourceLocation> names = new ArrayList<ResourceLocation>();
  protected final List<ResourceLocation> nameExcludes = new ArrayList<ResourceLocation>();

  @Override
  public void addBiomeDescriptor(IBiomeDescriptor biome) {
    if (biome.getType() != null) {
      if (biome.isExclude()) {
        typeExcludes.add(biome.getType());
      } else {
        types.add(biome.getType());
      }
    } else if (biome.getRegistryName() != null) {
      if (biome.isExclude()) {
        nameExcludes.add(biome.getRegistryName());
      } else {
        names.add(biome.getRegistryName());
      }
    }
  }

  protected boolean isExcluded(Biome candidate) {
    for (BiomeDictionary.Type exType : typeExcludes) {
      if (BiomeDictionary.hasType(candidate, exType)) {
        Log.debug("Excluded ", candidate.getRegistryName(), ", ");
        return true;

      }
    }
    for (ResourceLocation exName : nameExcludes) {
      if (exName != null && exName.equals(candidate.getRegistryName())) {
        Log.debug("Excluded ", candidate.getRegistryName(), ", ");
        return false;
      }
    }
    return false;
  }

}
