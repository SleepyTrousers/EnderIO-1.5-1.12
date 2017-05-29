package crazypants.enderio.integration.thaumcraft;

import crazypants.enderio.farming.IFarmer;
import crazypants.enderio.farming.farmers.CustomSeedFarmer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManaBeanFarmer extends CustomSeedFarmer {

  public ManaBeanFarmer(Block block, ItemStack stack) {
    super(block, stack);
    this.requiresTilling = false;
  }

  @Override
  protected boolean canPlant(IFarmer farm, World world, BlockPos bc) {
    return getPlantedBlock().canPlaceBlockOnSide(world, bc, EnumFacing.DOWN);
  }
}
