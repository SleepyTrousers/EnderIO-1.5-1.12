package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class FarmConfig {

  public static final IValueFactory F = Config.F.section("farm");

  public static final IValue<Integer> farmBonemealDelaySuccess = F.make("farmBonemealDelaySuccess", 16, //
      "The delay (in blocks) between a successful bonemealing and the next try.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmBonemealDelayFail = F.make("farmBonemealDelayFail", 4, //
      "The delay (in blocks) between an unsuccessful bonemealing and the next try.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Float> farmBonemealChance = F.make("farmBonemealChance", .75f, //
      "The chance that bonemeal will be tried.").setRange(0, 1).sync();

  public static final IValue<Integer> farmBonemealEnergyUseSuccess = F.make("farmBonemealEnergyUseSuccess", 160, //
      "The amount of energy a successful bonemealing uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmBonemealEnergyUseFail = F.make("farmBonemealEnergyUseFail", 80, //
      "The amount of energy an unsuccessful bonemealing uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmHarvestAxeEnergyUse = F.make("farmHarvestAxeEnergyUse", 1000, //
      "The amount of energy harvesting a block with an axe uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmHarvestEnergyUse = F.make("farmHarvestEnergyUse", 500, //
      "The amount of energy harvesting a block with anything else but an axe uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmPlantEnergyUse = F.make("farmPlantEnergyUse", 0, //
      "The amount of energy planting a seed uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> farmTillEnergyUse = F.make("farmTillEnergyUse", 0, //
      "The amount of energy tilling the ground uses.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Float> farmToolDamageChance = F.make("farmToolDamageChance", 1f, //
      "The chance that a tool will take damage when used.").setRange(0, 1).sync();

  public static final IValue<Integer> farmSaplingReserveAmount = F.make("farmSaplingReserveAmount", 8, //
      "The amount of saplings the farm has to have in reserve to switch to shearing all leaves. If there are less "
          + "saplings in store, it will only shear part the leaves and break the others for saplings. Set this to 0 to always shear all leaves.")
      .setRange(0, 64).sync();

  public static final IValue<Boolean> farmStopOnNoOutputSlots = F.make("farmStopOnNoOutputSlots", true, //
      "If this is enabled the farm will stop if there is not at least one empty output slot. Otherwise it will only stop if all output slots are full.").sync();

  public static final IValue<Boolean> disableFarmNotification = F.make("disableFarmNotification", false, //
      "Disable the notification text above the farm block.");

  public static final IValue<Boolean> useOutputQueue = F.make("useOutputQueue", true, //
      "If enabled, output overflow will be queued, otherwise it will spill.").sync();

  public static final IValue<Boolean> enableCarefulCare = F.make("enableCarefulCare", true, //
      "If enabled, the farming area will receive some additional growth ticks. Disabling this can improve performance on budy servers").sync();

  public static final IValue<Integer> waterTankSize = F.make("waterTankSize", 2000, //
      "The size of the water tank in mB. Setting this to 0 disables the tank and forces waterPerFarmland/waterCarefulCare to 0.").setRange(0, 64000).sync();

  public static final IValue<Integer> waterPerFarmland = F.make("waterPerFarmland", 1, //
      "The amount of water in mB that is used every time a farmland block tries to dry out. Setting this to 0 disables the need for water in the tank.")
      .setRange(0, 1000).sync();

  public static final IValue<Integer> waterCarefulCare = F.make("waterCarefulCare", 0, //
      "The amount of water in mB that is used every time the Farming Station boosts a plant. Setting this to 0 disables the need for water in the tank.")
      .setRange(0, 1000).sync();

  public static final IValue<Float> rainWaterChance = F.make("rainWaterChance", 1f, //
      "The chance that rain will flow into the tank. Set to 0 to disable. (Note: Vanilla Cauldron=0.05)").setRange(0, 1).sync();

  public static final IValue<Integer> rainWaterAmount = F.make("rainWaterAmount", 100, //
      "The amount of water in mB that flows into the tankl when it rains. (Note: Vanilla Cauldron=333)").setRange(0, 1000).sync();

  public static final IValue<Boolean> waterFarmlandParticles = F.make("waterFarmlandParticles", true, //
      "If this is enabled the farm will show water particles when watering farmland.");

}
