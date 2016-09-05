package crazypants.enderio.fluid;

import crazypants.util.ClientUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    if (FluidRegistry.isUniversalBucketEnabled()) {
      if (Fluids.fluidXpJuice != null) {
        FluidRegistry.addBucketForFluid(Fluids.fluidXpJuice);
        itemBucketXpJuice = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidXpJuice);
      }
      FluidRegistry.addBucketForFluid(Fluids.fluidNutrientDistillation);
      itemBucketNutrientDistillation = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidNutrientDistillation);
      FluidRegistry.addBucketForFluid(Fluids.fluidHootch);
      itemBucketHootch = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidHootch);
      FluidRegistry.addBucketForFluid(Fluids.fluidRocketFuel);
      itemBucketRocketFuel = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidRocketFuel);
      FluidRegistry.addBucketForFluid(Fluids.fluidFireWater);
      itemBucketFireWater = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidFireWater);
      FluidRegistry.addBucketForFluid(Fluids.fluidLiquidSunshine);
      itemBucketLiquidSunshine = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidLiquidSunshine);
      FluidRegistry.addBucketForFluid(Fluids.fluidCloudSeed);
      itemBucketCloudSeed = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidCloudSeed);
      FluidRegistry.addBucketForFluid(Fluids.fluidCloudSeedConcentrated);
      itemBucketCloudSeedConcentrated = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidCloudSeedConcentrated);
      FluidRegistry.addBucketForFluid(Fluids.fluidEnderDistillation);
      itemBucketEnderDistillation = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidEnderDistillation);
      FluidRegistry.addBucketForFluid(Fluids.fluidVaporOfLevity);
      itemBucketVaporOfLevity= UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, Fluids.fluidVaporOfLevity);
      
    } else {
      if (Fluids.fluidXpJuice != null) {
        itemBucketXpJuice = new ItemStack(ItemBucketEio.create(null, Fluids.fluidXpJuice));
      }
      itemBucketNutrientDistillation = new ItemStack(ItemBucketEio.create(Fluids.blockNutrientDistillation, Fluids.fluidNutrientDistillation));
      itemBucketHootch = new ItemStack(ItemBucketEio.create(Fluids.blockHootch, Fluids.fluidHootch));
      itemBucketRocketFuel = new ItemStack(ItemBucketEio.create(Fluids.blockRocketFuel, Fluids.fluidRocketFuel));
      itemBucketFireWater = new ItemStack(ItemBucketEio.create(Fluids.blockFireWater, Fluids.fluidFireWater));
      itemBucketLiquidSunshine = new ItemStack(ItemBucketEio.create(Fluids.blockLiquidSunshine, Fluids.fluidLiquidSunshine));
      itemBucketCloudSeed = new ItemStack(ItemBucketEio.create(Fluids.blockCloudSeed, Fluids.fluidCloudSeed));
      itemBucketCloudSeedConcentrated = new ItemStack(ItemBucketEio.create(Fluids.blockCloudSeedConcentrated, Fluids.fluidCloudSeedConcentrated));
      itemBucketEnderDistillation = new ItemStack(ItemBucketEio.create(Fluids.blockEnderDistillation, Fluids.fluidEnderDistillation));
      itemBucketVaporOfLevity = new ItemStack(ItemBucketEio.create(Fluids.blockVaporOfLevity, Fluids.fluidVaporOfLevity));
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers() {
    if (!FluidRegistry.isUniversalBucketEnabled()) {
      ClientUtil.registerRenderer(itemBucketNutrientDistillation.getItem(), ((ItemBucketEio) itemBucketNutrientDistillation.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketHootch.getItem(), ((ItemBucketEio) itemBucketHootch.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketRocketFuel.getItem(), ((ItemBucketEio) itemBucketRocketFuel.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketFireWater.getItem(), ((ItemBucketEio) itemBucketFireWater.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketLiquidSunshine.getItem(), ((ItemBucketEio) itemBucketLiquidSunshine.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketCloudSeed.getItem(), ((ItemBucketEio) itemBucketCloudSeed.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketCloudSeedConcentrated.getItem(), ((ItemBucketEio) itemBucketCloudSeedConcentrated.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketEnderDistillation.getItem(), ((ItemBucketEio) itemBucketEnderDistillation.getItem()).getItemName());
      ClientUtil.registerRenderer(itemBucketVaporOfLevity.getItem(), ((ItemBucketEio) itemBucketVaporOfLevity.getItem()).getItemName());
      if (itemBucketXpJuice != null) {
        ClientUtil.registerRenderer(itemBucketXpJuice.getItem(), ((ItemBucketEio) itemBucketXpJuice.getItem()).getItemName());
      }
    }
  }

}
