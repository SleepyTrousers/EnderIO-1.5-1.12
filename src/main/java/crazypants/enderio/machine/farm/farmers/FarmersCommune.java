package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class FarmersCommune implements IFarmerJoe {

  public static FarmersCommune instance = new FarmersCommune();

  public static void joinCommune(IFarmerJoe joe) {
    instance.farmers.add(joe);
  }

  public static void leaveCommune(IFarmerJoe joe) {    
    throw new UnsupportedOperationException("As if this would be implemented. The commune is for life!");
  }

  private List<IFarmerJoe> farmers = new ArrayList<IFarmerJoe>();
  
  private Set<Class<?>> toolTypes = new HashSet<Class<?>>();
  
  private FarmersCommune() {
    registerToolType(ItemHoe.class);
    registerToolType(ItemAxe.class);
  }
  
  public void registerToolType(Class<?> clz) {
    toolTypes.add(clz);
  }
  
  @Override
  public boolean canHarvest(TileFarmStation farm,  BlockCoord bc, Block block, int meta) {
    for (IFarmerJoe joe : farmers) {
      if(joe.canHarvest(farm, bc, block, meta)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    for (IFarmerJoe joe : farmers) {
      if(joe.canHarvest(farm, bc, block, meta)) {
        return joe.harvestBlock(farm, bc, block, meta);
      }
    }
    return null;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    for (IFarmerJoe joe : farmers) {
      if(joe.prepareBlock(farm, bc, block, meta)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    for (IFarmerJoe joe : farmers) {
      if(joe.canPlant(stack)) {
        return true;
      }
    }
    return false;
  }

  public Set<Class<?>> getToolTypes() {
    return toolTypes;
  }



}
