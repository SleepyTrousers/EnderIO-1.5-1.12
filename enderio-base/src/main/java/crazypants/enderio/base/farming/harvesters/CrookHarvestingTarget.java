package crazypants.enderio.base.farming.harvesters;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class CrookHarvestingTarget implements IHarvestingTarget {

  private final @Nonnull BoundingBox bb;

  public CrookHarvestingTarget(@Nonnull BlockPos pos) {
    this.bb = new BoundingBox(pos, pos.up(30)).expand(12, 0, 12);
  }

  @Override
  public boolean isWood(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean isInBounds(@Nonnull BlockPos pos) {
    return bb.contains(pos);
  }

}
