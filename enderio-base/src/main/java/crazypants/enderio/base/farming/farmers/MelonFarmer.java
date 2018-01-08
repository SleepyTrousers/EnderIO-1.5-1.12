package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmer;
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
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    int xVal = farm.getLocation().getX() & 1;
    int zVal = farm.getLocation().getZ() & 1;
    if ((bc.getX() & 1) != xVal || (bc.getZ() & 1) != zVal) {
      // if we have melon seeds, we still want to return true here so they are not planted by the default plantable handlers
      return canPlant(farm.getSeedTypeInSuppliesFor(bc));
    }
    return super.prepareBlock(farm, bc, block, meta);
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return block == grownBlock;
  }

}
