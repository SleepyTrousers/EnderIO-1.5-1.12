package crazypants.enderio.zoo.spawn.impl;

import net.minecraft.world.World;

public class DimensionFilter {

  public static enum Type {
    NONE,
    BLACK,
    WHITE;

    public Type combine(Type other) {
      if (this == other) {
        return this;
      } else if (this == NONE) {
        return other;
      } else if (other == NONE) {
        return this;
      } else {
        return WHITE;
      }
    }
  }

  private final String name;
  private final boolean isRange;
  private final int minId;
  private final int maxId;
  private final DimensionFilter.Type type;

  public DimensionFilter(String name, boolean isRange, int minId, int maxId, DimensionFilter.Type type) {
    this.name = name;
    this.isRange = isRange;
    this.minId = minId;
    this.maxId = maxId;
    this.type = type;
  }

  public DimensionFilter(String name, DimensionFilter.Type type) {
    this(name, false, Integer.MAX_VALUE, Integer.MAX_VALUE, type);
  }

  public DimensionFilter(int id, DimensionFilter.Type type) {
    this(null, false, id, Integer.MAX_VALUE, type);
  }

  public DimensionFilter(int minId, int maxId, DimensionFilter.Type type) {
    this(null, true, minId, maxId, type);
  }

  public DimensionFilter.Type canSpawnInDimension(World world) {
    if (name != null) {
      return name.equals(world.provider.getDimensionType().getName()) ? type : DimensionFilter.Type.NONE;
    }
    int id = world.provider.getDimension();
    if (isRange) {
      return (id < minId || id > maxId) ? DimensionFilter.Type.NONE : type;
    }
    return (id != minId) ? DimensionFilter.Type.NONE : type;
  }

}
