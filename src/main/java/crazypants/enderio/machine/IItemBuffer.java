package crazypants.enderio.machine;

import crazypants.util.BlockCoord;

public interface IItemBuffer {

  void setBufferStacks(boolean bufferStacks);

  boolean isBufferStacks();

  BlockCoord getLocation();

}
