package crazypants.enderio.zoo.spawn;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public interface ISpawnEntry {

  String getId();

  String getMobName();

  EnumCreatureType getCreatureType();

  int getRate();

  int getMaxGroupSize();

  int getMinGroupSize();

  boolean isRemove();

  List<IBiomeFilter> getFilters();

  boolean canSpawnInDimension(World world);

}
