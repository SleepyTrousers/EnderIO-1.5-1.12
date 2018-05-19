package crazypants.enderio.zoo.spawn;

import net.minecraft.world.biome.Biome;

public interface IBiomeFilter {

  void addBiomeDescriptor(IBiomeDescriptor biome);

  Biome[] getMatchedBiomes();

  boolean isMatchingBiome(Biome bgb);

}
