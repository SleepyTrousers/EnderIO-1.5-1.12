package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class MelonFarmer extends CustomSeedFarmer {

  private Block grownBlock;

  public MelonFarmer(Block plantedBlock, Block grownBlock, ItemStack seeds) {
    super(plantedBlock, seeds);
    this.grownBlock = grownBlock;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    int xVal = farm.getLocation().x % 2; 
    int zVal = farm.getLocation().z % 2;
    if(bc.x % 2 != xVal || bc.z % 2 != zVal) {
      //if we have melon seeds, we still want ot return true here so they are not planted by the default plantable
      //handlers
      return canPlant(farm.getSeedTypeInSuppliesFor(bc));
    }
    return super.prepareBlock(farm, bc, block, meta);
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    return block == grownBlock;
  }

}
