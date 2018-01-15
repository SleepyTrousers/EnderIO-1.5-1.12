package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class DarkSteelConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "darksteel"));

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

  public static final IValue<Float> darkSteelSpeedWalkModifier1 = F.make("darkSteelSpeedWalkModifier1", 0.15f, //
      "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier1 = F.make("darkSteelSpeedWalkModifier1", 0.1f, //
      "Speed modifier applied when sprinting in the Dark Steel Boots with Speed I.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier2 = F.make("darkSteelSpeedWalkModifier1", 0.3f, //
      "Speed modifier applied when walking in the Dark Steel Boots with Speed II.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier2 = F.make("darkSteelSpeedWalkModifier1", 0.45f, //
      "Speed modifier applied when sprinting in the Dark Steel Boots with Speed II.").setRange(0, 32).sync();

  public static final IValue<Float> darkSteelSpeedWalkModifier3 = F.make("darkSteelSpeedWalkModifier1", 0.3f, //
      "Speed modifier applied when walking in the Dark Steel Boots with Speed III.").setRange(0, 32).sync();
  public static final IValue<Float> darkSteelSpeedSprintModifier3 = F.make("darkSteelSpeedWalkModifier1", 0.5f, //
      "Speed modifier applied when sprinting in the Dark Steel Boots with Speed III.").setRange(0, 32).sync();

}
