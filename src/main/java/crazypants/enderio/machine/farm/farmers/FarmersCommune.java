package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.TileFarmStation;

public class FarmersCommune implements IFarmerJoe {

  public static FarmersCommune instance = new FarmersCommune();
  private static List<ItemStack> disableTrees = new ArrayList<ItemStack>();

  public static void joinCommune(IFarmerJoe joe) {
    if (joe instanceof CustomSeedFarmer)
    {
      CustomSeedFarmer customSeedFarmer = (CustomSeedFarmer) joe;
      if (customSeedFarmer.doesDisableTreeFarm()) disableTrees.add(customSeedFarmer.getSeeds());
    }
    instance.farmers.add(joe);
  }

  public static void leaveCommune(IFarmerJoe joe) {    
    throw new UnsupportedOperationException("As if this would be implemented. The commune is for life!");
  }

  private List<IFarmerJoe> farmers = new ArrayList<IFarmerJoe>();
    
  private FarmersCommune() {
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
      if (ignoreTreeHarvest(farm, bc, joe)) continue;
      if (joe.canHarvest(farm, bc, block, meta)) {
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

  private boolean ignoreTreeHarvest(TileFarmStation farm, BlockCoord bc, IFarmerJoe joe)
  {
    ItemStack stack = farm.getSeedTypeInSuppliesFor(bc);
    if (!(joe instanceof TreeFarmer) || stack == null) return false;
    for (ItemStack disableTreeStack : disableTrees)
    {
      if (disableTreeStack.isItemEqual(stack)) return true;
    }
    return false;
  }
}
