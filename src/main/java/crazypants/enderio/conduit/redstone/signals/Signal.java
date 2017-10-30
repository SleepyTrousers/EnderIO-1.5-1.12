package crazypants.enderio.conduit.redstone.signals;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Signal extends CombinedSignal implements ISignalSource {

  private final @Nonnull BlockPos source;
  private final @Nonnull EnumFacing dir;
  private final @Nonnull DyeColor color;

  public Signal(@Nonnull BlockPos source, @Nonnull EnumFacing dir, int strength, @Nonnull DyeColor color) {
    super(strength);
    this.source = source.toImmutable();
    this.dir = dir;
    this.color = color;
  }

  public Signal(@Nonnull CombinedSignal signal, @Nonnull DyeColor color, @Nonnull ISignalSource source) {
    this(source.getSource(), source.getDir(), signal.getStrength(), color);
  }

  @Override
  @Nonnull
  public BlockPos getSource() {
    return source;
  }

  @Override
  @Nonnull
  public EnumFacing getDir() {
    return dir;
  }

  @Nonnull
  public DyeColor getColor() {
    return color;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + color.hashCode();
    result = prime * result + dir.hashCode();
    result = prime * result + source.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Signal other = (Signal) obj;
    if (color != other.color)
      return false;
    if (dir != other.dir)
      return false;
    if (!source.equals(other.source))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Signal [getStrength()=" + getStrength() + ", source=" + source + ", dir=" + dir + ", color=" + color + "]";
  }

}
