package crazypants.enderio.machine.farm.farmers;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

public interface IHarvestResult {

  List<EntityItem> getDrops();

  List<BlockPos> getHarvestedBlocks();

}
