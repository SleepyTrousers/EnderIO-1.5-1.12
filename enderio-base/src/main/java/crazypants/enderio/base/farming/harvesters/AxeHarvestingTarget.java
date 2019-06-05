package crazypants.enderio.base.farming.harvesters;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class AxeHarvestingTarget implements IHarvestingTarget {

  private final @Nonnull IBlockState wood;
  private final EnumType variant;
  private final @Nonnull BoundingBox bb;

  public AxeHarvestingTarget(@Nonnull IBlockState wood, @Nonnull BlockPos pos, int radius, int height) {
    this.wood = wood;
    this.variant = getVariant(wood);
    this.bb = new BoundingBox(pos, pos.up(height)).expand(radius, 0, radius);
  }

  private static EnumType getVariant(IBlockState bs) {
    if (bs.getProperties().containsKey(BlockNewLog.VARIANT)) {
      return bs.getValue(BlockNewLog.VARIANT);
    }
    if (bs.getProperties().containsKey(BlockOldLog.VARIANT)) {
      return bs.getValue(BlockOldLog.VARIANT);
    }
    return null;
  }

  @Override
  public boolean isWood(@Nonnull IBlockState state) {
    // shortcut for same blockstate, then the long check
    return state == wood || (state.getBlock() == wood.getBlock() && (variant == null || variant == getVariant(state)));
  }

  @Override
  public boolean isInBounds(@Nonnull BlockPos pos) {
    return bb.contains(pos);
  }

}
