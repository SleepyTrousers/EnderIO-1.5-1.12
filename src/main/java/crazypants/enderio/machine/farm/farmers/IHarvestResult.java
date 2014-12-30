package crazypants.enderio.machine.farm.farmers;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import crazypants.util.BlockCoord;

public interface IHarvestResult {

  List<EntityItem> getDrops();
  List<BlockCoord> getHarvestedBlocks();

}
