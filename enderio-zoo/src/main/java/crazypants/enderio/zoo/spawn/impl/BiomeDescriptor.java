package crazypants.enderio.zoo.spawn.impl;

import crazypants.enderio.zoo.spawn.IBiomeDescriptor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeDescriptor implements IBiomeDescriptor {

  private final ResourceLocation name;
  private final BiomeDictionary.Type type;
  private final boolean isExclude;

  public BiomeDescriptor(Type type, boolean isExclude) {
    name = null;
    this.type = type;
    this.isExclude = isExclude;
  }

  public BiomeDescriptor(ResourceLocation name, boolean isExclude) {
    this.name = name;
    type = null;
    this.isExclude = isExclude;
  }

  @Override
  public ResourceLocation getRegistryName() {
    return name;
  }

  @Override
  public BiomeDictionary.Type getType() {
    return type;
  }

  @Override
  public boolean isExclude() {
    return isExclude;
  }

}
