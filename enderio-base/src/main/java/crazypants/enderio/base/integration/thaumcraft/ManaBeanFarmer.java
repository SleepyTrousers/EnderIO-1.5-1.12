package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.base.farming.farmers.CustomSeedFarmer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManaBeanFarmer extends CustomSeedFarmer {

  public ManaBeanFarmer(@Nonnull Block block, @Nonnull ItemStack stack) {
    super(block, stack);
    this.requiresTilling = false;
  }

  @Override
  protected boolean canPlant(@Nonnull IFarmer farm, @Nonnull World world, @Nonnull BlockPos bc) {
    return getPlantedBlock().canPlaceBlockOnSide(world, bc, EnumFacing.DOWN);
  }

}
