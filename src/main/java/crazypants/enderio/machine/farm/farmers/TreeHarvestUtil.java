package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TreeHarvestUtil {

 
  private int horizontalRange;
  private int verticalRange;
  private BlockCoord origin;
  
  public TreeHarvestUtil() {
  }

  public void harvest(TileFarmStation farm, TreeFarmer farmer, BlockPos bc, HarvestResult res) {
    horizontalRange = farm.getFarmSize() + 7;
    verticalRange = 30;
    harvest(farm.getWorld(), farm.getLocation(), bc, res, farmer.getIgnoreMeta());
  }
  
  public void harvest(World world, BlockPos bc, HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = new BlockCoord(bc);
    IBlockState wood = world.getBlockState(bc);    
    harvestUp(world, bc, res, new HarvestTarget(wood));
  }
  
  private void harvest(World world, BlockCoord origin, BlockPos bc, HarvestResult res, boolean ignoreMeta) {
    this.origin = new BlockCoord(origin);
    IBlockState wood = world.getBlockState(bc);    
    if (ignoreMeta) {
      harvestUp(world, bc, res, new BaseHarvestTarget(wood.getBlock()));
    } else {
      harvestUp(world, bc, res, new HarvestTarget(wood));
    }
  }
  
  protected void harvestUp(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {

    if(!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
      return;
    }
    IBlockState bs = world.getBlockState(bc);
    Block blk = bs.getBlock();
    boolean isLeaves = blk.getMaterial() == Material.leaves;    
    if(target.isTarget(bs) || isLeaves) {
      res.harvestedBlocks.add(bc);
      for (EnumFacing dir : EnumFacing.VALUES) {
        if(dir != EnumFacing.DOWN) {
          harvestUp(world, bc.offset(dir), res, target);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res, target);
      //and another check for large oaks, where wood can be surrounded by leaves
      for (EnumFacing dir : EnumFacing.VALUES) {
        if(dir.getFrontOffsetY() == 0) {
          BlockPos loc = bc.offset(dir);
          Block targetBlock = world.getBlockState(loc).getBlock();
          if(targetBlock.getMaterial() == Material.leaves) {
            harvestAdjacentWood(world, bc, res, target);
          }
        }
      }
    }

  }

  private void harvestAdjacentWood(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {
    for (EnumFacing dir : EnumFacing.VALUES) {
      if(dir.getFrontOffsetY() == 0) {        
        IBlockState targetBS = world.getBlockState(bc);        
        if(target.isTarget(targetBS)) {
          harvestUp(world, bc.offset(dir), res, target);
        }
      }
    }
  }

  private boolean isInHarvestBounds(BlockPos bc) {
    
    int dist = Math.abs(origin.x - bc.getX());
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.z - bc.getZ());
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.y - bc.getY());
    if(dist > verticalRange) {
      return false;
    }
    return true;
  }
  
  private static final class HarvestTarget extends BaseHarvestTarget {
    
    IBlockState bs;
    EnumType variant; 

    HarvestTarget(IBlockState bs) {
      super(bs.getBlock());
      this.bs = bs;
      variant = getVariant(bs);
    }

    private EnumType getVariant(IBlockState bs2) {
      EnumType v = bs.getValue(BlockNewLog.VARIANT);
      if(v == null) {
        v = bs.getValue(BlockOldLog.VARIANT);
      }
      return v;
    }

    @Override
    boolean isTarget(IBlockState bs) {
      if(variant == null) {
        return super.isTarget(bs);
      }
      return super.isTarget(bs) && variant == getVariant(bs);
    }
  }

  private static class BaseHarvestTarget {
    
    private final Block wood;

    BaseHarvestTarget(Block wood) {
      this.wood = wood;
    }

    boolean isTarget(IBlockState bs) {
      return bs.getBlock() == wood;
    }
  }

}
