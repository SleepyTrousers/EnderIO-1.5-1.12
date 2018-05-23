package crazypants.enderio.base.integration.natura;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.farmers.HarvestResult;
import crazypants.enderio.base.farming.farmers.PickableFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NaturaBerryFarmer extends PickableFarmer {

  public NaturaBerryFarmer(@Nonnull Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, @Nonnull ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
    checkGroundForFarmland = requiresTilling = false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, final @Nonnull IBlockState stateIn) {
    IBlockState state = stateIn;
    Block block = state.getBlock();
    if (block != getPlantedBlock()) {
      return null;
    }
    if (!farm.hasTool(FarmingTool.HOE)) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    IHarvestResult res = new HarvestResult();

    BlockPos checkBlock = bc;
    while (checkBlock != null && checkBlock.getY() <= 255 && farm.hasTool(FarmingTool.HOE)) {
      state = farm.getBlockState(checkBlock);
      block = state.getBlock();
      if (block != getPlantedBlock()) {
        checkBlock = null;
      } else {

        if (getFullyGrownBlockMeta() == block.getMetaFromState(state)) {
          IHarvestResult blockRes = super.harvestBlock(farm, checkBlock, state);

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
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    BlockPos checkBlock = pos;
    Block block = state.getBlock();
    while (checkBlock.getY() <= 255) {
      if (block != getPlantedBlock()) {
        return false;
      }
      if (super.canHarvest(farm, checkBlock, state)) {
        return true;
      }
      checkBlock = checkBlock.up();
      state = farm.getBlockState(checkBlock);
      block = state.getBlock();
    }
    return false;
  }

}