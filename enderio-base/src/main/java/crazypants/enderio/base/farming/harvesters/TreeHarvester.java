package crazypants.enderio.base.farming.harvesters;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.farm.IHarvestResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeHarvester implements Callback<BlockPos> {

  public static void harvest(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IHarvestResult res,
      @Nonnull final IHarvestingTarget target) {
    final TreeHarvester visitor = new TreeHarvester(world, pos, res.getHarvestedBlocks(), target);
    while (visitor.hasNext()) {
      NNList.SHELL.apply(visitor);
    }
  }

  private final @Nonnull NNList<BlockPos> candidates = new NNList<>();
  private final @Nonnull Set<BlockPos> seen = new HashSet<>();
  private final @Nonnull World world;
  private final @Nonnull NonNullList<BlockPos> result;
  private final @Nonnull IHarvestingTarget target;
  private @Nonnull BlockPos next;

  private TreeHarvester(@Nonnull World world, @Nonnull BlockPos start, @Nonnull NonNullList<BlockPos> result, @Nonnull IHarvestingTarget target) {
    this.world = world;
    this.result = result;
    this.next = start;
    this.target = target;
    candidates.add(start);
    seen.add(start);
    result.add(start);
  }

  boolean hasNext() {
    if (candidates.isEmpty()) {
      return false;
    } else {
      next = candidates.remove(0);
      return true;
    }
  }

  @Override
  public void apply(@Nonnull BlockPos offset) {
    final BlockPos neighbor = next.add(offset);
    if (!seen.contains(neighbor) && target.isInBounds(neighbor)) {
      seen.add(neighbor);
      if (world.isBlockLoaded(neighbor)) {
        IBlockState blockState = world.getBlockState(neighbor);
        if (target.isTarget(blockState)) {
          result.add(neighbor);
          candidates.add(neighbor);
        }
      }
    }
  }

}
