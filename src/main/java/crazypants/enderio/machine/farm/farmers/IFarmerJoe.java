package crazypants.enderio.machine.farm.farmers;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.TileFarmStation;

public interface IFarmerJoe {

  boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta);

  boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta);

  boolean canPlant(ItemStack stack);

  IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta);

}
