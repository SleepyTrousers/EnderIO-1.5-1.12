package crazypants.enderio.machines.machine.teleport.telepad.render;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public enum BlockType implements IStringSerializable {

  SINGLE(null),
  MASTER(new BlockPos(0,0,0)),
  N(reverseOffsetFor(EnumFacing.NORTH)),
  NE(reverseOffsetFor(EnumFacing.NORTH, EnumFacing.EAST)),
  E(reverseOffsetFor(EnumFacing.EAST)),
  SE(reverseOffsetFor(EnumFacing.SOUTH, EnumFacing.EAST)),
  S(reverseOffsetFor(EnumFacing.SOUTH)),
  SW(reverseOffsetFor(EnumFacing.SOUTH, EnumFacing.WEST)),
  W(reverseOffsetFor(EnumFacing.WEST)),
  NW(reverseOffsetFor(EnumFacing.NORTH, EnumFacing.WEST));

  private final BlockPos offsetToMaster;
  
  private BlockType(BlockPos offsetToMaster) {
    this.offsetToMaster = offsetToMaster;
  }
  
  public BlockPos getOffsetToMaster() {
    return offsetToMaster;
  }
  
  public Vec3i getOffsetFromMaster() {
    return new BlockPos(-offsetToMaster.getX(), -offsetToMaster.getY(), -offsetToMaster.getZ());
  }

  public BlockPos getLocationOfMaster(BlockPos loc) {
    if(offsetToMaster == null) {
      return null;
    }
    return loc.add(offsetToMaster.getX(), offsetToMaster.getY(), offsetToMaster.getZ());
  }

  @Override
  public String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.US), "toLowerCase returned null!");
  }
  
  private static BlockPos reverseOffsetFor(EnumFacing... dirs) {
    BlockPos res = new BlockPos(0, 0, 0);
    for(EnumFacing dir : dirs) {      
      res = res.offset(dir.getOpposite());
    }
    return res;
  }

  @Nonnull
  public static BlockType getType(int meta) {
    if(meta < 0 || meta >= values().length) {
      return BlockType.SINGLE;
    }
    return NullHelper.notnullJ(values()[meta], "BlockType value is null!");    
  }
}
