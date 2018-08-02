package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmingTool;
import crazypants.enderio.base.farming.FarmingTool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class MelonFarmer extends CustomSeedFarmer {

  private final @Nonnull Block grownBlock;

  public MelonFarmer(@Nonnull Block plantedBlock, @Nonnull Block grownBlock, @Nonnull ItemStack seeds) {
    super(plantedBlock, seeds);
    this.grownBlock = grownBlock;
  }

  @Override
  public Result tryPrepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    int xVal = farm.getLocation().getX() & 1;
    int zVal = farm.getLocation().getZ() & 1;
    if (((pos.getX() & 1) != xVal) ^ ((pos.getZ() & 1) != zVal)) {
      // if we have melon seeds, we still want to return true here so they are not planted by the default plantable handlers
      return canPlant(farm.getSeedTypeInSuppliesFor(pos)) ? Result.CLAIM : Result.NEXT;
    }
    return super.tryPrepareBlock(farm, pos, state);
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return state.getBlock() == grownBlock;
  }

  @Override
  protected @Nonnull IFarmingTool getHarvestTool() {
    return FarmingTool.AXE;
  }

  @Override
  protected @Nonnull FarmNotification getNoHarvestToolNotification() {
    return FarmNotification.NO_AXE;
  }

}
