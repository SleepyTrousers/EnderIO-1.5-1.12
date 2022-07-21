package crazypants.enderio.machine;

import com.enderio.core.common.util.BlockCoord;

public interface IItemBuffer {

    void setBufferStacks(boolean bufferStacks);

    boolean isBufferStacks();

    BlockCoord getLocation();
}
