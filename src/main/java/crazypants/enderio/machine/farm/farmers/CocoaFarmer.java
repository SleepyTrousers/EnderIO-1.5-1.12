package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CocoaFarmer extends CustomSeedFarmer {
  public CocoaFarmer() {
    super(Blocks.cocoa, new ItemStack(Items.dye, 1, 3));
    this.requiresFarmland = false;
    if (!Config.farmHarvestJungleWhenCocoa) {
      this.disableTreeFarm = true;
    }
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    //return block == getPlantedBlock() && (meta & 12) >> 2 >= 2;
    //TODO: 1.8
    return false;
  }

  @Override
  protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc) {
//    worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
//    EnumFacing dir = getPlantDirection(worldObj, bc);
//    if (dir == null) {
//      return false;
//    }
//    worldObj.setBlock(bc.x, bc.y, bc.z, getPlantedBlock(), Direction.facingToDirection[dir], 1 | 2);
//    farm.actionPerformed(false);
//    return true;
    //TODO: 1.8
    return false;
  }

  @Override
  protected boolean canPlant(World worldObj, BlockCoord bc) {
    return getPlantDirection(worldObj, bc) != null;
  }

  private EnumFacing getPlantDirection(World worldObj, BlockCoord bc) {
    if (!worldObj.isAirBlock(bc.getBlockPos())) {
      return null;
    }

    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
        
      int x = bc.x + dir.getFrontOffsetX();
      int y = bc.y + dir.getFrontOffsetY();
      int z = bc.z + dir.getFrontOffsetZ();
       BlockPos p = bc.getBlockPos().offset(dir);
      if (validBlock(worldObj.getBlockState(p)))
        return dir;
    }
    
    return null;
  }

  private boolean validBlock(IBlockState bs) {
    //TODO: 1.8
    //return bs.getBlock() == Blocks.log && BlockLog.func_150165_c(metadata) == 3;
    return bs.getBlock() == Blocks.log;
  }
}
