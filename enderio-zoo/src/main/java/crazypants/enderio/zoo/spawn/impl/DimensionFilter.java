package crazypants.enderio.zoo.spawn.impl;

import net.minecraft.world.World;

public class DimensionFilter {

  private final String name;
  private final boolean isRange;
  private final int minId;
  private final int maxId;

  public DimensionFilter(String name, boolean isRange, int minId, int maxId) {
    this.name = name;
    this.isRange = isRange;
    this.minId = minId;
    this.maxId = maxId;
  }

  public DimensionFilter(String name) {
    this(name, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  public DimensionFilter(int id) {
    this(null, false, id, Integer.MAX_VALUE);
  }

  public DimensionFilter(int minId, int maxId) {
    this(null, true, minId, maxId);
  }

  public boolean canSpawnInDimension(World world) {
    if (name != null) {
      return !name.equals(world.provider.getDimensionType().getName());
    }
    int id = world.provider.getDimension();
    if(isRange) {
      return id < minId || id > maxId;
    }
    return id != minId;
  }

}
