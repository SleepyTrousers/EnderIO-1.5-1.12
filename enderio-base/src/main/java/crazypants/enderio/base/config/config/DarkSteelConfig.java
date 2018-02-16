package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class DarkSteelConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.sword"));

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered = F.make("darkSteelSwordDamageBonusEmpowered", 1f, //
      "The extra damage dealt when the sword is empowered I and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered = F.make("darkSteelSwordSpeedBonusEmpowered", 0.4f, //
      "The increase in attack speed when the sword is empowered I and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered1 = F.make("darkSteelSwordDamageBonusEmpowered1", 2f, //
      "The extra damage dealt when the sword is empowered II and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered1 = F.make("darkSteelSwordSpeedBonusEmpowered1", 0.45f, //
      "The increase in attack speed when the sword is empowered II and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered2 = F.make("darkSteelSwordDamageBonusEmpowered2", 3f, //
      "The extra damage dealt when the sword is empowered III and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered2 = F.make("darkSteelSwordSpeedBonusEmpowered2", 0.5f, //
      "The increase in attack speed when the sword is empowered III and has energy.").setRange(0, 2).sync();

  public static final IValue<Float> darkSteelSwordDamageBonusEmpowered3 = F.make("darkSteelSwordDamageBonusEmpowered3", 4f, //
      "The extra damage dealt when the sword is empowered IV and has energy.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSwordSpeedBonusEmpowered3 = F.make("darkSteelSwordSpeedBonusEmpowered3", 0.55f, //
      "The increase in attack speed when the sword is empowered IV and has energy.").setRange(0, 2).sync();

  public static final SectionedValueFactory F1 = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.upgrades.speed"));

  public static final IValue<Float> darkSteelSpeedWalkModifier1 = F1.make("darkSteelSpeedWalkModifier1", 0.15f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier1 = F1.make("darkSteelSpeedWalkModifier1", 0.10f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed I.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier2 = F1.make("darkSteelSpeedWalkModifier2", 0.30f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier2 = F1.make("darkSteelSpeedWalkModifier2", 0.30f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed II.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier3 = F1.make("darkSteelSpeedWalkModifier3", 0.45f, //
      "Speed modifier applied when walking in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier3 = F1.make("darkSteelSpeedWalkModifier3", 0.50f, //
      "Speed modifier applied when sprinting in the Dark Steel Leggings with Speed III.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedBonusEmpowered = F1.make("darkSteelSpeedBonusEmpowered", 1f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered1 = F1.make("darkSteelSpeedBonusEmpowered1", 1.10f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered2 = F1.make("darkSteelSpeedBonusEmpowered2", 1.50f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();
  public static final IValue<Float> darkSteelSpeedBonusEmpowered3 = F1.make("darkSteelSpeedBonusEmpowered3", 2.00f, //
      "The extra effectiveness of the speed upgrade when the Leggings are empowered I and have energy.").setRange(0, 2).sync();

  public static final SectionedValueFactory F2 = new SectionedValueFactory(BaseConfig.F, new Section("", "items.darksteel.upgrades"));

  public static final IValue<Integer> explosiveUpgradeCost = F2.make("explosiveUpgradeCost", 8, //
      "Cost for the explosive upgrade in levels.").setRange(1, 99).sync();

  public static final IValue<Integer> explosiveUpgradeEnergyPerBlock = F2.make("explosiveUpgradeEnergyPerBlock", 20, //
      "Extra energy the explosive upgrade uses to blow up blocks. This goes on top of the energy used to counteract durability loss.").setMin(0).sync();

  public static final IValue<Float> explosiveUpgradeDurabilityChance = F2.make("explosiveUpgradeDurabilityChance", .3f, //
      "Chance that employing the explosive upgrade to blow up extra blocks costs the pickaxe durability.").setRange(0, 1).sync();

  public static final IValue<Boolean> explosiveUpgradeUnlimitedTargets = F2.make("explosiveUpgradeUnlimitedTargets", false, //
      "Should the explosive upgrade blow up any kind of block the pickaxe can mine? If disabled, only a limited list of trash blocks will be blown up. "
          + "Enable this in modpacks that have a large number of modded stone or dirt in their worldgen.")
      .sync();

}
