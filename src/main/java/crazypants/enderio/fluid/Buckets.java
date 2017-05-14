package crazypants.enderio.fluid;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;

public class Buckets {

  public static ItemStack itemBucketNutrientDistillation;
  public static ItemStack itemBucketHootch;
  public static ItemStack itemBucketRocketFuel;
  public static ItemStack itemBucketFireWater;
  public static ItemStack itemBucketXpJuice;
  public static ItemStack itemBucketLiquidSunshine;
  public static ItemStack itemBucketCloudSeed;
  public static ItemStack itemBucketCloudSeedConcentrated;
  public static ItemStack itemBucketEnderDistillation;
  public static ItemStack itemBucketVaporOfLevity;

  public static void createBuckets() {
    itemBucketXpJuice = registerForBucket(Fluids.fluidXpJuice);
    itemBucketNutrientDistillation = registerForBucket(Fluids.fluidNutrientDistillation);
    itemBucketHootch = registerForBucket(Fluids.fluidHootch);
    itemBucketRocketFuel = registerForBucket(Fluids.fluidRocketFuel);
    itemBucketFireWater = registerForBucket(Fluids.fluidFireWater);
    itemBucketLiquidSunshine = registerForBucket(Fluids.fluidLiquidSunshine);
    itemBucketCloudSeed = registerForBucket(Fluids.fluidCloudSeed);
    itemBucketCloudSeedConcentrated = registerForBucket(Fluids.fluidCloudSeedConcentrated);
    itemBucketEnderDistillation = registerForBucket(Fluids.fluidEnderDistillation);
    itemBucketVaporOfLevity = registerForBucket(Fluids.fluidVaporOfLevity);
  }

  private static ItemStack registerForBucket(Fluid fluid) {
    if (!FluidRegistry.getBucketFluids().contains(fluid)) {
      FluidRegistry.addBucketForFluid(fluid);
    }
    final UniversalBucket universalBucket = ForgeModContainer.getInstance().universalBucket;
    if (universalBucket == null) {
      throw new NullPointerException("Forge Universal Bucket is missing");
    }
    return UniversalBucket.getFilledBucket(universalBucket, fluid);
  }

}
