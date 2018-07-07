package crazypants.enderio.base.item.darksteel.attributes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class DarkSteelAttributeModifiers {

  private static final @Nonnull String UU_ATTACK = "Ender IO Empowered Attack Bonus";
  private static final @Nonnull String UU_SPEED = "Ender IO Speed Bonus";

  private static final @Nonnull NNList<AttributeModifier> ATTACK_DAMAGE = new NNList<>(
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordDamageBonusEmpowered, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordDamageBonusEmpowered1, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordDamageBonusEmpowered2, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordDamageBonusEmpowered3, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordDamageBonusEmpowered4, Operation.ADD));
  private static final @Nonnull NNList<AttributeModifier> ATTACK_SPEED = new NNList<>(
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered1, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered2, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered3, Operation.ADD),
      new DarkSteelAttributeModifier(UU_ATTACK, DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered4, Operation.ADD));

  private static final @Nonnull NNList<NNList<AttributeModifier>> WALK_SPEED = new NNList<>( //
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered1),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered1),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered1)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered2)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered3),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered3),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered3)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered4),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered4),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedWalkModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered4)));

  private static final @Nonnull NNList<NNList<AttributeModifier>> SPRINT_SPEED = new NNList<>( //
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered1),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered1),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered1)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered2)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered2),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered2)),
      new NNList<>( //
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier1, DarkSteelConfig.darkSteelSpeedBonusEmpowered3),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier2, DarkSteelConfig.darkSteelSpeedBonusEmpowered3),
          new DoubleSteelAttributeModifier(UU_SPEED, DarkSteelConfig.darkSteelSpeedSprintModifier3, DarkSteelConfig.darkSteelSpeedBonusEmpowered3)));

  public static @Nonnull AttributeModifier getAttackDamage(int level) {
    return ATTACK_DAMAGE.get(level);
  }

  public static @Nonnull AttributeModifier getAttackSpeed(int level) {
    return ATTACK_SPEED.get(level);
  }

  public static @Nonnull AttributeModifier getWalkSpeed(boolean sprint, int level, int energyLevel) {
    return (sprint ? SPRINT_SPEED : WALK_SPEED).get(energyLevel).get(level - 1);
  }

}
