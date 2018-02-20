package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class FarmConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "farm"));

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
      "If enabled, output overflow will be queued, otherwise it will spill.");

}
