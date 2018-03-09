package crazypants.enderio.conduits.conduit.liquid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
// TODO Make Java Docs
public class LiquidOutput {

  final EnumFacing dir;
  final BlockPos location;

  public LiquidOutput(@Nonnull BlockPos pos, @Nonnull EnumFacing dir) {
    this.dir = dir;
    this.location = pos;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((dir == null) ? 0 : dir.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    LiquidOutput other = (LiquidOutput) obj;
    if(location == null) {
      if(other.location != null)
        return false;
    } else if(!location.equals(other.location))
      return false;
    if(dir != other.dir)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LiquidOutput [dir=" + dir + ", location=" + location + "]";
  }

}