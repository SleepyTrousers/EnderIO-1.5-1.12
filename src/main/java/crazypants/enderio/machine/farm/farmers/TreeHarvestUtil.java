package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.BlockCoord;

public class TreeHarvestUtil {

  public static boolean canDropApples(Block block, int meta) {
    return 
        (block instanceof BlockOldLeaf && (meta == 0 || meta == 8)) || //oak
            (block instanceof BlockNewLeaf && (meta == 1 || meta == 9)); //giant oak
  }
  
  private int horizontalRange;
  private int verticalRange;
  protected Block[] woods;
  private BlockCoord origin;
  
//  private List<Block> extraLeaves = new ArrayList<Block>();
  
  public TreeHarvestUtil() {
    //Dont get saplings if we do this
//    Block blk = GameRegistry.findBlock("Thaumcraft", "blockMagicalLeaves");
//    if(blk != null) {
//      extraLeaves.add(blk);
//    }
  }

  public void harvest(TileFarmStation farm, TreeFarmer farmer, BlockCoord bc, HarvestResult res) {
    woods = farmer.woods;
    horizontalRange = farm.getFarmSize() + 7;
    verticalRange = 30;
    harvest(farm.getWorldObj(), farm.getLocation(), bc, res);
  }
  
  public void harvest(World world, BlockCoord bc, HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = new BlockCoord(bc);
    woods = new Block[] {world.getBlock(bc.x, bc.y, bc.z)};
    harvestUp(world, bc, res);
  }
  
  private void harvest(World farm, BlockCoord origin, BlockCoord bc, HarvestResult res) {
    this.origin = new BlockCoord(origin);
    harvestUp(farm, bc, res);
  }
  
  protected void harvestUp(World world, BlockCoord bc, HarvestResult res) {

    if(!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
      return;
    }

    Block blk = world.getBlock(bc.x, bc.y,bc.z);
    boolean isLeaves = blk instanceof BlockLeaves;//|| extraLeaves.contains(blk);        
    if(isWood(blk) || isLeaves) {
      res.harvestedBlocks.add(bc);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != ForgeDirection.DOWN) {
          harvestUp(world, bc.getLocation(dir), res);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res);
      //and another check for large oaks, where wood can be surrounded by leaves
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir.offsetY == 0) {
          BlockCoord loc = bc.getLocation(dir);
          Block targetBlock = world.getBlock(loc.x,loc.y,loc.z);
          if(targetBlock instanceof BlockLeaves) {
            harvestAdjacentWood(world, bc, res);
          }
        }
      }
    }

  }

  private void harvestAdjacentWood(World world, BlockCoord bc, HarvestResult res) {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(dir.offsetY == 0) {
        BlockCoord loc = bc.getLocation(dir);
        Block targetBlock = world.getBlock(loc.x, loc.y, loc.z);
        if(isWood(targetBlock)) {
          harvestUp(world, bc.getLocation(dir), res);
        }
      }
    }
  }

  private boolean isInHarvestBounds(BlockCoord bc) {
    
    int dist = Math.abs(origin.x - bc.x);
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.z - bc.z);
    if(dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(bc.y - horizontalRange);
    if(dist > verticalRange) {
      return false;
    }
    return true;
  }
  
  protected boolean isWood(Block block) {
    for(Block wood : woods) {
      if(block == wood) {
        return true;
      }
    }
    return false;
  }
  
}
