package crazypants.enderio.util;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * A mutable {@link BlockPos} that can point to a block and any of its 6 neighbours.
 * 
 * Usage pattern:
 * 
 * <pre>
 * Neighbours base = new Neighbours(new BlockPos(1, 2, 3));
 * for (BlockPos offset : base) { // base === offset
 *   world.getTileEntity(base).hasCapability(CAP, base.getOffset());
 * }
 * </pre>
 *
 */
public class Neighbours extends BlockPos implements Iterable<Neighbours>, Iterator<Neighbours> {

  private int x, y, z, offset = -1;

  public Neighbours(@Nonnull BlockPos source) {
    super(source);
    setOffset(null);
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getZ() {
    return z;
  }

  public void setOffset(@Nullable EnumFacing facing) {
    if (facing != null) {
      x = super.getX() + facing.getFrontOffsetX();
      y = super.getY() + facing.getFrontOffsetY();
      z = super.getZ() + facing.getFrontOffsetZ();
      offset = facing.ordinal();
    } else {
      x = super.getX();
      y = super.getY();
      z = super.getZ();
      offset = -1;
    }
  }

  /**
   * If the position was shifted it gives the direction of the shift. If not, it throws an {@link ArrayIndexOutOfBoundsException} to be {@link Nonnull}.
   */
  @SuppressWarnings("null") // Trust me, when EnumFacing.VALUES contains a null you want to crash ASAP...
  public @Nonnull EnumFacing getOffset() {
    return EnumFacing.VALUES[offset];
  }

  /**
   * If the position was shifted it gives the direction opposite to the shift. If not, it throws an {@link ArrayIndexOutOfBoundsException} to be
   * {@link Nonnull}.
   */
  public @Nonnull EnumFacing getOpposite() {
    return EnumFacing.VALUES[offset].getOpposite();
  }

  @Override
  public @Nonnull BlockPos toImmutable() {
    return new BlockPos(this);
  }

  /**
   * See {@link Iterable#iterator()}. Please note that next() will always return the base {@link Neighbours} object which also means that iterators are not
   * independent.
   */
  @Override
  public Iterator<Neighbours> iterator() {
    offset = -1;
    return this;
  }

  @Override
  public boolean hasNext() {
    return offset < 5;
  }

  @Override
  public @Nonnull Neighbours next() {
    setOffset(EnumFacing.VALUES[offset + 1]);
    return this;
  }

}
