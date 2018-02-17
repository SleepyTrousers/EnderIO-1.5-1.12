package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class DarkSteelConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.sword"));

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered = F.make("damageBonusEmpowered1", 1f, //
      "The extra damage dealt when the sword is empowered I and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered = F.make("speedBonusEmpowered1", 0.4f, //
      "The increase in attack speed when the sword is empowered I and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered1 = F.make("damageBonusEmpowered2", 2f, //
      "The extra damage dealt when the sword is empowered II and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered1 = F.make("speedBonusEmpowered2", 0.45f, //
      "The increase in attack speed when the sword is empowered II and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered2 = F.make("damageBonusEmpowered3", 3f, //
      "The extra damage dealt when the sword is empowered III and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered2 = F.make("speedBonusEmpowered3", 0.5f, //
      "The increase in attack speed when the sword is empowered III and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered3 = F.make("damageBonusEmpowered4", 4f, //
      "The extra damage dealt when the sword is empowered IV and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered3 = F.make("speedBonusEmpowered4", 0.55f, //
      "The increase in attack speed when the sword is empowered IV and has energy.").setRange(0, 2).sync();

  public static final SectionedValueFactory F1 = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.upgrades.speed"));

  public static final IValue<Float> darkSteelSpeedWalkModifier1 = F1.make("walkModifier1", 0.15f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier1 = F1.make("sprintModifier1", 0.10f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier2 = F1.make("walkModifier2", 0.30f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier2 = F1.make("sprintModifier2", 0.30f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier3 = F1.make("walkModifier3", 0.45f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier3 = F1.make("sprintModifier3", 0.50f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedBonusEmpowered = F1.make("empoweredBonus1", 1f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered1 = F1.make("empoweredBonus2", 1.10f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered II and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered2 = F1.make("empoweredBonus3", 1.50f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered III and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered3 = F1.make("empoweredBonus4", 2.00f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered IV and have energy.").setRange(0, 2).sync();

  public static final SectionedValueFactory F2 = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.upgrades.explosive"));

  public static final IValue<Integer> explosiveUpgradeCost = F2.make("upgradeCost", 8, //
      "Cost for the explosive upgrade in levels.").setRange(1, 99).sync();

  public static final IValue<Integer> explosiveUpgradeEnergyPerBlock = F2.make("energyPerBlock", 20, //
      "Extra energy the explosive upgrade uses to blow up blocks. This goes on top of the energy used to counteract durability loss.").setMin(0).sync();

  public static final IValue<Float> explosiveUpgradeDurabilityChance = F2.make("durabilityChance", .3f, //
      "Chance that employing the explosive upgrade to blow up extra blocks costs the pickaxe durability.").setRange(0, 1).sync();

  public static final IValue<Boolean> explosiveUpgradeUnlimitedTargets = F2.make("unlimitedTargets", false, //
      "Should the explosive upgrade blow up any kind of block the pickaxe can mine? If disabled, only a limited list of trash blocks will be blown up. "
          + "Enable this in modpacks that have a large number of modded stone or dirt in their worldgen.")
      .sync();

}
