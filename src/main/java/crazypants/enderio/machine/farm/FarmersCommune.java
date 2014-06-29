package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.util.BlockCoord;

public class FarmersCommune implements IFarmerJoe {

  public static FarmersCommune instance = new FarmersCommune();

  public static void joinCommune(IFarmerJoe joe) {
    instance.farmers.add(joe);
  }

  public static void leaveCommune(IFarmerJoe joe) {    
    throw new UnsupportedOperationException("As if this would be implemented. The commune is for life!");
  }

  private List<IFarmerJoe> farmers = new ArrayList<IFarmerJoe>();

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
//    if(!block.canHarvestBlock(farm.getFakePlayer(), meta)) {
//      return null;
//    }
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



}
