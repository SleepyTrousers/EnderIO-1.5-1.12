package crazypants.enderio.conduit.redstone;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SignalSource {

  public final BlockPos pos;

  public final EnumFacing fromDirection;

  public SignalSource(Signal signal) {
    this(new BlockPos(signal.x, signal.y, signal.z), signal.dir);    
  }

  public SignalSource(BlockPos blockPos, EnumFacing side) {
    pos = blockPos;
    fromDirection = side;
  }
  
  public BlockPos getPos() {
    return pos;
  }

  public EnumFacing getFromDirection() {
    return fromDirection;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fromDirection == null) ? 0 : fromDirection.hashCode());
    result = prime * result + ((pos == null) ? 0 : pos.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    SignalSource other = (SignalSource) obj;
    if(fromDirection != other.fromDirection) {
      return false;
    }
    if(pos == null) {
      if(other.pos != null) {
        return false;
      }
    } else if(!pos.equals(other.pos)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SignalSource [pos=" + pos + ", fromDirection=" + fromDirection + "]";
  }

  

}
