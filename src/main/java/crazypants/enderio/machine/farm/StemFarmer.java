package crazypants.enderio.machine.farm;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class StemFarmer extends SeedFarmer {


  public StemFarmer(Block plantedBlock, ItemStack seeds) {
    super(plantedBlock, seeds);
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if(plantedBlock == block) {
      return true;
    }
    return plantFromInventory(farm, bc);
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    BlockCoord up = bc.getLocation(ForgeDirection.UP);
    Block upBLock = farm.getBlock(up);
    return upBLock == plantedBlock;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return seeds.isItemEqual(stack);
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        
    
    HarvestResult res = new HarvestResult();
    BlockCoord harvestCoord = bc;
    boolean done = false;
    do{
      harvestCoord = harvestCoord.getLocation(ForgeDirection.UP);
      if(plantedBlock == farm.getBlock(harvestCoord) && farm.hasHarvestTool()) {
        res.harvestedBlocks.add(harvestCoord);
        ArrayList<ItemStack> drops = plantedBlock.getDrops(farm.getWorld(), harvestCoord.x, harvestCoord.y, harvestCoord.z, meta, farm.getMaxLootingValue());
        if(drops != null) {
          for(ItemStack drop : drops) {
            res.drops.add(new EntityItem(farm.getWorld(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, drop.copy()));
          }

        }
        farm.damageMaxLootingItem();
        farm.actionPerformed();
        farm.getWorld().setBlockToAir(harvestCoord.x, harvestCoord.y, harvestCoord.z);
      } else {
        done = true;
      }
    } while(!done);

    return res;
  }

  @Override
  protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc) {
    World worldObj = farm.getWorldObj();
    if(canPlant(worldObj, bc) && farm.getSeedFromSupplies(seeds, bc) != null) {
      return plant(farm, worldObj, bc);
    }
    return false;
  }

}
