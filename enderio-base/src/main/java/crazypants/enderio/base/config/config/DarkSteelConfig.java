package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import net.minecraftforge.fluids.Fluid;

public final class DarkSteelConfig {

  public static final IValueFactory F_DARK_STEEL = ItemConfig.F.section(".darksteel");

  public static final IValueFactory F_PICKAXE = F_DARK_STEEL.section(".pickaxe");

  public static final IValue<Boolean> rightClickPlaceEnabled_pick = F_PICKAXE.make("rightClickPlaceEnabled", false, //
      "If enabled, right clicking with the dark steel pickaxe will place a block.");

  public static final IValueFactory F_AXE = F_DARK_STEEL.section(".axe");

  public static final IValue<Boolean> rightClickPlaceEnabled_axe = F_AXE.make("rightClickPlaceEnabled", false, //
      "If enabled, right clicking with the dark steel axe will place a block.");
  public static final IValue<Integer> darkSteelHoeCost = F_AXE.make("darkSteelHoeCost", 4, "Number of levels required for the 'Hoe' upgrade.");

  public static final IValueFactory F_SWORD = F_DARK_STEEL.section(".sword");

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered = F_SWORD.make("damageBonusEmpowered1", 1f, //
      "The extra damage dealt when the sword is empowered I and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered = F_SWORD.make("speedBonusEmpowered1", 0.4f, //
      "The increase in attack speed when the sword is empowered I and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered1 = F_SWORD.make("damageBonusEmpowered2", 2f, //
      "The extra damage dealt when the sword is empowered II and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered1 = F_SWORD.make("speedBonusEmpowered2", 0.45f, //
      "The increase in attack speed when the sword is empowered II and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered2 = F_SWORD.make("damageBonusEmpowered3", 3f, //
      "The extra damage dealt when the sword is empowered III and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered2 = F_SWORD.make("speedBonusEmpowered3", 0.5f, //
      "The increase in attack speed when the sword is empowered III and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered3 = F_SWORD.make("damageBonusEmpowered4", 4f, //
      "The extra damage dealt when the sword is empowered IV and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered3 = F_SWORD.make("speedBonusEmpowered4", 0.55f, //
      "The increase in attack speed when the sword is empowered IV and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered4 = F_SWORD.make("damageBonusEmpowered5", 5f, //
      "The extra damage dealt when the sword is empowered V and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered4 = F_SWORD.make("speedBonusEmpowered5", 0.6f, //
      "The increase in attack speed when the sword is empowered V and has energy.").setRange(0, 2).sync();

  public static final IValueFactory F_UPGRADES = F_DARK_STEEL.section(".upgrades");

  public static final IValueFactory F_SPEED = F_UPGRADES.section(".speed");

  public static final IValue<Float> darkSteelSpeedWalkModifier1 = F_SPEED.make("walkModifier1", 0.15f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier1 = F_SPEED.make("sprintModifier1", 0.10f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier2 = F_SPEED.make("walkModifier2", 0.30f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier2 = F_SPEED.make("sprintModifier2", 0.30f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier3 = F_SPEED.make("walkModifier3", 0.45f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier3 = F_SPEED.make("sprintModifier3", 0.50f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();

  public static final IValue<Integer> darkSteelSpeedWalkEnergyCost = F_SPEED.make("walkEnergyCost", 80, //
      "Energy cost of walking one block in the Dark Steel Leggings with Speed (any level).").setMin(0).sync();
  public static final IValue<Integer> darkSteelSpeedSprintEnergyCost = F_SPEED.make("sprintEnergyCost", 320, //
      "Energy cost of walking one block in the Dark Steel Leggings with Speed (any level).").setMin(0).sync();

  public static final IValue<Float> darkSteelSpeedBonusEmpowered = F_SPEED.make("empoweredBonus1", 1f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered1 = F_SPEED.make("empoweredBonus2", 1.10f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered II and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered2 = F_SPEED.make("empoweredBonus3", 1.50f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered III and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered3 = F_SPEED.make("empoweredBonus4", 2.00f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered IV and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered4 = F_SPEED.make("empoweredBonus5", 2.50f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered V and have energy.").setRange(0, 2).sync();

  public static final IValueFactory F_EXPLOSIVE = F_UPGRADES.section(".explosive");

  public static final IValue<Integer> explosiveUpgradeCost = F_EXPLOSIVE.make("upgradeCost", 8, //
      "Cost for the explosive upgrade in levels.").setRange(1, 99).sync();

  public static final IValue<Integer> explosiveUpgradeEnergyPerBlock = F_EXPLOSIVE.make("energyPerBlock", 20, //
      "Extra energy the explosive upgrade uses to blow up blocks. This goes on top of the energy used to counteract durability loss.").setMin(0).sync();

  public static final IValue<Float> explosiveUpgradeDurabilityChance = F_EXPLOSIVE.make("durabilityChance", .3f, //
      "Chance that employing the explosive upgrade to blow up extra blocks costs the pickaxe durability.").setRange(0, 1).sync();

  public static final IValue<Boolean> explosiveUpgradeUnlimitedTargets = F_EXPLOSIVE.make("unlimitedTargets", false, //
      "Should the explosive upgrade blow up any kind of block the pickaxe can mine? If disabled, only a limited list of trash blocks will be blown up. "
          + "Enable this in modpacks that have a large number of modded stone or dirt in their worldgen.")
      .sync();

  public static final IValueFactory F_COLDFIRE = F_DARK_STEEL.section(".coldfire");

  public static final IValue<Fluid> fluidType = F_COLDFIRE.makeFluid("fluidType", "vapor_of_levity", //
      "The type of fluid required to ignite cold fire.").sync();

  public static final IValue<Integer> mbCapacity = F_COLDFIRE.make("mbCapacity", 1000, //
      "The amount of fluid in mb the item can hold.").setMin(0).sync();

  public static final IValue<Integer> mbPerUse = F_COLDFIRE.make("mbPerUse", 10, //
      "The amount of fluid in mb used per usage. If set to <= 0 fluid use will be disabled.").setMin(-1).sync();

  public static final IValueFactory F_ENERGY = F_UPGRADES.section(".empowered");

  public static final IValue<Integer> energyUpgradeLevelCostEmpowered0 = F_ENERGY.make("upgradeCost1", 4, //
      "Cost for the 'Empowered I' upgrade in levels.").setRange(1, 99).sync();
  public static final IValue<Integer> energyUpgradeLevelCostEmpowered1 = F_ENERGY.make("upgradeCost2", 8, //
      "Cost for the 'Empowered II' upgrade in levels.").setRange(1, 99).sync();
  public static final IValue<Integer> energyUpgradeLevelCostEmpowered2 = F_ENERGY.make("upgradeCost3", 12, //
      "Cost for the 'Empowered III' upgrade in levels.").setRange(1, 99).sync();
  public static final IValue<Integer> energyUpgradeLevelCostEmpowered3 = F_ENERGY.make("upgradeCost4", 16, //
      "Cost for the 'Empowered IV' upgrade in levels.").setRange(1, 99).sync();
  public static final IValue<Integer> energyUpgradeLevelCostEmpowered4 = F_ENERGY.make("upgradeCost5", 20, //
      "Cost for the 'Empowered V' upgrade in levels.").setRange(1, 99).sync();

  public static final IValue<Integer> energyUpgradePowerStorageEmpowered0 = F_ENERGY.make("powerStorage1", 100000, //
      "Size of the internal energy storage of the 'Empowered I' upgrade.").setRange(1000, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerStorageEmpowered1 = F_ENERGY.make("powerStorage2", 150000, //
      "Size of the internal energy storage of the 'Empowered II' upgrade.").setRange(1000, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerStorageEmpowered2 = F_ENERGY.make("powerStorage3", 250000, //
      "Size of the internal energy storage of the 'Empowered III' upgrade.").setRange(1000, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerStorageEmpowered3 = F_ENERGY.make("powerStorage4", 1000000, //
      "Size of the internal energy storage of the 'Empowered IV' upgrade.").setRange(1000, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerStorageEmpowered4 = F_ENERGY.make("powerStorage5", 2500000, //
      "Size of the internal energy storage of the 'Empowered V' upgrade.").setRange(1000, 99999999).sync();

  public static final IValue<Integer> energyUpgradePowerTransferEmpowered0 = F_ENERGY.make("powerTransfer1", 1000, //
      "Maximum energy input/output of the 'Empowered I' upgrade.").setRange(10, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerTransferEmpowered1 = F_ENERGY.make("powerTransfer2", 1500, //
      "Maximum energy input/output of the 'Empowered II' upgrade.").setRange(10, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerTransferEmpowered2 = F_ENERGY.make("powerTransfer3", 2500, //
      "Maximum energy input/output of the 'Empowered III' upgrade.").setRange(10, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerTransferEmpowered3 = F_ENERGY.make("powerTransfer4", 10000, //
      "Maximum energy input/output of the 'Empowered IV' upgrade.").setRange(10, 99999999).sync();
  public static final IValue<Integer> energyUpgradePowerTransferEmpowered4 = F_ENERGY.make("powerTransfer5", 25000, //
      "Maximum energy input/output of the 'Empowered V' upgrade.").setRange(10, 99999999).sync();

  public static final IValue<Double> energyUpgradeAbsorptionRatioEmpowered0 = F_ENERGY.make("absorptionRatio1", .5, //
      "Ratio of damage absorbed by energy of the 'Empowered I' upgrade. (0=none, 1=all)").setRange(0, 1).sync();
  public static final IValue<Double> energyUpgradeAbsorptionRatioEmpowered1 = F_ENERGY.make("absorptionRatio2", .6, //
      "Ratio of damage absorbed by energy of the 'Empowered II' upgrade. (0=none, 1=all)").setRange(0, 1).sync();
  public static final IValue<Double> energyUpgradeAbsorptionRatioEmpowered2 = F_ENERGY.make("absorptionRatio3", .7, //
      "Ratio of damage absorbed by energy of the 'Empowered III' upgrade. (0=none, 1=all)").setRange(0, 1).sync();
  public static final IValue<Double> energyUpgradeAbsorptionRatioEmpowered3 = F_ENERGY.make("absorptionRatio4", .85, //
      "Ratio of damage absorbed by energy of the 'Empowered IV' upgrade. (0=none, 1=all)").setRange(0, 1).sync();
  public static final IValue<Double> energyUpgradeAbsorptionRatioEmpowered4 = F_ENERGY.make("absorptionRatio5", .95, //
      "Ratio of damage absorbed by energy of the 'Empowered V' upgrade. (0=none, 1=all)").setRange(0, 1).sync();

  public static final IValueFactory F_BOW = F_DARK_STEEL.section(".bow");

  public static final IValue<Integer> bowPowerUsePerDamagePoint = F_BOW.make("energyUsePerDamagePoint", 1000, //
      "Amount of energy needed to mitigate one point of item damage.").setRange(0, 99999999).sync();
  public static final IValue<Integer> bowPowerUsePerDraw = F_BOW.make("energyUsePerDraw", 750, //
      "Amount of energy needed to fully draw the bow.").setRange(0, 99999999).sync();
  public static final IValue<Integer> bowPowerUsePerHoldTick = F_BOW.make("energyUsePerHoldTick", 5, //
      "Amount of energy needed hold the bow fully draws (per tick).").setRange(0, 99999999).sync();

  public static final IValueFactory F_DBOW = F_BOW.section(".dark_bow");

  public static final NNList<IValue<Integer>> darkBowDrawSpeed = new NNList<>( //
      F_DBOW.make("drawSpeedUnpowered", 30, "Draw speed of the Dark Steel Bow when not empowered or out of energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_DBOW.make("drawSpeed1", 20, "Draw speed of the Dark Steel Bow when 'Empowered I' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_DBOW.make("drawSpeed2", 18, "Draw speed of the Dark Steel Bow when 'Empowered II' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_DBOW.make("drawSpeed3", 16, "Draw speed of the Dark Steel Bow when 'Empowered III' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_DBOW.make("drawSpeed4", 14, "Draw speed of the Dark Steel Bow when 'Empowered IV' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_DBOW.make("drawSpeed5", 12, "Draw speed of the Dark Steel Bow when 'Empowered V' and it has energy. (normal bow is 20)").setRange(1, 100).sync());

  public static final NNList<IValue<Float>> darkBowForceMultipliers = new NNList<>( //
      F_DBOW.make("forceUnpowered", 1.1f, "Force multiplier of the Dark Steel Bow when not empowered or out of energy.").setRange(1, 10).sync(), //
      F_DBOW.make("force1", 1.2f, "Force multiplier of the Dark Steel Bow when 'Empowered I' and it has energy.").setRange(1, 10).sync(), //
      F_DBOW.make("force2", 1.3f, "Force multiplier of the Dark Steel Bow when 'Empowered II' and it has energy.").setRange(1, 10).sync(), //
      F_DBOW.make("force3", 1.4f, "Force multiplier of the Dark Steel Bow when 'Empowered III' and it has energy.").setRange(1, 10).sync(), //
      F_DBOW.make("force4", 1.5f, "Force multiplier of the Dark Steel Bow when 'Empowered IV' and it has energy.").setRange(1, 10).sync(), //
      F_DBOW.make("force5", 1.6f, "Force multiplier of the Dark Steel Bow when 'Empowered V' and it has energy.").setRange(1, 10).sync());

  public static final NNList<IValue<Float>> darkBowFOVMultipliers = new NNList<>( //
      F_DBOW.make("fovUnpowered", .25f, "FOV multiplier of the Dark Steel Bow when not empowered or out of energy.").setRange(0, 1).sync(), //
      F_DBOW.make("fov1", .3f, "FOV multiplier of the Dark Steel Bow when 'Empowered I' and it has energy.").setRange(0, 1).sync(), //
      F_DBOW.make("fov2", .35f, "FOV multiplier of the Dark Steel Bow when 'Empowered II' and it has energy.").setRange(0, 1).sync(), //
      F_DBOW.make("fov3", .4f, "FOV multiplier of the Dark Steel Bow when 'Empowered III' and it has energy.").setRange(0, 1).sync(), //
      F_DBOW.make("fov4", .45f, "FOV multiplier of the Dark Steel Bow when 'Empowered IV' and it has energy.").setRange(0, 1).sync(), //
      F_DBOW.make("fov5", .5f, "FOV multiplier of the Dark Steel Bow when 'Empowered V' and it has energy.").setRange(0, 1).sync());

  public static final NNList<IValue<Double>> darkBowDamageBonus = new NNList<>( //
      F_DBOW.make("damageUnpowered", 0.0, "Damage bonus of the Dark Steel Bow when not empowered or out of energy.").setRange(0, 30).sync(), //
      F_DBOW.make("damage1", 0.0, "Damage bonus of the Dark Steel Bow when 'Empowered I' and it has energy.").setRange(0, 30).sync(), //
      F_DBOW.make("damage2", 0.0, "Damage bonus of the Dark Steel Bow when 'Empowered II' and it has energy.").setRange(0, 30).sync(), //
      F_DBOW.make("damage3", 0.0, "Damage bonus of the Dark Steel Bow when 'Empowered III' and it has energy.").setRange(0, 30).sync(), //
      F_DBOW.make("damage4", 0.0, "Damage bonus of the Dark Steel Bow when 'Empowered IV' and it has energy.").setRange(0, 30).sync(), //
      F_DBOW.make("damage5", 0.0, "Damage bonus of the Dark Steel Bow when 'Empowered V' and it has energy.").setRange(0, 30).sync());

  public static final IValueFactory F_EBOW = F_BOW.section(".end_bow");

  public static final NNList<IValue<Integer>> endBowDrawSpeed = new NNList<>( //
      F_EBOW.make("drawSpeedUnpowered", 20, "Draw speed of the End Steel Bow when not empowered or out of energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_EBOW.make("drawSpeed1", 15, "Draw speed of the End Steel Bow when 'Empowered I' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_EBOW.make("drawSpeed2", 12, "Draw speed of the End Steel Bow when 'Empowered II' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_EBOW.make("drawSpeed3", 11, "Draw speed of the End Steel Bow when 'Empowered III' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_EBOW.make("drawSpeed4", 10, "Draw speed of the End Steel Bow when 'Empowered IV' and it has energy. (normal bow is 20)").setRange(1, 100).sync(), //
      F_EBOW.make("drawSpeed5", 9, "Draw speed of the End Steel Bow when 'Empowered V' and it has energy. (normal bow is 20)").setRange(1, 100).sync());

  public static final NNList<IValue<Float>> endBowForceMultipliers = new NNList<>( //
      F_EBOW.make("forceUnpowered", 1.25f, "Force multiplier of the End Steel Bow when not empowered or out of energy.").setRange(1, 10).sync(), //
      F_EBOW.make("force1", 1.35f, "Force multiplier of the End Steel Bow when 'Empowered I' and it has energy.").setRange(1, 10).sync(), //
      F_EBOW.make("force2", 1.45f, "Force multiplier of the End Steel Bow when 'Empowered II' and it has energy.").setRange(1, 10).sync(), //
      F_EBOW.make("force3", 1.55f, "Force multiplier of the End Steel Bow when 'Empowered III' and it has energy.").setRange(1, 10).sync(), //
      F_EBOW.make("force4", 1.65f, "Force multiplier of the End Steel Bow when 'Empowered IV' and it has energy.").setRange(1, 10).sync(), //
      F_EBOW.make("force5", 1.75f, "Force multiplier of the End Steel Bow when 'Empowered V' and it has energy.").setRange(1, 10).sync());

  public static final NNList<IValue<Float>> endBowFOVMultipliers = new NNList<>( //
      F_EBOW.make("fovUnpowered", .3f, "FOV multiplier of the End Steel Bow when not empowered or out of energy.").setRange(0, 1).sync(), //
      F_EBOW.make("fov1", .35f, "FOV multiplier of the End Steel Bow when 'Empowered I' and it has energy.").setRange(0, 1).sync(), //
      F_EBOW.make("fov2", .4f, "FOV multiplier of the End Steel Bow when 'Empowered II' and it has energy.").setRange(0, 1).sync(), //
      F_EBOW.make("fov3", .45f, "FOV multiplier of the End Steel Bow when 'Empowered III' and it has energy.").setRange(0, 1).sync(), //
      F_EBOW.make("fov4", .5f, "FOV multiplier of the End Steel Bow when 'Empowered IV' and it has energy.").setRange(0, 1).sync(), //
      F_EBOW.make("fov5", .55f, "FOV multiplier of the End Steel Bow when 'Empowered V' and it has energy.").setRange(0, 1).sync());

  public static final NNList<IValue<Double>> endBowDamageBonus = new NNList<>( //
      F_EBOW.make("damageUnpowered", 0.0, "Damage bonus of the End Steel Bow when not empowered or out of energy.").setRange(0, 30).sync(), //
      F_EBOW.make("damage1", 0.0, "Damage bonus of the End Steel Bow when 'Empowered I' and it has energy.").setRange(0, 30).sync(), //
      F_EBOW.make("damage2", 0.0, "Damage bonus of the End Steel Bow when 'Empowered II' and it has energy.").setRange(0, 30).sync(), //
      F_EBOW.make("damage3", 0.0, "Damage bonus of the End Steel Bow when 'Empowered III' and it has energy.").setRange(0, 30).sync(), //
      F_EBOW.make("damage4", 0.0, "Damage bonus of the End Steel Bow when 'Empowered IV' and it has energy.").setRange(0, 30).sync(), //
      F_EBOW.make("damage5", 0.0, "Damage bonus of the End Steel Bow when 'Empowered V' and it has energy.").setRange(0, 30).sync());

  public static final IValueFactory F_TAP = F_DARK_STEEL.section(".treetap");

  public static final IValue<Integer> tapDurability = F_TAP.make("durability", 2000, //
      "Durability of the Dark Steel Tree Tap.").setRange(1, 99999999).sync();
  public static final IValue<Integer> tapEnergyPerDamage = F_TAP.make("energyPerDamage", 750, //
      "Energy use per damage/durability point avoided.").setRange(1, 99999999).sync();

}
