package crazypants.enderio.machine.farm.farmers;

import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Things;
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

  private final static Things LEAVES = new Things("treeLeaves");

  private int horizontalRange;
  private int verticalRange;
  private BlockPos origin;

  public TreeHarvestUtil() {
  }

  public void harvest(TileFarmStation farm, TreeFarmer farmer, BlockPos bc, HarvestResult res) {
    horizontalRange = farm.getFarmSize() + 7;
    verticalRange = 30;
    harvest(farm.getWorld(), farm.getPos(), bc, res, farmer.getIgnoreMeta());
  }

  public void harvest(World world, BlockPos bc, HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = bc.toImmutable();
    IBlockState wood = world.getBlockState(bc);
    harvestUp(world, bc, res, new HarvestTarget(wood));
  }

  private void harvest(World world, BlockPos originIn, BlockPos bc, HarvestResult res, boolean ignoreMeta) {
    this.origin = originIn.toImmutable();
    IBlockState wood = world.getBlockState(bc);
    if (ignoreMeta) {
      harvestUp(world, bc, res, new BaseHarvestTarget(wood.getBlock()));
    } else {
      harvestUp(world, bc, res, new HarvestTarget(wood));
    }
  }

  protected void harvestUp(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {
    if (!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
      return;
    }
    IBlockState bs = world.getBlockState(bc);
    boolean isLeaves = isLeaves(bs);
    if (target.isTarget(bs) || isLeaves) {
      res.harvestedBlocks.add(bc);
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (dir != EnumFacing.DOWN) {
          harvestUp(world, bc.offset(dir), res, target);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res, target);
      // and another check for large oaks, where wood can be surrounded by
      // leaves
      for (EnumFacing dir : EnumFacing.HORIZONTALS) {
        BlockPos loc = bc.offset(dir);
        IBlockState locBS = world.getBlockState(loc);
        if (isLeaves(locBS)) {
          harvestAdjacentWood(world, loc, res, target);
        }
      }
    }

  }

  static boolean isLeaves(IBlockState bs) {
    return bs.getMaterial() == Material.LEAVES || LEAVES.contains(bs.getBlock());
  }

  private void harvestAdjacentWood(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {
    for (EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos targ = bc.offset(dir);
      if (target.isTarget(world.getBlockState(targ))) {
        harvestUp(world, targ, res, target);
      }
    }
  }

  private boolean isInHarvestBounds(BlockPos bc) {

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

    HarvestTarget(IBlockState bs) {
      super(bs.getBlock());
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

    BaseHarvestTarget(Block wood) {
      this.wood = wood;
    }

    boolean isTarget(IBlockState bs) {
      return bs.getBlock() == wood;
    }
  }

}
