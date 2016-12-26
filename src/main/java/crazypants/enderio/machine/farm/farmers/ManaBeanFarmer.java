package crazypants.enderio.machine.farm.farmers;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManaBeanFarmer extends CustomSeedFarmer {

  public ManaBeanFarmer(Block block, ItemStack stack) {
    super(block, stack);
    this.requiresFarmland = false;
  }

  @Override
  protected boolean canPlant(TileFarmStation farm, World worldObj, BlockPos bc) {
    return getPlantedBlock().canPlaceBlockOnSide(worldObj, bc, EnumFacing.DOWN);
  }
}
