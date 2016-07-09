package crazypants.enderio.machine.farm.farmers;

import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class NaturaBerryFarmer extends PickableFarmer {

  public NaturaBerryFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {

    if(block != getPlantedBlock()) {
      return null;
    }
    if(!farm.hasHoe()) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    IHarvestResult res = new HarvestResult();

    BlockCoord checkBlock = bc;
    for (int i = 0; i < 5 && farm.hasHoe(); i++) {
      meta = farm.getBlockState(checkBlock.getBlockPos());
      block = farm.getBlock(checkBlock);

      if(super.canHarvest(farm, bc, block, meta)) { // redundant check because our canHarvest checks all 5 blocks so a bush may be invalid in the stack of 5
        IHarvestResult blockRes = super.harvestBlock(farm, checkBlock, block, meta);

        if(blockRes != null) {
          res.getHarvestedBlocks().add(checkBlock.getBlockPos());
          List<EntityItem> addToDrops = blockRes.getDrops();
          for (EntityItem stack : addToDrops) {
            res.getDrops().add(stack);
          }
        }
      }

      checkBlock = checkBlock.getLocation(EnumFacing.UP);
    }

    if(res.getHarvestedBlocks().isEmpty()) {
      return null;
    }

    return res;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState bs) {
    int meta = bs.getBlock().getMetaFromState(bs);
    if(!Config.farmEssenceBerriesEnabled && "tile.ore.berries.two".equals(block.getUnlocalizedName()) && meta == grownBlockMeta) {
      return false;
    }
    BlockCoord checkBlock = bc;
    for (int i = 0; i < 5; i++) {
      bs = farm.getBlockState(checkBlock.getBlockPos());
      block = checkBlock.getBlock(farm.getWorld());
      if(super.canHarvest(farm, checkBlock, block, bs)) {
        return true;
      }
      checkBlock = checkBlock.getLocation(EnumFacing.UP);
    }

    return false;
  }
}