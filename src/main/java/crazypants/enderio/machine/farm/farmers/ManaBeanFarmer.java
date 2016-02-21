package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ManaBeanFarmer extends CustomSeedFarmer {
  
  public ManaBeanFarmer(Block block, ItemStack stack) {
    super(block, stack);
    this.requiresFarmland = false;
  }

  @Override
  protected boolean canPlant(World worldObj, BlockCoord bc) {
    return getPlantedBlock().canPlaceBlockOnSide(worldObj, bc.getBlockPos(), EnumFacing.DOWN);
  }
}
