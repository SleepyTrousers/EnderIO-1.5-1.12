package crazypants.enderio.base.item.darksteel;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class DarkSteelAttributeModifier extends AttributeModifier {

  public static final @Nonnull UUID UU_ID = new UUID(12879874982l, 320981923);

  private static final @Nonnull AttributeModifier NONE = new AttributeModifier(UU_ID, "Empowered", 0, 0);

  private static final @Nonnull NNList<AttributeModifier> ATTACK_DAMAGE = new NNList<>(
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordDamageBonusEmpowered),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordDamageBonusEmpowered1),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordDamageBonusEmpowered2),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordDamageBonusEmpowered3));
  private static final @Nonnull NNList<AttributeModifier> ATTACK_SPEED = new NNList<>(
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered1),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered2),
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSwordSpeedBonusEmpowered3));
  private static final @Nonnull NNList<AttributeModifier> WALK_SPEED = new NNList<>(NONE,
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedWalkModifier1), //
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedWalkModifier2), //
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedWalkModifier3));
  private static final @Nonnull NNList<AttributeModifier> SPRINT_SPEED = new NNList<>(NONE,
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedSprintModifier1), //
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedSprintModifier2), //
      new DarkSteelAttributeModifier(DarkSteelConfig.darkSteelSpeedSprintModifier3));

  public static @Nonnull AttributeModifier getAttackDamage(int level) {
    return ATTACK_DAMAGE.get(level);
  }

  public static @Nonnull AttributeModifier getAttackSpeed(int level) {
    return ATTACK_SPEED.get(level);
  }

  public static @Nonnull AttributeModifier getWalkSpeed(int level) {
    return WALK_SPEED.get(level);
  }

  public static @Nonnull AttributeModifier getSprintSpeed(int level) {
    return SPRINT_SPEED.get(level);
  }

  private final IValue<Float> config;

  private DarkSteelAttributeModifier(IValue<Float> config) {
    super(UU_ID, "Empowered", 0, 0);
    this.config = config;
  }

  @Override
  public double getAmount() {
    return config.get();
  }

}
