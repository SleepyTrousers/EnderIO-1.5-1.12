package crazypants.enderio.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import crazypants.util.BlockCoord;

public class FarmersComune implements IFarmerJoe {

  public static FarmersComune instance = new FarmersComune();

  public static void joinComune(IFarmerJoe joe) {
    instance.farmers.add(joe);
  }

  public static void leaveComune(IFarmerJoe joe) {
    //instance.farmers.remove(joe);
    throw new UnsupportedOperationException("As if this would be implemented.");
  }

  static {
    joinComune(new SeedFarmer(Blocks.potatoes, new ItemStack(Items.potato)));
    joinComune(new SeedFarmer(Blocks.wheat, new ItemStack(Items.wheat_seeds)));
    joinComune(new SeedFarmer(Blocks.carrots, new ItemStack(Items.carrot)));
    joinComune(new NetherWartFarmer());
    joinComune(new StemFarmer(Blocks.reeds, new ItemStack(Items.reeds)));
    joinComune(new StemFarmer(Blocks.cactus, new ItemStack(Blocks.cactus)));
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
    if(!block.canHarvestBlock(farm.getFakePlayer(), meta)) {
      return null;
    }
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
