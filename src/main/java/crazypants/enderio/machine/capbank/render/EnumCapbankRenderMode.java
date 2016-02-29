package crazypants.enderio.machine.capbank.render;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumCapbankRenderMode implements IStringSerializable {
  DEFAULTS,
  AUTO,

  sides,

  north_west_up(NORTH, WEST, UP),

  north_east_up(NORTH, EAST, UP),

  south_east_up(SOUTH, EAST, UP),

  south_west_up(SOUTH, WEST, UP),

  north_west_down(NORTH, WEST, DOWN),

  north_east_down(NORTH, EAST, DOWN),

  south_east_down(SOUTH, EAST, DOWN),

  south_west_down(SOUTH, WEST, DOWN),

  north_west(NORTH, WEST),

  north_east(NORTH, EAST),

  south_east(SOUTH, EAST),

  south_west(SOUTH, WEST),

  north_up(NORTH, UP),

  east_up(EAST, UP),

  west_up(WEST, UP),

  south_up(SOUTH, UP),

  north_down(NORTH, DOWN),

  east_down(EAST, DOWN),

  west_down(WEST, DOWN),

  south_down(SOUTH, DOWN),

  ;

  public static final PropertyEnum<EnumCapbankRenderMode> RENDER = PropertyEnum.<EnumCapbankRenderMode> create("render", EnumCapbankRenderMode.class);

  private EnumCapbankRenderMode(EnumFacing dir1, EnumFacing dir2, EnumFacing dir3) {
    int id = (1 << dir1.ordinal()) | (1 << dir2.ordinal()) | (1 << dir3.ordinal());
    Mapping.mapping.put(id, this);
  }

  private EnumCapbankRenderMode(EnumFacing dir1, EnumFacing dir2) {
    int id = (1 << dir1.ordinal()) | (1 << dir2.ordinal());
    Mapping.mapping.put(id, this);
  }

  private EnumCapbankRenderMode() {

  }

  public static EnumCapbankRenderMode get(EnumFacing dir1, EnumFacing dir2, EnumFacing dir3) {
    int id = (1 << dir1.ordinal()) | (1 << dir2.ordinal()) | (1 << dir3.ordinal());
    return Mapping.mapping.get(id);
  }

  public static EnumCapbankRenderMode get(EnumFacing dir1, EnumFacing dir2) {
    int id = (1 << dir1.ordinal()) | (1 << dir2.ordinal());
    return Mapping.mapping.get(id);
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  private static class Mapping {
    private static Map<Integer, EnumCapbankRenderMode> mapping = new HashMap<Integer, EnumCapbankRenderMode>();
  }
}
