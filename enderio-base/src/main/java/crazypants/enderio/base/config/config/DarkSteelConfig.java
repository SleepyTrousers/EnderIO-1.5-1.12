package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import crazypants.enderio.base.item.darksteel.upgrade.explosive.ExplosiveTargets;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import net.minecraftforge.fluids.Fluid;

public final class DarkSteelConfig {

  public static final IValueFactoryEIO F_DARK_STEEL = ItemConfig.F.section(".darksteel");

  public static final IValueFactory F_PICKAXE = F_DARK_STEEL.section(".pickaxe");

  public static final IValue<Boolean> rightClickPlaceEnabled_pick = F_PICKAXE.make("rightClickPlaceEnabled", false, //
      "If enabled, right clicking with the dark steel pickaxe will place a block.");

  public static final IValue<Integer> pickPowerUseObsidian = F_PICKAXE.make("powerUseObsidian", 10000, //
      "The amount of energy used to break an obsidian block.").setRange(1, 99999999).sync();
  public static final IValue<Integer> pickEfficiencyObsidian = F_PICKAXE.make("efficiencyObsidian", 50, //
      "The efficiency when breaking obsidian with a powered Dark Pickaxe.").setRange(1, 500).sync();
  public static final IValue<Float> pickApplyObsidianEfficiencyAtHardness = F_PICKAXE.make("obsidianEfficiencyAtHardness", 40f, //
      "If set to a value > 0, the obsidian speed and power use will be used for all blocks with hardness >= to this value.").setRange(1, 10000).sync();
  public static final IValue<Integer> pickPowerUsePerDamagePoint = F_PICKAXE.make("powerUsePerDamagePoint", 750, //
      "Energy use per damage/durability point avoided.").setRange(0, 99999999).sync();
  public static final IValue<Float> pickEfficiencyBoostWhenPowered = F_PICKAXE.make("efficiencyBoostWhenPowered", 2f, //
      "The increase in efficiency when powered.").setRange(1, 20).sync();

  public static final IValueFactory F_DPICK = F_PICKAXE.section(".dark_steel_pickaxe");

  public static final IValue<Boolean> darkSteelPickMinesTiCArdite = F_DPICK.make("canMineTiCArdite", true, //
      "When true the dark steel pick will be able to mine TiC Ardite and Cobalt").sync();

  public static final IValueFactory F_EPICK = F_PICKAXE.section(".end_steel_pickaxe");

  public static final IValue<Boolean> endSteelPickMinesTiCArdite = F_EPICK.make("canMineTiCArdite", true, //
      "When true the end steel pick will be able to mine TiC Ardite and Cobalt").sync();

  public static final IValueFactory F_AXE = F_DARK_STEEL.section(".axe");

  public static final IValue<Boolean> rightClickPlaceEnabled_axe = F_AXE.make("rightClickPlaceEnabled", false, //
      "If enabled, right clicking with the dark steel axe will place a block.");

  public static final IValue<Integer> axePowerUsePerDamagePoint = F_AXE.make("powerUsePerDamagePoint", 750, //
      "Energy use per damage/durability point avoided.").setRange(0, 99999999).sync();
  public static final IValue<Integer> axePowerUsePerDamagePointMultiHarvest = F_AXE.make("powerUsePerDamagePointMultiHarvest", 1500, //
      "Energy per damage/durability point avoided when shift-harvesting multiple logs").setRange(0, 99999999).sync();
  public static final IValue<Float> axeSpeedPenaltyMultiHarvest = F_AXE.make("speedPenaltyMultiHarvest", 4f, //
      "How much slower multi-harvesting logs is.").setRange(1, 40).sync();
  public static final IValue<Float> axeEfficiencyBoostWhenPowered = F_AXE.make("efficiencyBoostWhenPowered", 2f, //
      "The increase in efficiency when powered.").setRange(1, 20).sync();

  public static final IValueFactory F_CROOK = F_DARK_STEEL.section(".crook");

  public static final IValue<Boolean> rightClickPlaceEnabled_crook = F_CROOK.make("rightClickPlaceEnabled", false, //
      "If enabled, right clicking with the dark steel crook will place a block.");
  public static final IValue<Integer> crookEnergyPerDamage = F_CROOK.make("energyPerDamage", 150, //
      "Energy use per damage/durability point avoided.").setRange(1, 99999999).sync();
  public static final IValue<Integer> crookEnergyPerDamageMulti = F_CROOK.make("energyPerDamageMulti", 150, //
      "Energy per damage/durability point avoided when shift-harvesting multiple blocks.").setRange(1, 99999999).sync();
  public static final IValue<Integer> crookExtraDropsUnpowered = F_CROOK.make("extraDropsUnpowered", 3, //
      "Number of extra tries to get drops for an unpowered crook.").setRange(0, 32).sync();
  public static final IValue<Integer> crookExtraDropsPowered = F_CROOK.make("extraDropsPowered", 5, //
      "Number of extra tries to get drops for a powered crook.").setRange(0, 32).sync();

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

  public static final IValue<Integer> darkSteelSwordPowerUsePerHit = F_SWORD.make("powerUsePerHit", 750, //
      "The amount of energy used per hit.").setRange(1, 99999999).sync();

  public static final IValue<Double> darkSteelSwordEnderPearlDropChance = F_SWORD.make("enderPearlDropChance", 1.05, //
      "The chance that an ender pearl will be dropped when using the sword (0 = no chance, 1 = 100% chance; can go over 100%).").setRange(0, 10).sync();
  public static final IValue<Double> darkSteelSwordEnderPearlDropChancePerLooting = F_SWORD.make("enderPearlDropChancePerLooting", 0.5, //
      "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)")
      .setRange(0, 5).sync();

  public static final IValueFactory F_SHEARS = F_DARK_STEEL.section(".shears");

  public static final IValue<Integer> shearsDurabilityFactor = F_SHEARS.make("durabilityFactor", 5, //
      "How much more durable as vanilla shears they are.").setRange(0.5, 50).sync();
  public static final IValue<Integer> shearsPowerUsePerDamagePoint = F_SHEARS.make("powerUsePerDamagePoint", 250, //
      "Energy use per damage/durability point avoided.").setRange(0, 99999999).sync();
  public static final IValue<Float> shearsEfficiencyBoostWhenPowered = F_SHEARS.make("efficiencyBoostWhenPowered", 2f, //
      "The increase in efficiency when powered.").setRange(1, 20).sync();
  public static final IValue<Integer> shearsBlockAreaBoostWhenPowered = F_SHEARS.make("blockAreaBoostWhenPowered", 4, //
      "The increase in effected area (radius) when powered and used on blocks.").setRange(0, 16).sync();
  public static final IValue<Float> shearsEntityAreaBoostWhenPowered = F_SHEARS.make("entityAreaBoostWhenPowered", 5f, //
      "The increase in effected area (radius) when powered and used on sheep.").setRange(0, 16).sync();

  public static final IValueFactoryEIO F_UPGRADES = F_DARK_STEEL.section(".upgrades");

  public static final IValue<String> disabledUpgrades = F_UPGRADES.make("disabled", "",
      "Comma-separated list of IDs of upgrades that should be disabled. "
          + "Those upgrades will be registered with the game engine, but they will be removed from the list of available upgrades. Please note that this may "
          + "not work well with upgrades that already exist on items. It will also not work well when other upgrades depend on them.")
      .startup();

  public static final IValueFactory F_HOE = F_UPGRADES.section(".hoe");

  public static final IValue<Integer> darkSteelHoeCost = F_HOE.make("darkSteelHoeCost", 4, "Number of levels required for the 'Hoe' upgrade.");

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

  public static final NNList<IValue<Integer>> speedUpgradeCost = new NNList<>( //
      F_SPEED.make("upgradeCost1", 4, "Number of levels required for the 'Speed I' upgrade.").setRange(1, 99).sync(), //
      F_SPEED.make("upgradeCost2", 6, "Number of levels required for the 'Speed II' upgrade.").setRange(1, 99).sync(), //
      F_SPEED.make("upgradeCost3", 8, "Number of levels required for the 'Speed III' upgrade.").setRange(1, 99).sync());

  public static final IValueFactory F_JUMP = F_UPGRADES.section(".jump");

  public static final NNList<IValue<Integer>> jumpUpgradeCost = new NNList<>( //
      F_JUMP.make("upgradeCost1", 4, "Number of levels required for the 'Jump I' upgrade.").setRange(1, 99).sync(), //
      F_JUMP.make("upgradeCost2", 6, "Number of levels required for the 'Jump II' upgrade.").setRange(1, 99).sync(), //
      F_JUMP.make("upgradeCost3", 8, "Number of levels required for the 'Jump III' upgrade.").setRange(1, 99).sync());

  public static final IValue<Double> darkSteelBootsJumpModifier = F_JUMP.make("modifier", 1.5, //
      "Jump height modifier applied when jumping with Dark Steel Boots equipped").setRange(1, 3).sync();

  public static final IValueFactoryEIO F_INVENTORY = F_UPGRADES.section(".inventory");

  public static final NNList<IValue<Integer>> inventoryUpgradeCost = new NNList<>( //
      F_INVENTORY.make("upgradeCost1", 12, "Cost for the inventory I upgrade in levels.").setRange(1, 99).sync(),
      F_INVENTORY.make("upgradeCost2", 20, "Cost for the inventory II upgrade in levels.").setRange(1, 99).sync(),
      F_INVENTORY.make("upgradeCost3", 32, "Cost for the inventory III upgrade in levels.").setRange(1, 99).sync());

  public enum COLS {
    COL1(1),
    COL3(3),
    COL5(5),
    COL7(7),
    COL9(9);
    public final int cols;

    private COLS(int cols) {
      this.cols = cols;
    }
  }

  public static final NNList<IValue<COLS>> inventoryUpgradeCols = new NNList<>( //
      F_INVENTORY.make("columnsFeet", COLS.COL3, "Number of inventory columns the inventory upgrade gives for foot armor.").sync(),
      F_INVENTORY.make("columnsLegs", COLS.COL5, "Number of inventory columns the inventory upgrade gives for leg armor.").sync(),
      F_INVENTORY.make("columnsBody", COLS.COL9, "Number of inventory columns the inventory upgrade gives for body armor.").sync(),
      F_INVENTORY.make("columnsHead", COLS.COL1, "Number of inventory columns the inventory upgrade gives for head armor.").sync());

  public static final NNList<IValue<Integer>> inventoryUpgradeRows = new NNList<>( //
      F_INVENTORY.make("rows1", 2, "Number of inventory columns the inventory I upgrade gives.").setRange(1, 6).sync(),
      F_INVENTORY.make("rows2", 4, "Number of inventory columns the inventory II upgrade gives.").setRange(1, 6).sync(),
      F_INVENTORY.make("rows3", 6, "Number of inventory columns the inventory III upgrade gives.").setRange(1, 6).sync());

  public static final IValueFactoryEIO F_EXPLOSIVE = F_UPGRADES.section(".explosive");

  public static final NNList<IValue<Integer>> explosiveUpgradeCost = new NNList<>( //
      F_EXPLOSIVE.make("upgradeCost1", 8, "Cost for the explosive I upgrade in levels.").setRange(1, 99).sync(),
      F_EXPLOSIVE.make("upgradeCost2", 12, "Cost for the explosive II upgrade in levels.").setRange(1, 99).sync(),
      F_EXPLOSIVE.make("upgradeCost3", 18, "Cost for the explosive III upgrade in levels.").setRange(1, 99).sync(),
      F_EXPLOSIVE.make("upgradeCost4", 26, "Cost for the explosive IV upgrade in levels.").setRange(1, 99).sync(),
      F_EXPLOSIVE.make("upgradeCost5", 36, "Cost for the explosive V upgrade in levels.").setRange(1, 99).sync());

  public static final IValue<Integer> explosiveUpgradeEnergyPerBlock = F_EXPLOSIVE.make("energyPerBlock", 20, //
      "Extra energy the explosive upgrade uses to blow up blocks. This goes on top of the energy used to counteract durability loss.").setMin(0).sync();

  public static final IValue<Float> explosiveUpgradeDurabilityChance = F_EXPLOSIVE.make("durabilityChance", .3f, //
      "Chance that employing the explosive upgrade to blow up extra blocks costs the pickaxe durability.").setRange(0, 1).sync();

  public static final IValue<ExplosiveTargets> explosiveUpgradeTargets = F_EXPLOSIVE.make("targets", ExplosiveTargets.DEFAULT, //
      "Which kinds of blocks should the explosive upgrade blow up? DEFAULT: Limited list of trash blocks. NO_INVENORY: All blocks that don't have a TileEntity. "
          + " CUSTOM: Only the blocks in the config values 'customStone'/'customDirt'. DEFAULT_AND_CUSTOM: Combines DEFAULT and CUSTOM. ALL: Anything (dangerous!)"
          + "Use this in modpacks that have a large number of modded stone or dirt in their worldgen.")
      .sync();

  public static final IValue<Things> explosiveUpgradeCustomStone = F_EXPLOSIVE.make("customStone", new Things(), //
      "Custom 'stone' target blocks for the explosive upgrade. See 'targets'.").sync();

  public static final IValue<Things> explosiveUpgradeCustomDirt = F_EXPLOSIVE.make("customDirt", new Things(), //
      "Custom 'dirt' target blocks for the explosive upgrade. See 'targets'. (Used whith the 'spoon' upgrade.)").sync();

  public static final IValueFactory F_CARPET = F_EXPLOSIVE.section(".carpet");

  public static final IValue<Integer> explosiveCarpetUpgradeCost = F_CARPET.make("upgradeCost", 8, //
      "Cost for the explosive carpet upgrade in levels.").setRange(1, 99).sync();

  public static final IValueFactory F_DEPTH = F_EXPLOSIVE.section(".carpet");

  public static final IValue<Integer> explosiveDepthUpgradeCost = F_DEPTH.make("upgradeCost", 8, //
      "Cost for the explosive depth upgrade in levels.").setRange(1, 99).sync();

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

  public static final IValueFactory F_SWIM = F_UPGRADES.section(".swim");

  public static final IValue<Integer> swimCost = F_SWIM.make("upgradeCost", 4, "Number of levels required for the 'Swim' upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F_NIGHT_VISION = F_UPGRADES.section(".nightVision");

  public static final IValue<Integer> nightVisionCost = F_NIGHT_VISION.make("upgradeCost", 4, "Number of levels required for the 'Night Vision' upgrade.")
      .setRange(1, 99).sync();

  public static final IValueFactory F_TOP = F_UPGRADES.section(".theOneProbe");

  public static final IValue<Integer> topCost = F_TOP.make("upgradeCost", 4, //
      "Number of levels required for 'The One Probe' upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F_GLIDER = F_UPGRADES.section(".glider");

  public static final IValue<Integer> gliderCost = F_GLIDER.make("upgradeCost", 4, //
      "Number of levels required for the 'Glider' upgrade.").setRange(1, 99).sync();
  public static final IValue<Double> gliderHorizontalSpeed = F_GLIDER.make("horizontalSpeed", 0.03, //
      "Horizontal movement speed modifier when gliding.").setRange(0.001, 0.6).sync();
  public static final IValue<Double> gliderVerticalSpeed = F_GLIDER.make("verticalSpeed", -0.05, //
      "Rate of altitude loss when gliding.").setRange(-1, -0.001).sync();
  public static final IValue<Double> gliderVerticalSpeedSprinting = F_GLIDER.make("verticalSpeedSprinting", -0.15, //
      "Rate of altitude loss when sprinting and gliding.").setRange(-3, -0.001).sync();

  public static final IValueFactory F_ELYTRA = F_UPGRADES.section(".elytra");

  public static final IValue<Integer> elytraCost = F_ELYTRA.make("upgradeCost", 10, //
      "Number of levels required for the 'Elytra' upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F_SOUND_LOCATOR = F_UPGRADES.section(".soundLocator");

  public static final IValue<Integer> soundLocatorCost = F_SOUND_LOCATOR.make("upgradeCost", 4, //
      "Number of levels required for the 'Sound Locator' upgrade.").setRange(1, 99).sync();
  public static final IValue<Integer> soundLocatorRange = F_SOUND_LOCATOR.make("range", 40, //
      "Range of the 'Sound Locator' upgrade.").setRange(1, 200).sync();
  public static final IValue<Integer> soundLocatorLifespan = F_SOUND_LOCATOR.make("lifespan", 40, //
      "Number of ticks the 'Sound Locator' icons are displayed for.").setRange(1, 200).sync();

  public static final IValueFactory F_DIRECT = F_UPGRADES.section(".direct");

  public static final IValue<Integer> directCost = F_DIRECT.make("upgradeCost", 8, //
      "Number of levels required for the 'Direct' upgrade.").setRange(1, 99).sync();

  public static final IValue<Integer> directEnergyCost = F_DIRECT.make("energyCost", 100, //
      "Amount of energy used by the 'Direct' upgrade to pick up one stack of stuff.").setRange(0, 999999).sync();

  public static final IValueFactory F_PADDING = F_UPGRADES.section(".padding");

  public static final IValue<Integer> paddingCost = F_PADDING.make("upgradeCost", 4, //
      "Number of levels required for the 'Padding' upgrade.").setRange(1, 99).sync();
  public static final IValue<Double> cutoffDistance = F_PADDING.make("cutoffDistance", 15d, //
      "Distance in blocks that is no longer considered 'nearby' by the 'Padding' upgrade. Sounds at least this far away will play normally.").setRange(1, 99);
  public static final IValue<Float> pitchAdjust = F_PADDING.make("pitchAdjust", .8f, //
      "All sounds will be adjusted in pitch by this factor when using the 'Padding' upgrade. (1 for off").setRange(0.01, 1);

  public static final IValueFactory F_GOGGLES_OF_REVEALING = F_UPGRADES.section(".gogglesofrevealing");

  public static final IValue<Integer> gogglesOfRevealingCost = F_GOGGLES_OF_REVEALING.make("upgradeCost", 4, //
      "Number of levels required for the 'Goggles of Revealing' upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F_THAUMATURGE_ROBES = F_UPGRADES.section(".thaumaturgerobes");

  public static final IValue<Integer> thaumaturgeRobesCost = F_THAUMATURGE_ROBES.make("upgradeCost", 4, //
      "Number of levels required for the 'Thaumatruge's Robes' upgrades.").setRange(1, 99).sync();

  public static final IValueFactory F_TRAVEL = F_UPGRADES.section(".travel");

  public static final IValue<Integer> travelCost = F_TRAVEL.make("upgradeCost", 16, //
      "Number of levels required for the 'Travel' upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F_SPOON = F_UPGRADES.section(".spoon");

  public static final IValue<Integer> spoonCost = F_SPOON.make("upgradeCost", 4, //
      "Number of levels required for the 'Spoon' upgrade.").setRange(1, 99).sync();

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

  public static final IValueFactory F_BACKHOE = F_DARK_STEEL.section(".backhoe");

  public static final IValue<Integer> backhoeDurability = F_BACKHOE.make("durability", 2000 * 3, //
      "Durability of the Dark Steel Backhoe.").setRange(1, 99999999).sync();
  public static final IValue<Integer> backhoeEnergyPerDamage = F_BACKHOE.make("energyPerDamage", 250, //
      "Energy use per damage/durability point avoided.").setRange(1, 99999999).sync();

  public static final IValueFactory F_ARMOR = F_DARK_STEEL.section(".armor");

  public static final IValue<Boolean> armorDrainPowerFromInventory = F_ARMOR.make("drainPowerFromInventory", false, //
      "If true, dark steel armor will drain power stored energy in power containers in the players inventory.").sync();

  public static final IValue<Integer> bootsJumpPowerCost = F_ARMOR.make("bootsJumpPowerCost", 150, //
      "Base amount of power used per jump energy dark steel boots. The second jump in a 'double jump' uses 2x this etc").setRange(1, 99999999).sync();
  public static final IValue<Integer> fallDistanceCost = F_ARMOR.make("fallDistanceCost", 75, //
      "Amount of power used per block height of fall distance damage negated.").setRange(1, 99999999);

  public static final IValue<Boolean> slotZeroPlacesEight = F_DARK_STEEL.make("slotZeroPlacesEight", true, //
      "Should the dark steel placement, when in the first (0th) slot, place the item in the last slot. If false, will place what's in the second slot.");

}
