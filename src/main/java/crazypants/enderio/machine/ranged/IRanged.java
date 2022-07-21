package crazypants.enderio.machine.ranged;

import com.enderio.core.common.util.BlockCoord;
import net.minecraft.world.World;

public interface IRanged {

    World getWorld();

    BlockCoord getLocation();

    float getRange();

    boolean isShowingRange();
}
