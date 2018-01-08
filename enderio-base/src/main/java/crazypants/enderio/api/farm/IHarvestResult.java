package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

/**
 * This represents the result of a harvest.
 * 
 * @author Henry Loenwind
 *
 */
public interface IHarvestResult {

  /**
   * @return A list of harvested items and the locations they were harvested at. They will be dropped at those locations if the Farming Station's output is
   *         full.
   */
  @Nonnull
  NonNullList<EntityItem> getDrops();

  /**
   * @return A list of locations which were harvested. These are used for the FX particles.
   */
  @Nonnull
  NonNullList<BlockPos> getHarvestedBlocks();

}
