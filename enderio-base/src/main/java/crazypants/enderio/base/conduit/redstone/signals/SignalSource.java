package crazypants.enderio.base.conduit.redstone.signals;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SignalSource implements ISignalSource {

  private final @Nonnull BlockPos source;
  private final @Nonnull EnumFacing dir;

  public SignalSource(@Nonnull BlockPos source, @Nonnull EnumFacing dir) {
    this.source = source.toImmutable();
    this.dir = dir;
  }

  @Override
  public @Nonnull BlockPos getSource() {
    return source;
  }

  @Override
  public @Nonnull EnumFacing getDir() {
    return dir;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dir.hashCode();
    result = prime * result + source.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SignalSource other = (SignalSource) obj;
    if (dir != other.dir)
      return false;
    if (!source.equals(other.source))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SignalSource [source=" + source + ", dir=" + dir + "]";
  }

}
