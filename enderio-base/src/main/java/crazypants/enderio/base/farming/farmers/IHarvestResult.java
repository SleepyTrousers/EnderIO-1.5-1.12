package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

public interface IHarvestResult {

  @Nonnull
  NNList<EntityItem> getDrops();

  @Nonnull
  NNList<BlockPos> getHarvestedBlocks();

}
