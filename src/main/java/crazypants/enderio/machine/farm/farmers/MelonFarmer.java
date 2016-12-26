package crazypants.enderio.machine.farm.farmers;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class MelonFarmer extends CustomSeedFarmer {

  private Block grownBlock;

  public MelonFarmer(Block plantedBlock, Block grownBlock, ItemStack seeds) {
    super(plantedBlock, seeds);
    this.grownBlock = grownBlock;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    int xVal = farm.getLocation().x & 1;
    int zVal = farm.getLocation().z & 1;
    if ((bc.getX() & 1) != xVal || (bc.getZ() & 1) != zVal) {
      // if we have melon seeds, we still want to return true here so they are not planted by the default plantable handlers
      return canPlant(farm.getSeedTypeInSuppliesFor(bc));
    }
    return super.prepareBlock(farm, bc, block, meta);
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    return block == grownBlock;
  }

}
