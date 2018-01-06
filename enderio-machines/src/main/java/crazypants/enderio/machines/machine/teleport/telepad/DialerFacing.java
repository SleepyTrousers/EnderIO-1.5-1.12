package crazypants.enderio.machines.machine.teleport.telepad;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

//Derived from LogicFacing in RTTools
public enum DialerFacing implements IStringSerializable {

  DOWN_TONORTH("down_tonorth", 0, NORTH),
  DOWN_TOSOUTH("down_tosouth", 1, SOUTH),
  DOWN_TOWEST("down_towest", 2, WEST),
  DOWN_TOEAST("down_toeast", 3, EAST),

  UP_TONORTH("up_tonorth", 0, NORTH),
  UP_TOSOUTH("up_tosouth", 1, SOUTH),
  UP_TOWEST("up_towest", 2, WEST),
  UP_TOEAST("up_toeast", 3, EAST),

  NORTH_TOWEST("north_towest", 0, WEST),
  NORTH_TOEAST("north_toeast", 1, EAST),
  NORTH_TOUP("north_toup", 2, UP),
  NORTH_TODOWN("north_todown", 3, DOWN),

  SOUTH_TOWEST("south_towest", 0, WEST),
  SOUTH_TOEAST("south_toeast", 1, EAST),
  SOUTH_TOUP("south_toup", 2, UP),
  SOUTH_TODOWN("south_todown", 3, DOWN),

  WEST_TONORTH("west_tonorth", 0, NORTH),
  WEST_TOSOUTH("west_tosouth", 1, SOUTH),
  WEST_TOUP("west_toup", 2, UP),
  WEST_TODOWN("west_todown", 3, DOWN),

  EAST_TONORTH("east_tonorth", 0, NORTH),
  EAST_TOSOUTH("east_tosouth", 1, SOUTH),
  EAST_TOUP("east_toup", 2, UP),
  EAST_TODOWN("east_todown", 3, DOWN);

  private final @Nonnull String name;
  private final int meta;
  private final @Nonnull EnumFacing inputSide;

  DialerFacing(@Nonnull String name, int meta, @Nonnull EnumFacing inputSide) {
    this.name = name;
    this.meta = meta;
    this.inputSide = inputSide;
  }

  @Override
  public @Nonnull String getName() {
    return name;
  }

  public int getMeta() {
    return meta;
  }

  public @Nonnull EnumFacing getInputSide() {
    return inputSide;
  }

  @SuppressWarnings("null")
  public @Nonnull EnumFacing getSide() {
    return EnumFacing.values()[ordinal() / 4];
  }

}
