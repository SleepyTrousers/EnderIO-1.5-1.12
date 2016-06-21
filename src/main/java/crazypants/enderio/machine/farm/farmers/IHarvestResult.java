package crazypants.enderio.machine.farm.farmers;

import java.util.List;

import net.minecraft.entity.item.EntityItem;

import com.enderio.core.common.util.BlockCoord;

public interface IHarvestResult {

  List<EntityItem> getDrops();
  List<BlockCoord> getHarvestedBlocks();

}
