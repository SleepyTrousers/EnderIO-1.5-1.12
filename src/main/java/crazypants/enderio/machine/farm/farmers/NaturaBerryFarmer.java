package crazypants.enderio.machine.farm.farmers;


import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NaturaBerryFarmer extends PickableFarmer {

  public NaturaBerryFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
    checkGroundForFarmland = requiresFarmland = false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    if (block != getPlantedBlock()) {
      return null;
    }
    if (!farm.hasHoe()) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    IHarvestResult res = new HarvestResult();

    BlockPos checkBlock = bc;
    while (checkBlock != null && checkBlock.getY() <= 255 && farm.hasHoe()) {
      meta = farm.getBlockState(checkBlock);
      block = farm.getBlock(checkBlock);
      if (block != getPlantedBlock()) {
        checkBlock = null;
      } else {

        if (getFullyGrownBlockMeta() == block.getMetaFromState(meta)) {
          IHarvestResult blockRes = super.harvestBlock(farm, checkBlock, block, meta);

          if (blockRes != null) {
            res.getHarvestedBlocks().add(checkBlock);
            res.getDrops().addAll(blockRes.getDrops());
          }
        }

        checkBlock = checkBlock.up();
      }
    }

    if (res.getHarvestedBlocks().isEmpty()) {
      return null;
    }

    return res;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState bs) {
    BlockPos checkBlock = bc;
    while (checkBlock.getY() <= 255) {
      bs = farm.getBlockState(checkBlock);
      block = bs.getBlock();
      if (block != getPlantedBlock()) {
        return false;
      }
      if (super.canHarvest(farm, checkBlock, block, bs)) {
        return true;
      }
      checkBlock = checkBlock.up();
    }
    return false;
  }
}