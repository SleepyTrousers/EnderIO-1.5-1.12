package crazypants.enderio.init;

import java.lang.reflect.Field;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.fluid.ItemBucketEio;
import crazypants.render.IconUtil;
import crazypants.render.IconUtil.IIconProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class EIOFluids {

  // Fluids
  public static Fluid fluidNutrientDistillation;
  public static BlockFluidEio blockNutrientDistillation;
  public static ItemBucketEio itemBucketNutrientDistillation;

  public static Fluid fluidHootch;
  public static BlockFluidEio blockHootch;
  public static ItemBucketEio itemBucketHootch;

  public static Fluid fluidRocketFuel;
  public static BlockFluidEio blockRocketFuel;
  public static ItemBucketEio itemBucketRocketFuel;

  public static Fluid fluidFireWater;
  public static BlockFluidEio blockFireWater;
  public static ItemBucketEio itemBucketFireWater;

  // Open block compatable liquid XP
  public static Fluid fluidXpJuice;
  public static ItemBucketEio itemBucketXpJuice;

  public static void registerFluids() {
    Fluid f = new Fluid(Fluids.NUTRIENT_DISTILLATION_NAME).setDensity(1500).setViscosity(3000);
    FluidRegistry.registerFluid(f);
    fluidNutrientDistillation = FluidRegistry.getFluid(f.getName());
    blockNutrientDistillation = BlockFluidEio.create(fluidNutrientDistillation, Material.water);

    f = new Fluid(Fluids.HOOTCH_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidHootch = FluidRegistry.getFluid(f.getName());
    blockHootch = BlockFluidEio.create(fluidHootch, Material.water);
    FluidFuelRegister.instance.addFuel(f, Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.HOOTCH_NAME + "@"
        + (Config.hootchPowerPerCycleRF / 10 * Config.hootchPowerTotalBurnTime));

    f = new Fluid(Fluids.ROCKET_FUEL_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidRocketFuel = FluidRegistry.getFluid(f.getName());
    blockRocketFuel = BlockFluidEio.create(fluidRocketFuel, Material.water);
    FluidFuelRegister.instance.addFuel(f, Config.rocketFuelPowerPerCycleRF, Config.rocketFuelPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.ROCKET_FUEL_NAME + "@"
        + (Config.rocketFuelPowerPerCycleRF / 10 * Config.rocketFuelPowerTotalBurnTime));

    f = new Fluid(Fluids.FIRE_WATER_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidFireWater = FluidRegistry.getFluid(f.getName());
    blockFireWater = BlockFluidEio.create(fluidFireWater, Material.lava);
    FluidFuelRegister.instance.addFuel(f, Config.fireWaterPowerPerCycleRF, Config.fireWaterPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.FIRE_WATER_NAME + "@"
        + (Config.fireWaterPowerPerCycleRF / 10 * Config.fireWaterPowerTotalBurnTime));

    if (!Loader.isModLoaded("OpenBlocks")) {
      Log.info("XP Juice registered by Ender IO.");
      fluidXpJuice = new Fluid(Config.xpJuiceName).setLuminosity(10).setDensity(800).setViscosity(1500)
          .setUnlocalizedName("eio.xpjuice");
      FluidRegistry.registerFluid(fluidXpJuice);
      itemBucketXpJuice = ItemBucketEio.create(fluidXpJuice);
    } else {
      Log.info("XP Juice registration left to Open Blocks.");
    }

    itemBucketNutrientDistillation = ItemBucketEio.create(fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(fluidFireWater);
  }

  public static void postInitFluids() {
    if (fluidXpJuice == null) { // should have been registered by open blocks
      fluidXpJuice = FluidRegistry.getFluid(EIOFluids.getXPJuiceName());
      if (fluidXpJuice == null) {
        Log.error("Liquid XP Juice registration left to open blocks but could not be found.");
      }
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

  @SideOnly(Side.CLIENT)
  public static void registerFluidRenderers() {
    if (!Loader.isModLoaded("OpenBlocks")) {
      // We have registered liquid XP so we need to give it textures
      IconUtil.addIconProvider(new IconUtil.IIconProvider() {
  
        @Override
        public void registerIcons(IIconRegister register) {
          // NB: textures re-used with permission from OpenBlocks to maintain
          // look
          IIcon flowing = register.registerIcon("enderio:xpjuiceflowing");
          IIcon still = register.registerIcon("enderio:xpjuicestill");
          fluidXpJuice.setIcons(still, flowing);
        }
  
        @Override
        public int getTextureType() {
          return 0;
        }
  
      });
    }
  }

}
