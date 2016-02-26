package crazypants.enderio.fluid;

import crazypants.util.ClientUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
      itemBucketXpJuice = ItemBucketEio.create(Fluids.fluidXpJuice);
    } 
    itemBucketNutrientDistillation = ItemBucketEio.create(Fluids.fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(Fluids.fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(Fluids.fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(Fluids.fluidFireWater);
    itemBucketLiquidSunshine = ItemBucketEio.create(Fluids.fluidLiquidSunshine);
    itemBucketCloudSeed = ItemBucketEio.create(Fluids.fluidCloudSeed);
    itemBucketCloudSeedCompressed = ItemBucketEio.create(Fluids.fluidCloudSeedConcentrated);
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
