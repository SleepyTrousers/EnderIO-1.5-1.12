package crazypants.enderio.fluid;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.util.ClientUtil;

public class Buckets {

  public static ItemBucketEio itemBucketNutrientDistillation;
  public static ItemBucketEio itemBucketHootch;
  public static ItemBucketEio itemBucketRocketFuel;
  public static ItemBucketEio itemBucketFireWater;
  public static ItemBucketEio itemBucketXpJuice;
  public static ItemBucketEio itemBucketLiquidSunshine;
  public static ItemBucketEio itemBucketCloudSeed;
  public static ItemBucketEio itemBucketCloudSeedCompressed;
  
  public static void createBuckets() {
    if(Fluids.fluidXpJuice != null) {
      itemBucketXpJuice = ItemBucketEio.create(null, Fluids.fluidXpJuice);
    } 
    itemBucketNutrientDistillation = ItemBucketEio.create(Fluids.blockNutrientDistillation, Fluids.fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(Fluids.blockHootch, Fluids.fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(Fluids.blockRocketFuel, Fluids.fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(Fluids.blockFireWater, Fluids.fluidFireWater);
    itemBucketLiquidSunshine = ItemBucketEio.create(Fluids.blockLiquidSunshine, Fluids.fluidLiquidSunshine);
    itemBucketCloudSeed = ItemBucketEio.create(Fluids.blockCloudSeed, Fluids.fluidCloudSeed);
    itemBucketCloudSeedCompressed = ItemBucketEio.create(Fluids.blockCloudSeedConcentrated, Fluids.fluidCloudSeedConcentrated);
  }
  
  @SideOnly(Side.CLIENT)
  public static void registerRenderers() {
    ClientUtil.registerRenderer(itemBucketNutrientDistillation, itemBucketNutrientDistillation.getItemName());
    ClientUtil.registerRenderer(itemBucketHootch, itemBucketHootch.getItemName());
    ClientUtil.registerRenderer(itemBucketRocketFuel, itemBucketRocketFuel.getItemName());
    ClientUtil.registerRenderer(itemBucketFireWater, itemBucketFireWater.getItemName());
    ClientUtil.registerRenderer(itemBucketLiquidSunshine, itemBucketLiquidSunshine.getItemName());
    ClientUtil.registerRenderer(itemBucketCloudSeed, itemBucketCloudSeed.getItemName());
    ClientUtil.registerRenderer(itemBucketCloudSeedCompressed, itemBucketCloudSeedCompressed.getItemName());
    if(itemBucketXpJuice != null) {
      ClientUtil.registerRenderer(itemBucketXpJuice, itemBucketXpJuice.getItemName());
    }    
  }
  
}
