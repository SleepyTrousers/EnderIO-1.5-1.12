package crazypants.enderio.machine.farm.farmers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.machine.farm.TileFarmStation;

public class TreeHarvestUtil {

    public static boolean canDropApples(Block block, int meta) {
        return (block instanceof BlockOldLeaf && (meta == 0 || meta == 8)) || // oak
                (block instanceof BlockNewLeaf && (meta == 1 || meta == 9)); // giant oak
    }

    private int horizontalRange;
    private int verticalRange;
    private BlockCoord origin;

    public TreeHarvestUtil() {}

    public void harvest(TileFarmStation farm, TreeFarmer farmer, BlockCoord bc, HarvestResult res) {
        horizontalRange = farm.getFarmSize() + 7;
        verticalRange = 30;
        harvest(farm.getWorldObj(), farm.getLocation(), bc, res, farmer.getIgnoreMeta());
    }

    public void harvest(World world, BlockCoord bc, HarvestResult res) {
        horizontalRange = 12;
        verticalRange = 30;
        origin = new BlockCoord(bc);
        Block wood = world.getBlock(bc.x, bc.y, bc.z);
        int woodMeta = world.getBlockMetadata(bc.x, bc.y, bc.z);
        harvestUp(world, bc, res, new HarvestTarget(wood, woodMeta));
    }

    private void harvest(World world, BlockCoord origin, BlockCoord bc, HarvestResult res, boolean ignoreMeta) {
        this.origin = new BlockCoord(origin);
        Block wood = world.getBlock(bc.x, bc.y, bc.z);
        int woodMeta = world.getBlockMetadata(bc.x, bc.y, bc.z);
        if (ignoreMeta) {
            harvestUp(world, bc, res, new BaseHarvestTarget(wood));
        } else {
            harvestUp(world, bc, res, new HarvestTarget(wood, woodMeta));
        }
    }

    protected void harvestUp(World world, BlockCoord bc, HarvestResult res, BaseHarvestTarget target) {

        if (!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
            return;
        }

        Block blk = world.getBlock(bc.x, bc.y, bc.z);
        boolean isLeaves = blk instanceof BlockLeaves;
        if (target.isTarget(blk, world.getBlockMetadata(bc.x, bc.y, bc.z)) || isLeaves) {
            res.harvestedBlocks.add(bc);
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (dir != ForgeDirection.DOWN) {
                    harvestUp(world, bc.getLocation(dir), res, target);
                }
            }
        } else {
            // check the sides for connected wood
            harvestAdjacentWood(world, bc, res, target);
            // and another check for large oaks, where wood can be surrounded by leaves
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (dir.offsetY == 0) {
                    BlockCoord loc = bc.getLocation(dir);
                    Block targetBlock = world.getBlock(loc.x, loc.y, loc.z);
                    if (targetBlock instanceof BlockLeaves) {
                        harvestAdjacentWood(world, bc, res, target);
                    }
                }
            }
        }
    }

    private void harvestAdjacentWood(World world, BlockCoord bc, HarvestResult res, BaseHarvestTarget target) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (dir.offsetY == 0) {
                BlockCoord loc = bc.getLocation(dir);
                Block targetBlock = world.getBlock(loc.x, loc.y, loc.z);
                if (target.isTarget(targetBlock, world.getBlockMetadata(loc.x, loc.y, loc.z))) {
                    harvestUp(world, bc.getLocation(dir), res, target);
                }
            }
        }
    }

    private boolean isInHarvestBounds(BlockCoord bc) {

        int dist = Math.abs(origin.x - bc.x);
        if (dist > horizontalRange) {
            return false;
        }
        dist = Math.abs(origin.z - bc.z);
        if (dist > horizontalRange) {
            return false;
        }
        dist = Math.abs(origin.y - bc.y);
        if (dist > verticalRange) {
            return false;
        }
        return true;
    }

    private static final class HarvestTarget extends BaseHarvestTarget {

        private final int woodMeta;

        HarvestTarget(Block wood, int woodMeta) {
            super(wood);
            this.woodMeta = woodMeta;
        }

        boolean isTarget(Block blk, int meta) {
            return super.isTarget(blk, meta) && ((meta & 3) == (woodMeta & 3));
        }
    }

    private static class BaseHarvestTarget {

        private final Block wood;

        BaseHarvestTarget(Block wood) {
            this.wood = wood;
        }

        boolean isTarget(Block blk, int meta) {
            return blk == wood;
        }
    }
}
