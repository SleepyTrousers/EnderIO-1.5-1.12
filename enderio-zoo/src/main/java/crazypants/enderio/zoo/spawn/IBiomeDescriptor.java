package crazypants.enderio.zoo.spawn;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

public interface IBiomeDescriptor {

  BiomeDictionary.Type getType();

  ResourceLocation getRegistryName();

  boolean isExclude();

}
