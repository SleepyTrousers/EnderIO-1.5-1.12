package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.farming.IFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeHarvestUtil {

  private final static @Nonnull Things LEAVES = new Things("treeLeaves");
  private final static @Nonnull Things NOT_LEAVES = new Things();

  private int horizontalRange;
  private int verticalRange;
  private BlockPos origin;

  public TreeHarvestUtil() {
  }

  public void harvest(@Nonnull IFarmer farm, @Nonnull TreeFarmer farmer, @Nonnull BlockPos bc, @Nonnull HarvestResult res) {
    horizontalRange = farm.getFarmSize() + 7;
    verticalRange = 30;
    origin = farm.getLocation().toImmutable();
    if (farmer.getIgnoreMeta()) {
      harvestUp(farm.getWorld(), bc, res, new BaseHarvestTarget(null, farmer));
    } else {
      harvestUp(farm.getWorld(), bc, res, new HarvestTarget(null, farmer));
    }
  }

  public void harvest(@Nonnull World world, @Nonnull BlockPos bc, @Nonnull HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = bc.toImmutable();
    IBlockState wood = world.getBlockState(bc);
    harvestUp(world, bc, res, new HarvestTarget(wood, null));
  }

  protected void harvestUp(@Nonnull final World world, @Nonnull final BlockPos bc, @Nonnull final HarvestResult res, @Nonnull final BaseHarvestTarget target) {
    if (!isInHarvestBounds(bc) || res.getHarvestedBlocks().contains(bc)) {
      return;
    }
    IBlockState bs = world.getBlockState(bc);
    boolean isLeaves = isLeaves(bs);
    if (target.isTarget(bs) || isLeaves) {
      res.getHarvestedBlocks().add(bc);
      NNList.FACING.apply(new Callback<EnumFacing>() {
        @Override
        public void apply(@Nonnull EnumFacing dir) {
          if (dir != EnumFacing.DOWN) {
            harvestUp(world, bc.offset(dir), res, target);
          }
        }
      });
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res, target);
      // check the lower diagonals for connected wood
      harvestAdjacentWood(world, bc.down(), res, target);
      // and another check for large oaks, where wood can be surrounded by
      // leaves
      NNList.FACING_HORIZONTAL.apply(new Callback<EnumFacing>() {
        @Override
        public void apply(@Nonnull EnumFacing dir) {
          BlockPos loc = bc.offset(dir);
          IBlockState locBS = world.getBlockState(loc);
          if (isLeaves(locBS)) {
            harvestAdjacentWood(world, loc, res, target);
          }
        }
      });
    }
  }

  static boolean isLeaves(@Nonnull IBlockState bs) {
    return (bs.getMaterial() == Material.LEAVES || LEAVES.contains(bs.getBlock())) && !NOT_LEAVES.contains(bs.getBlock());
  }

  private void harvestAdjacentWood(@Nonnull final World world, @Nonnull final BlockPos bc, @Nonnull final HarvestResult res,
      @Nonnull final BaseHarvestTarget target) {
    NNList.FACING_HORIZONTAL.apply(new Callback<EnumFacing>() {
      @Override
      public void apply(@Nonnull EnumFacing dir) {
        BlockPos targ = bc.offset(dir);
        if (target.isTarget(world.getBlockState(targ))) {
          harvestUp(world, targ, res, target);
        }
      }
    });
  }

  private boolean isInHarvestBounds(@Nonnull BlockPos bc) {
    int dist = Math.abs(origin.getX() - bc.getX());
    if (dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.getZ() - bc.getZ());
    if (dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.getY() - bc.getY());
    if (dist > verticalRange) {
      return false;
    }
    return true;
  }

  private static final class HarvestTarget extends BaseHarvestTarget {

    final EnumType variant;

    HarvestTarget(IBlockState bs, TreeFarmer farmer) {
      super(bs == null ? null : bs.getBlock(), farmer);
      variant = getVariant(bs);
    }

    static EnumType getVariant(IBlockState bs) {
      try {
        return bs.getValue(BlockNewLog.VARIANT);
      } catch (Exception e) {
      }
      try {
        return bs.getValue(BlockOldLog.VARIANT);
      } catch (Exception e) {
      }
      return null;
    }

    @Override
    boolean isTarget(IBlockState bs) {
      if (variant == null) {
        return super.isTarget(bs);
      }
      return super.isTarget(bs) && variant == getVariant(bs);
    }
  }

  private static class BaseHarvestTarget {

    private final Block wood;
    private final TreeFarmer farmer;

    BaseHarvestTarget(Block wood, TreeFarmer farmer) {
      this.wood = wood;
      this.farmer = farmer;
    }

    boolean isTarget(IBlockState bs) {
      return farmer != null ? farmer.isWood(bs.getBlock()) : bs.getBlock() == wood;
    }
  }

  public static void addLeavesExcemption(Block block) {
    NOT_LEAVES.add(block);
  }
}
