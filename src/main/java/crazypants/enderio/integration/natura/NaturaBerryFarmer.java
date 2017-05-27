package crazypants.enderio.integration.natura;


import javax.annotation.Nonnull;

import crazypants.enderio.farming.FarmNotification;
import crazypants.enderio.farming.IFarmer;
import crazypants.enderio.farming.farmers.HarvestResult;
import crazypants.enderio.farming.farmers.IHarvestResult;
import crazypants.enderio.farming.farmers.PickableFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NaturaBerryFarmer extends PickableFarmer {

  public NaturaBerryFarmer(@Nonnull Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, @Nonnull ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
    checkGroundForFarmland = requiresFarmland = false;
  }

  @Override
  public IHarvestResult harvestBlock(IFarmer farm, BlockPos bc, Block block, IBlockState meta) {
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
  public boolean canHarvest(IFarmer farm, BlockPos bc, Block block, IBlockState bs) {
    BlockPos checkBlock = bc;
    while (checkBlock.getY() <= 255) {
      if (block != getPlantedBlock()) {
        return false;
      }
      if (super.canHarvest(farm, checkBlock, block, bs)) {
        return true;
      }
      checkBlock = checkBlock.up();
      bs = farm.getBlockState(checkBlock);
      block = bs.getBlock();
    }
    return false;
  }
}