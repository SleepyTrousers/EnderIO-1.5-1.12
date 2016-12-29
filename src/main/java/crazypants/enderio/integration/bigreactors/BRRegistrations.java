package crazypants.enderio.integration.bigreactors;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.integration.tic.AdditionalFluid;
import crazypants.enderio.material.Alloy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class BRRegistrations {

  private static final float conductivityWater = 0.1f;
  private static final float conductivityIron = 0.6f;
  private static final float conductivityCopper = 1f;
  private static final float conductivitySilver = 1.5f;
  private static final float conductivityGold = 2f;
  private static final float conductivityDiamond = 3f;
  private static final float conductivityEmerald = 2.5f;
  private static final float conductivityGraphene = 5f;

  public static void init(FMLPreInitializationEvent event) {

    BRProxy.registerTurbineBlock(Alloy.ELECTRICAL_STEEL.getOreBlock(), 1.8f, 1f, 1.6f);
    BRProxy.registerTurbineBlock(Alloy.ENERGETIC_ALLOY.getOreBlock(), 1.9f, 1f, 2.5f);
    BRProxy.registerTurbineBlock(Alloy.VIBRANT_ALLOY.getOreBlock(), 2.0f, 1f, 2.5f);
    BRProxy.registerTurbineBlock(Alloy.CONDUCTIVE_IRON.getOreBlock(), 1.3f, 1.01f, 1.6f);
    BRProxy.registerTurbineBlock(Alloy.DARK_STEEL.getOreBlock(), 1.5f, 1f, 1.3f);

    BRProxy.registerBlock(Alloy.ELECTRICAL_STEEL.getOreBlock(), 0.50f, 0.78f, 1.40f, conductivitySilver);
    BRProxy.registerBlock(Alloy.ENERGETIC_ALLOY.getOreBlock(), 0.59f, 0.80f, 1.48f, conductivityGold);
    BRProxy.registerBlock(Alloy.VIBRANT_ALLOY.getOreBlock(), 0.60f, 0.85f, 1.38f, conductivityEmerald);
    BRProxy.registerBlock(Alloy.CONDUCTIVE_IRON.getOreBlock(), 0.52f, 0.75f, 1.40f, conductivityIron);
    BRProxy.registerBlock(Alloy.DARK_STEEL.getOreBlock(), 0.50f, 0.78f, 1.42f, conductivityIron);
    BRProxy.registerBlock(Alloy.SOULARIUM.getOreBlock(), 0.45f, 0.95f, 1.8f, conductivityGold);

    BRProxy.registerFluid(Alloy.ELECTRICAL_STEEL.getFluidName(), 0.50f, 0.78f, 1.40f, conductivitySilver);
    BRProxy.registerFluid(Alloy.ENERGETIC_ALLOY.getFluidName(), 0.59f, 0.80f, 1.48f, conductivityGold);
    BRProxy.registerFluid(Alloy.VIBRANT_ALLOY.getFluidName(), 0.60f, 0.85f, 1.38f, conductivityEmerald);
    BRProxy.registerFluid(Alloy.CONDUCTIVE_IRON.getFluidName(), 0.52f, 0.75f, 1.40f, conductivityIron);
    BRProxy.registerFluid(Alloy.DARK_STEEL.getFluidName(), 0.50f, 0.78f, 1.42f, conductivityIron);
    BRProxy.registerFluid(Alloy.SOULARIUM.getFluidName(), 0.45f, 0.95f, 1.8f, conductivityGold);

    BRProxy.registerFluid(Fluids.NUTRIENT_DISTILLATION_NAME, 0.50f, 0.65f, 1.44f, conductivityIron);
    BRProxy.registerFluid(Fluids.ENDER_DISTILLATION_NAME, 0.55f, 0.74f, 1.44f, conductivityEmerald);
    BRProxy.registerFluid(Fluids.VAPOR_OF_LEVITY_NAME, 0.95f, 0.95f, 4.00f, conductivityDiamond);

    BRProxy.registerFluid(Fluids.HOOTCH_NAME, 0.5f, 0.4f, 1.33f, conductivityWater);
    BRProxy.registerFluid(Fluids.ROCKET_FUEL_NAME, 0.6f, 0.3f, 1.33f, conductivityWater);
    BRProxy.registerFluid(Fluids.FIRE_WATER_NAME, 0.7f, 0.2f, 1.33f, conductivityWater);

    BRProxy.registerFluid(Fluids.LIQUID_SUNSHINE_NAME, .39f, 0.1f, 1.11f, conductivityWater);
    BRProxy.registerFluid(Fluids.CLOUD_SEED_NAME, .39f, 0.05f, 1.33f, conductivityWater);
    BRProxy.registerFluid(Fluids.CLOUD_SEED_CONCENTRATED_NAME, .39f, 0.005f, 2f, conductivityWater);

    BRProxy.registerFluid(AdditionalFluid.REDSTONE_FLUID_NAME, 0.75f, 0.55f, 1.60f, conductivityEmerald);
    BRProxy.registerFluid(AdditionalFluid.GLOWSTONE_FLUID_NAME, 0.20f, 0.60f, 1.75f, conductivityCopper);
    BRProxy.registerFluid(AdditionalFluid.ENDER_FLUID_NAME, 0.90f, 0.75f, 2.00f, conductivityGold);

  }

}
