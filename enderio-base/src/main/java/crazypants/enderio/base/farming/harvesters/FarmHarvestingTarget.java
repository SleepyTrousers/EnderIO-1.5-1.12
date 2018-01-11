package crazypants.enderio.base.farming.harvesters;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.base.farming.farmers.TreeFarmer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class FarmHarvestingTarget implements IHarvestingTarget {

  private final @Nonnull TreeFarmer farmer;
  private final @Nonnull BoundingBox bb;

  public FarmHarvestingTarget(@Nonnull TreeFarmer farmer, @Nonnull IFarmer farm) {
    this.farmer = farmer;
    this.bb = new BoundingBox(farm.getLocation(), farm.getLocation().up(30)).expand(farm.getFarmSize() + 7, 0, farm.getFarmSize() + 7);
  }

  @Override
  public boolean isWood(@Nonnull IBlockState state) {
    return farmer.isWood(state.getBlock());
  }

  @Override
  public boolean isInBounds(@Nonnull BlockPos pos) {
    return bb.contains(pos);
  }

}
