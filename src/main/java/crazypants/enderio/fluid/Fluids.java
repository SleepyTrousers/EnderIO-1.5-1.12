package crazypants.enderio.fluid;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import com.enderio.core.common.fluid.BlockFluidEnder;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.integration.railcraft.RailcraftUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Fluids {

  public static final String NUTRIENT_DISTILLATION_NAME = "nutrient_distillation";
  public static final String ENDER_DISTILLATION_NAME = "ender_distillation";
  public static final String VAPOR_OF_LEVITY_NAME = "vapor_of_levity";
  public static final String HOOTCH_NAME = "hootch";
  public static final String ROCKET_FUEL_NAME = "rocket_fuel";
  public static final String FIRE_WATER_NAME = "fire_water";
  public static final String XP_JUICE_NAME = "xpjuice";

  public static final String LIQUID_SUNSHINE_NAME = "liquid_sunshine";
  public static final String CLOUD_SEED_NAME = "cloud_seed";
  public static final String CLOUD_SEED_CONCENTRATED_NAME = "cloud_seed_concentrated";

  public static Fluid fluidNutrientDistillation;
  public static BlockFluidEnder blockNutrientDistillation;

  public static Fluid fluidHootch;
  public static BlockFluidEnder blockHootch;

  public static Fluid fluidRocketFuel;
  public static BlockFluidEnder blockRocketFuel;

  public static Fluid fluidFireWater;
  public static BlockFluidEnder blockFireWater;

  public static Fluid fluidLiquidSunshine;
  public static BlockFluidEnder blockLiquidSunshine;

  public static Fluid fluidCloudSeed;
  public static BlockFluidEnder blockCloudSeed;

  public static Fluid fluidCloudSeedConcentrated;
  public static BlockFluidEnder blockCloudSeedConcentrated;

  public static Fluid fluidEnderDistillation;
  public static BlockFluidEnder blockEnderDistillation;

  public static Fluid fluidVaporOfLevity;
  public static BlockFluidEnder blockVaporOfLevity;

  // Open block compatible liquid XP
  public static Fluid fluidXpJuice;

  public static ResourceLocation getStill(String fluidName) {
    return new ResourceLocation(EnderIO.DOMAIN, "blocks/fluid_" + fluidName + "_still");
  }

  public static ResourceLocation getFlowing(String fluidName) {
    return new ResourceLocation(EnderIO.DOMAIN, "blocks/fluid_" + fluidName + "_flow");
  }

  public static String toCapactityString(IFluidTank tank) {
    if (tank == null) {
      return "0/0 " + MB();
    }
    return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
  }

  public static String MB() {
    return EnderIO.lang.localize("fluid.millibucket.abr");
  }

  public void registerFluids() {
    Fluid f = new Fluid(Fluids.NUTRIENT_DISTILLATION_NAME, getStill(Fluids.NUTRIENT_DISTILLATION_NAME), getFlowing(Fluids.NUTRIENT_DISTILLATION_NAME))
        .setDensity(1500).setViscosity(3000);
    FluidRegistry.registerFluid(f);
    fluidNutrientDistillation = FluidRegistry.getFluid(f.getName());
    blockNutrientDistillation = BlockFluidEio.create(fluidNutrientDistillation, Material.WATER, 0x5a5e00);

    f = new Fluid(Fluids.ENDER_DISTILLATION_NAME, getStill(Fluids.ENDER_DISTILLATION_NAME), getFlowing(Fluids.ENDER_DISTILLATION_NAME)).setDensity(200)
        .setViscosity(1000).setTemperature(175);
    FluidRegistry.registerFluid(f);
    fluidEnderDistillation = FluidRegistry.getFluid(f.getName());
    blockEnderDistillation = BlockFluidEio.create(fluidEnderDistillation, Material.WATER, 0x149535);

    f = new Fluid(Fluids.VAPOR_OF_LEVITY_NAME, getStill(Fluids.VAPOR_OF_LEVITY_NAME), getFlowing(Fluids.VAPOR_OF_LEVITY_NAME)).setDensity(-10).setViscosity(100)
        .setTemperature(5).setGaseous(true);
    FluidRegistry.registerFluid(f);
    fluidVaporOfLevity = FluidRegistry.getFluid(f.getName());
    blockVaporOfLevity = BlockFluidEio.create(fluidVaporOfLevity, Material.WATER, 0x41716a);
    blockVaporOfLevity.setQuantaPerBlock(1);

    f = new Fluid(Fluids.HOOTCH_NAME, Fluids.getStill(Fluids.HOOTCH_NAME), Fluids.getFlowing(Fluids.HOOTCH_NAME)).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidHootch = FluidRegistry.getFluid(f.getName());
    blockHootch = BlockFluidEio.create(fluidHootch, Material.WATER, 0xffffff);
    FluidFuelRegister.instance.addFuel(f, Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);

    f = new Fluid(Fluids.ROCKET_FUEL_NAME, Fluids.getStill(Fluids.ROCKET_FUEL_NAME), Fluids.getFlowing(Fluids.ROCKET_FUEL_NAME)).setDensity(900)
        .setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidRocketFuel = FluidRegistry.getFluid(f.getName());
    blockRocketFuel = BlockFluidEio.create(fluidRocketFuel, Material.WATER, 0x707044);
    FluidFuelRegister.instance.addFuel(f, Config.rocketFuelPowerPerCycleRF, Config.rocketFuelPowerTotalBurnTime);

    f = new Fluid(Fluids.FIRE_WATER_NAME, Fluids.getStill(Fluids.FIRE_WATER_NAME), Fluids.getFlowing(Fluids.FIRE_WATER_NAME)).setDensity(900)
        .setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidFireWater = FluidRegistry.getFluid(f.getName());
    blockFireWater = BlockFluidEio.create(fluidFireWater, Material.LAVA, 0x8a490f);
    FluidFuelRegister.instance.addFuel(f, Config.fireWaterPowerPerCycleRF, Config.fireWaterPowerTotalBurnTime);

    f = new Fluid(Fluids.LIQUID_SUNSHINE_NAME, getStill(LIQUID_SUNSHINE_NAME), getFlowing(LIQUID_SUNSHINE_NAME)).setDensity(200).setViscosity(400);
    FluidRegistry.registerFluid(f);
    fluidLiquidSunshine = FluidRegistry.getFluid(f.getName());
    blockLiquidSunshine = BlockFluidEio.create(fluidLiquidSunshine, Material.WATER, 0xd2c561);
    blockLiquidSunshine.setLightLevel(1);

    f = new Fluid(Fluids.CLOUD_SEED_NAME, getStill(CLOUD_SEED_NAME), getFlowing(CLOUD_SEED_NAME)).setDensity(500).setViscosity(800);
    FluidRegistry.registerFluid(f);
    fluidCloudSeed = FluidRegistry.getFluid(f.getName());
    blockCloudSeed = BlockFluidEio.create(fluidCloudSeed, Material.WATER, 0x248589);

    f = new Fluid(Fluids.CLOUD_SEED_CONCENTRATED_NAME, getStill(CLOUD_SEED_CONCENTRATED_NAME), getFlowing(CLOUD_SEED_CONCENTRATED_NAME)).setDensity(1000)
        .setViscosity(1200);
    FluidRegistry.registerFluid(f);
    fluidCloudSeedConcentrated = FluidRegistry.getFluid(f.getName());
    blockCloudSeedConcentrated = BlockFluidEio.create(fluidCloudSeedConcentrated, Material.WATER, 0x3f5c5d);

    f = new Fluid(getXPJuiceName(), getStill(Fluids.XP_JUICE_NAME), getFlowing(Fluids.XP_JUICE_NAME)).setLuminosity(10)
        .setDensity(800).setViscosity(1500);
    if (FluidRegistry.registerFluid(f)) {
      Log.info("XP Juice registered by Ender IO.");
    } else {
      Log.info("XP Juice already registered by another mod as '" + FluidRegistry.getFluid(f.getName()).getUnlocalizedName() + "'");
    }
    fluidXpJuice = FluidRegistry.getFluid(f.getName());

    RailcraftUtil.registerFuels();
    Buckets.createBuckets();
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    MinecraftForge.EVENT_BUS.register(this);
    registerFluidBlockRendering(fluidNutrientDistillation);
    registerFluidBlockRendering(fluidEnderDistillation);
    registerFluidBlockRendering(fluidHootch);
    registerFluidBlockRendering(fluidFireWater);
    registerFluidBlockRendering(fluidRocketFuel);
    registerFluidBlockRendering(fluidLiquidSunshine);
    registerFluidBlockRendering(fluidCloudSeed);
    registerFluidBlockRendering(fluidCloudSeedConcentrated);
    registerFluidBlockRendering(fluidVaporOfLevity);
  }

  @SideOnly(Side.CLIENT)
  public void registerFluidBlockRendering(@Nullable Fluid fluid) {
    if (fluid == null) {
      return;
    }
    Block block = fluid.getBlock();
    if (block == null) {
      return;
    }
    FluidStateMapper mapper = new FluidStateMapper(fluid);
    // block-model
    ModelLoader.setCustomStateMapper(block, mapper);

    Item item = Item.getItemFromBlock(block);
    // item-model
    if (item != Items.AIR) {
      ModelLoader.registerItemVariants(item);
      ModelLoader.setCustomMeshDefinition(item, mapper);
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {
    if (fluidXpJuice != null) {
      event.getMap().registerSprite(fluidXpJuice.getStill());
      event.getMap().registerSprite(fluidXpJuice.getFlowing());
    }

  }

  private static String getXPJuiceName() {
    String openBlocksXPJuiceName = null;

    try {
      Field getField = Class.forName("openblocks.Config").getField("xpFluidId");
      openBlocksXPJuiceName = (String) getField.get(null);
    } catch (Exception e) {
    }

    if (openBlocksXPJuiceName != null && !Config.xpJuiceName.equals(openBlocksXPJuiceName)) {
      Log.info("Overwriting XP Juice name with '" + openBlocksXPJuiceName + "' taken from OpenBlocks' config");
      return openBlocksXPJuiceName;
    }

    return Config.xpJuiceName;
  }

}
