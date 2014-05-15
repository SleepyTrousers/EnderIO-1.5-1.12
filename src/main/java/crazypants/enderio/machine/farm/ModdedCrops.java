package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.util.BlockCoord;

public final class ModdedCrops {

  public static void addModdedFarmers() {
    addNutura();
    addTiC();    
  }

  private static void addTiC() {
    String mod = "TConstruct";
    String blockName = "ore.berries.two";
    
    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 2; i++) {                
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 8 + i));
          farmer.setRequiresFarmland(false);
          FarmersComune.instance.joinComune(farmer);
        }
      }
    }
    
    blockName = "ore.berries.one";
    cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 4; i++) {        
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 8 + i));
          farmer.setRequiresFarmland(false);
          FarmersComune.instance.joinComune(farmer);
        }
      }
    }
    
  }

  private static void addNutura() {
    String mod = "Natura";
    String blockName = "N Crops";

    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, "barley.seed");
      if(seedItem != null) {
        //barley
        FarmersComune.instance.joinComune(new SeedFarmer(cropBlock, 3, new ItemStack(seedItem)));
        // cotton
        FarmersComune.instance.joinComune(new PickableFarmer(cropBlock, 4, 8, new ItemStack(seedItem, 1, 1)));
      }
    }

    blockName = "BerryBush";
    cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 4; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 12 + i));
          farmer.setRequiresFarmland(false);
          FarmersComune.instance.joinComune(farmer);
        }
      }
    }
  }

  private ModdedCrops() {
  }

  private static class NaturaBerryFarmer extends PickableFarmer {

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

}
