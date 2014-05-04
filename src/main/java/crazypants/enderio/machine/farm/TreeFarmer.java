package crazypants.enderio.machine.farm;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class TreeFarmer implements IFarmerJoe {

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
    return res;
  }

  protected void harvestUp(TileFarmStation farm, BlockCoord bc, HarvestResult res) {

    if(wood == farm.getBlock(bc) && farm.hasAxe()) {
      res.harvestedBlocks.add(bc);
      ArrayList<ItemStack> drops = wood.getDrops(farm.getWorld(), bc.x, bc.y, bc.z, farm.getBlockMeta(bc), farm.geAxeLootingValue());
      if(drops != null) {
        for (ItemStack drop : drops) {
          res.drops.add(new EntityItem(farm.getWorld(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, drop.copy()));
        }

      }
      farm.damageAxe();
      farm.actionPerformed();
      farm.getWorld().setBlockToAir(bc.x, bc.y, bc.z);

      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != ForgeDirection.DOWN) {
          harvestUp(farm, bc.getLocation(dir), res);
        }
      }

    } else {
      //for new trees, check the sides
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir.offsetY == 0) {
          if(wood == farm.getBlock(bc.getLocation(dir))) {
            harvestUp(farm, bc.getLocation(dir), res);
          }
        }
      }
    }

  }

}
