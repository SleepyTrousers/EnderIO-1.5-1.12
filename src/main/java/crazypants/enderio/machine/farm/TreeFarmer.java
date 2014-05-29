package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class TreeFarmer implements IFarmerJoe {

  private static final HeightComparator comp = new HeightComparator();

  protected Block sapling;
  protected ItemStack saplingItem;
  protected Block wood;

  public TreeFarmer(Block sapling, Block wood) {
    this.sapling = sapling;
    saplingItem = new ItemStack(sapling);
    this.wood = wood;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    return block == wood;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return stack != null && stack.getItem() == saplingItem.getItem();
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if(block == sapling) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    World worldObj = farm.getWorldObj();
    if(canPlant(worldObj, bc)) {
      ItemStack seed = farm.getSeedFromSupplies(saplingItem, bc, false);
      if(seed != null) {
        return plant(farm, worldObj, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(World worldObj, BlockCoord bc) {
    Block ground = worldObj.getBlock(bc.x, bc.y - 1, bc.z);
    IPlantable plantable = (IPlantable) sapling;
    if(sapling.canPlaceBlockAt(worldObj, bc.x, bc.y, bc.z) &&
        sapling.canBlockStay(worldObj, bc.x, bc.y, bc.z) &&
        ground.canSustainPlant(worldObj, bc.x, bc.y - 1, bc.z, ForgeDirection.UP, plantable)) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc, ItemStack seed) {
    worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
    if(canPlant(worldObj, bc)) {
      worldObj.setBlock(bc.x, bc.y, bc.z, sapling, seed.getItemDamage(), 1 | 2);
      farm.actionPerformed();
      return true;
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    HarvestResult res = new HarvestResult();
    harvestUp(farm, bc, res);
    Collections.sort(res.harvestedBlocks, comp);
    
    List<BlockCoord> actualHarvests = new ArrayList<BlockCoord>();

    for (int i = 0; i < res.harvestedBlocks.size() && farm.hasAxe(); i++) {
      BlockCoord coord = res.harvestedBlocks.get(i);
      Block blk = farm.getBlock(coord);

      ArrayList<ItemStack> drops = blk.getDrops(farm.getWorld(), bc.x, bc.y, bc.z, farm.getBlockMeta(coord), farm.getAxeLootingValue());
      if(drops != null) {
        for (ItemStack drop : drops) {
          res.drops.add(new EntityItem(farm.getWorld(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, drop.copy()));
        }
      }

      if(blk != wood) { //leaves
        int leaveMeta = farm.getBlockMeta(coord);        
        boolean canDropApple = 
            (blk instanceof BlockOldLeaf && (leaveMeta == 0 || leaveMeta == 8)) || //oak
            (blk instanceof BlockNewLeaf && (leaveMeta == 1 || leaveMeta == 9)); //giant oak

        if(canDropApple) {          
          if(farm.getWorldObj().rand.nextInt(200) == 0) {            
            res.drops.add(new EntityItem(farm.getWorld(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, new ItemStack(Items.apple)));
          }
        }               
      }
      farm.actionPerformed();
      farm.damageAxe();
      farm.getWorldObj().setBlockToAir(coord.x, coord.y, coord.z);
      actualHarvests.add(coord);
    }
    res.harvestedBlocks.clear();
    res.harvestedBlocks.addAll(actualHarvests);

    return res;
  }

  protected void harvestUp(TileFarmStation farm, BlockCoord bc, HarvestResult res) {

    if(!isInHarvestBounds(farm, bc) || res.harvestedBlocks.contains(bc)) {
      return;
    }

    Block blk = farm.getBlock(bc);
    boolean isLeaves = blk instanceof BlockLeaves;
    if(wood == blk || isLeaves) {
      res.harvestedBlocks.add(bc);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != ForgeDirection.DOWN) {
          harvestUp(farm, bc.getLocation(dir), res);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(farm, bc, res);
      //and another check for large oaks, where wood can be surrounded by leaves
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir.offsetY == 0) {
          Block targetBlock = farm.getBlock(bc.getLocation(dir));
          if(targetBlock instanceof BlockLeaves) {
            harvestAdjacentWood(farm, bc, res);
          }
        }
      }
    }

  }

  private void harvestAdjacentWood(TileFarmStation farm, BlockCoord bc, HarvestResult res) {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(dir.offsetY == 0) {
        Block targetBlock = farm.getBlock(bc.getLocation(dir));
        if(wood == targetBlock) {
          harvestUp(farm, bc.getLocation(dir), res);
        }
      }
    }
  }

  private boolean isInHarvestBounds(TileFarmStation farm, BlockCoord bc) {
    BlockCoord fLoc = farm.getLocation();
    int dist = Math.abs(fLoc.x - bc.x);
    if(dist > farm.getFarmSize() + 7) {
      return false;
    }
    dist = Math.abs(fLoc.z - bc.z);
    if(dist > farm.getFarmSize() + 7) {
      return false;
    }
    dist = Math.abs(bc.y - fLoc.y);
    if(dist > 30) {
      return false;
    }
    return true;
  }

  private static class HeightComparator implements Comparator<BlockCoord> {

    @Override
    public int compare(BlockCoord o1, BlockCoord o2) {
      return compare(o2.y, o1.y); //reverse order
    }

    //same as 1.7 Integer.compare
    public static int compare(int x, int y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

  }

}
