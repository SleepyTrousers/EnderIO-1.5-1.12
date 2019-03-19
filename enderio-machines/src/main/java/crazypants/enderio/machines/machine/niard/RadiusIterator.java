package crazypants.enderio.machines.machine.niard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class RadiusIterator {

  private final List<BlockPos> bcl = new ArrayList<>();
  private int idx = -1;

  public RadiusIterator(BlockPos bc, int radius) {
    bcl.add(bc);
    for (int i = 1; i <= radius; i++) {
      for (int j = -i; j < i; j++) {
        bcl.add(new BlockPos(bc.getX() - i, bc.getY(), bc.getZ() + j));
        bcl.add(new BlockPos(bc.getX() + i, bc.getY(), bc.getZ() - j));
        bcl.add(new BlockPos(bc.getX() + j, bc.getY(), bc.getZ() + i));
        bcl.add(new BlockPos(bc.getX() - j, bc.getY(), bc.getZ() - i));
      }
    }
  }

  public BlockPos next() {
    if (++idx >= bcl.size()) {
      idx = 0;
    }
    return bcl.get(idx);
  }

  public int size() {
    return bcl.size();
  }

}