package crazypants.enderio.base.farming.farmers;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

public class HarvestResult implements IHarvestResult {

  private final @Nonnull NNList<EntityItem> drops;
  private final @Nonnull NNList<BlockPos> harvestedBlocks;

  public HarvestResult(@Nonnull List<EntityItem> drops, @Nonnull List<BlockPos> harvestedBlocks) {
    this.drops = NNList.wrap(drops);
    this.harvestedBlocks = NNList.wrap(harvestedBlocks);
  }

  public HarvestResult(@Nonnull NNList<EntityItem> drops, @Nonnull NNList<BlockPos> harvestedBlocks) {
    this.drops = drops;
    this.harvestedBlocks = harvestedBlocks;
  }

  public HarvestResult(@Nonnull NNList<EntityItem> drops, BlockPos harvestedBlock) {
    this.drops = drops;
    this.harvestedBlocks = new NNList<>();
    harvestedBlocks.add(harvestedBlock);
  }

  public HarvestResult() {
    drops = new NNList<>();
    harvestedBlocks = new NNList<>();
  }

  @Override
  public @Nonnull NNList<EntityItem> getDrops() {
    return drops;
  }

  @Override
  public @Nonnull NNList<BlockPos> getHarvestedBlocks() {
    return harvestedBlocks;
  }

}
