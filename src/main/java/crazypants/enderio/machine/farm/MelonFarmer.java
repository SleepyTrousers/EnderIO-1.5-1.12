package crazypants.enderio.machine.farm;

import crazypants.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MelonFarmer extends CustomSeedFarmer {

  private Block grownBlock;

  public MelonFarmer(Block plantedBlock, Block grownBlock, ItemStack seeds) {
    super(plantedBlock, seeds);
    this.grownBlock = grownBlock;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
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
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    return block == grownBlock;
  }

}
