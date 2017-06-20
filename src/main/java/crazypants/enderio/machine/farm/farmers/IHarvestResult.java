package crazypants.enderio.machine.farm.farmers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IHarvestResult {

  List<EntityItem> getDrops();

  List<BlockPos> getHarvestedBlocks();

}
