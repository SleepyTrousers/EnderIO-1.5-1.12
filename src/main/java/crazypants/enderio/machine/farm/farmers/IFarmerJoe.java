package crazypants.enderio.machine.farm.farmers;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IFarmerJoe {

  boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState state);

  boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState state);

  boolean canPlant(ItemStack stack);

  IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState state);

}
