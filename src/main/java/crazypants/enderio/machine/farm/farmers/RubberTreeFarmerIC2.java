package crazypants.enderio.machine.farm.farmers;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;

public class RubberTreeFarmerIC2 extends TreeFarmer {

    public static Class<?> treeTap;
    private ItemStack stickyResin;

    public RubberTreeFarmerIC2() {
        super(true, GameRegistry.findBlock("IC2", "blockRubSapling"), GameRegistry.findBlock("IC2", "blockRubWood"));
        Item item = GameRegistry.findItem("IC2", "itemTreetap");
        if (item != null) {
            treeTap = item.getClass();
        }
        item = GameRegistry.findItem("IC2", "itemHarz");
        if (item != null) {
            stickyResin = new ItemStack(item);
            FarmStationContainer.slotItemsProduce.add(stickyResin);
        }
    }

    public boolean isValid() {
        return woods != null && woods.length > 0
                && sapling != null
                && saplingItem != null
                && treeTap != null
                && stickyResin != null;
    }

    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (!farm.hasAxe()) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    Block blk = farm.getBlock(bc.x + x, bc.y, bc.z + z);
                    if (isWood(blk) || sapling == blk) {
                        return false;
                    }
                }
            }
        }
        return super.prepareBlock(farm, bc, block, meta);
    }

    @Override
    public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (farm.hasAxe()) {
            return super.harvestBlock(farm, bc, block, meta);
        }
        HarvestResult res = new HarvestResult();
        int y = bc.y;
        boolean done = false;
        while (!done && farm.hasTool(ToolType.TREETAP)) {
            bc = new BlockCoord(bc.x, y, bc.z);
            block = farm.getBlock(bc);
            if (!isWood(block)) {
                done = true;
            } else {
                meta = farm.getBlockMeta(bc);
                if (attemptHarvest(res, farm.getWorldObj(), bc.x, y, bc.z, meta)) {
                    farm.damageTool(ToolType.TREETAP, woods[0], bc, 1);
                }
            }
            y++;
        }
        return res;
    }

    private boolean attemptHarvest(HarvestResult res, World world, int x, int y, int z, int meta) {
        if (meta > 1 && meta < 6) {
            world.setBlockMetadataWithNotify(x, y, z, meta + 6, 3);
            world.scheduleBlockUpdate(x, y, z, woods[0], woods[0].tickRate(world));
            ItemStack drop = stickyResin.copy();
            drop.stackSize = world.rand.nextInt(3) + 1;
            EntityItem dropEnt = new EntityItem(world, x + 0.5, y + 1, z + 0.5, drop);
            res.getDrops().add(dropEnt);
            return true;
        }
        return false;
    }
}
