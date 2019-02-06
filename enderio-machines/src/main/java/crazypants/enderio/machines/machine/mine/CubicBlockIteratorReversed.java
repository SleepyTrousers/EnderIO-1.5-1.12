package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.blockiterators.AbstractBlockIterator;

import net.minecraft.util.math.BlockPos;

// TODO: feed this back into ec

public class CubicBlockIteratorReversed extends AbstractBlockIterator {
  protected final int minX, minY, minZ;
  protected final int maxX, maxY, maxZ;
  protected int curX, curY, curZ;

  protected CubicBlockIteratorReversed(@Nonnull BlockPos base, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    super(base);
    this.minX = curX = Math.min(minX, maxX);
    this.minY = Math.min(minY, maxY);
    this.minZ = curZ = Math.min(minZ, maxZ);
    this.maxX = Math.max(minX, maxX);
    this.maxY = curY = Math.max(minY, maxY);
    this.maxZ = Math.max(minZ, maxZ);
  }

  public CubicBlockIteratorReversed(@Nonnull BlockPos base, int radius) {
    this(base, base.getX() - radius, base.getY() - radius, base.getZ() - radius, base.getX() + radius, base.getY() + radius, base.getZ() + radius);
  }

  public CubicBlockIteratorReversed(@Nonnull BlockPos pos0, @Nonnull BlockPos pos1) {
    this(pos0, pos0.getX(), pos0.getY(), pos0.getZ(), pos1.getX(), pos1.getY(), pos1.getZ());
  }

  public CubicBlockIteratorReversed(@Nonnull BoundingBox bb) {
    this(new BlockPos(bb.getCenter()), (int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
  }

  @Override
  public @Nonnull BlockPos next() {
    BlockPos ret = new BlockPos(curX, curY, curZ);
    if (++curX > maxX) {
      curX = minX;
      if (++curZ > maxZ) {
        curZ = minZ;
        --curY;
      }
    }
    return ret;
  }

  @Override
  public boolean hasNext() {
    return curY >= minY;
  }
}
