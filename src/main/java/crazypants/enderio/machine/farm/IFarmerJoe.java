package crazypants.enderio.machine.farm;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.util.BlockCoord;

public interface IFarmerJoe {

  boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta);

  boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta);

  boolean canPlant(ItemStack stack);

  IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta);

}
