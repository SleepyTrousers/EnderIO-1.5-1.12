package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;

import com.enderio.core.common.util.BlockCoord;

public class HarvestResult implements IHarvestResult {

    List<EntityItem> drops;
    List<BlockCoord> harvestedBlocks;

    public HarvestResult(List<EntityItem> drops, List<BlockCoord> harvestedBlocks) {
        this.drops = drops;
        this.harvestedBlocks = harvestedBlocks;
    }

    public HarvestResult(List<EntityItem> drops, BlockCoord harvestedBlock) {
        this.drops = drops;
        this.harvestedBlocks = new ArrayList<BlockCoord>();
        harvestedBlocks.add(harvestedBlock);
    }

    public HarvestResult() {
        drops = new ArrayList<EntityItem>();
        harvestedBlocks = new ArrayList<BlockCoord>();
    }

    @Override
    public List<EntityItem> getDrops() {
        return drops;
    }

    @Override
    public List<BlockCoord> getHarvestedBlocks() {
        return harvestedBlocks;
    }
}
