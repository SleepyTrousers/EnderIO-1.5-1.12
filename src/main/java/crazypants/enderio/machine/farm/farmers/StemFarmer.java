package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.machine.farm.TileFarmStation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class StemFarmer extends CustomSeedFarmer {

    private static final HeightCompatator COMP = new HeightCompatator();

    public StemFarmer(Block plantedBlock, ItemStack seeds) {
        super(plantedBlock, seeds);
    }

    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (plantedBlock == block) {
            return true;
        }
        return plantFromInventory(farm, bc);
    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        BlockCoord up = bc.getLocation(ForgeDirection.UP);
        Block upBLock = farm.getBlock(up);
        return upBLock == plantedBlock;
    }

    @Override
    public boolean canPlant(ItemStack stack) {
        return seeds.isItemEqual(stack);
    }

    @Override
    public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {

        HarvestResult res = new HarvestResult();
        BlockCoord harvestCoord = bc;
        boolean done = false;
        do {
            harvestCoord = harvestCoord.getLocation(ForgeDirection.UP);
            boolean hasHoe = farm.hasHoe();
            if (plantedBlock == farm.getBlock(harvestCoord) && hasHoe) {
                res.harvestedBlocks.add(harvestCoord);
                ArrayList<ItemStack> drops = plantedBlock.getDrops(
                        farm.getWorldObj(),
                        harvestCoord.x,
                        harvestCoord.y,
                        harvestCoord.z,
                        meta,
                        farm.getMaxLootingValue());
                if (drops != null) {
                    for (ItemStack drop : drops) {
                        res.drops.add(
                                new EntityItem(farm.getWorldObj(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, drop.copy()));
                    }
                }
                farm.damageHoe(1, harvestCoord);
                farm.actionPerformed(false);
            } else {
                if (!hasHoe) {
                    farm.setNotification(TileFarmStation.NOTIFICATION_NO_HOE);
                } else {
                    farm.clearNotification();
                }
                done = true;
            }
        } while (!done);

        List<BlockCoord> toClear = new ArrayList<BlockCoord>(res.getHarvestedBlocks());
        Collections.sort(toClear, COMP);
        for (BlockCoord coord : toClear) {
            farm.getWorldObj().setBlockToAir(coord.x, coord.y, coord.z);
        }

        return res;
    }

    @Override
    protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc) {
        World worldObj = farm.getWorldObj();
        if (canPlant(worldObj, bc) && farm.takeSeedFromSupplies(seeds, bc) != null) {
            return plant(farm, worldObj, bc);
        }
        return false;
    }

    private static class HeightCompatator implements Comparator<BlockCoord> {

        @Override
        public int compare(BlockCoord o1, BlockCoord o2) {
            return -compare(o1.y, o2.y);
        }

        public static int compare(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
