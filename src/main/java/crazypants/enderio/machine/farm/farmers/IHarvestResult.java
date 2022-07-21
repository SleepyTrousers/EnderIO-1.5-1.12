package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import java.util.List;
import net.minecraft.entity.item.EntityItem;

public interface IHarvestResult {

    List<EntityItem> getDrops();

    List<BlockCoord> getHarvestedBlocks();
}
