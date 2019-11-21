package crazypants.enderio.base.farming.farmers;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.util.NNPair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class HarvestResult implements IHarvestResult {

  private final @Nonnull NNList<NNPair<BlockPos, ItemStack>> drops;
  private final @Nonnull NNList<BlockPos> harvestedBlocks;

  public HarvestResult(@Nonnull List<NNPair<BlockPos, ItemStack>> drops, @Nonnull List<BlockPos> harvestedBlocks) {
    this.drops = NNList.wrap(drops);
    this.harvestedBlocks = NNList.wrap(harvestedBlocks);
  }

  public HarvestResult(@Nonnull NNList<NNPair<BlockPos, ItemStack>> drops, @Nonnull NNList<BlockPos> harvestedBlocks) {
    this.drops = drops;
    this.harvestedBlocks = harvestedBlocks;
  }

  public HarvestResult(@Nonnull NNList<NNPair<BlockPos, ItemStack>> drops, BlockPos harvestedBlock) {
    this.drops = drops;
    this.harvestedBlocks = new NNList<>();
    harvestedBlocks.add(harvestedBlock);
  }

  public HarvestResult(BlockPos harvestedBlock) {
    this();
    harvestedBlocks.add(harvestedBlock);
  }

  public HarvestResult() {
    drops = new NNList<>();
    harvestedBlocks = new NNList<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull NNList<NNPair<BlockPos, ItemStack>> getDrops() {
    return drops;
  }

  @Override
  public void addDrop(@Nonnull BlockPos pos, @Nonnull ItemStack stack) {
    drops.add(NNPair.of(pos, stack));
  }

  @Override
  public @Nonnull NNList<BlockPos> getHarvestedBlocks() {
    return harvestedBlocks;
  }

}
