package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class NaturaBerryFarmer extends PickableFarmer {

  public NaturaBerryFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    
    if(block != getPlantedBlock()) {
      return null;
    }
    BlockCoord upLoc = bc.getLocation(ForgeDirection.UP);
    Block aboveBlock = farm.getBlock(upLoc);
    ArrayList<ItemStack> drops = null;

    if(aboveBlock == getPlantedBlock()) {        
      drops = block.getDrops(farm.getWorld(), upLoc.x, upLoc.y, upLoc.z, meta, farm.getMaxLootingValue());
      farm.damageMaxLootingItem();
      farm.actionPerformed();
      farm.getWorld().setBlockToAir(upLoc.x, upLoc.y, upLoc.z);
      
    }
    IHarvestResult res = super.harvestBlock(farm, bc, block, meta);
    if(drops != null) {
      if(res == null) {
        res = new HarvestResult();
      }
      res.getHarvestedBlocks().add(upLoc);
      List<EntityItem> addToDrops = res.getDrops();
      for(ItemStack stack : drops) {
        addToDrops.add(new EntityItem(farm.getWorld(), upLoc.x + 0.5, upLoc.y + 1, upLoc.z + 0.5, stack.copy()));
      }
    }
    return res;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    BlockCoord upLoc = bc.getLocation(ForgeDirection.UP);
    Block aboveBlock = farm.getBlock(upLoc);      
    if(aboveBlock == getPlantedBlock()) {   
      return true;
    }      
    return super.canHarvest(farm, bc, block, meta);
  }

}