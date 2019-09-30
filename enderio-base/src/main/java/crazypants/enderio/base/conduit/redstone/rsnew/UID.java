package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class UID {
  protected final @Nonnull BlockPos pos;
  protected final @Nonnull EnumFacing facing;

  public UID(@Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
    this.pos = pos;
    this.facing = facing;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + facing.hashCode();
    result = prime * result + pos.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UID other = (UID) obj;
    if (facing != other.facing) {
      return false;
    }
    if (!pos.equals(other.pos)) {
      return false;
    }
    return true;
  }

}