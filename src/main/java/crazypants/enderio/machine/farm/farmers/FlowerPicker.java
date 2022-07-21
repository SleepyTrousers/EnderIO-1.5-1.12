package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FlowerPicker implements IFarmerJoe {

    protected List<Block> flowers = new ArrayList<Block>();

    public FlowerPicker() {}

    public FlowerPicker add(Block... flowers) {
        for (Block block : flowers) {
            if (block != null) {
                this.flowers.add(block);
                FarmStationContainer.slotItemsProduce.add(new ItemStack(block));
            }
        }
        return this;
    }

    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        return false;
    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        for (Block flower : flowers) {
            if (block == flower) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlant(ItemStack stack) {
        return false;
    }

    @Override
    public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {

        if (!farm.hasHoe()) {
            farm.setNotification(TileFarmStation.NOTIFICATION_NO_HOE);
            return null;
        }

        World worldObj = farm.getWorldObj();
        List<EntityItem> result = new ArrayList<EntityItem>();

        ArrayList<ItemStack> drops = block.getDrops(worldObj, bc.x, bc.y, bc.z, meta, farm.getMaxLootingValue());
        farm.damageHoe(1, bc);
        farm.actionPerformed(false);
        if (drops != null) {
            for (ItemStack stack : drops) {
                result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
            }
        }

        worldObj.setBlockToAir(bc.x, bc.y, bc.z);

        return new HarvestResult(result, bc);
    }
}
