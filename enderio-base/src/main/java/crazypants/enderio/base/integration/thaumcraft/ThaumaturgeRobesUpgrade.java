package crazypants.enderio.base.integration.thaumcraft;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ThaumaturgeRobesUpgrade extends AbstractUpgrade {

  public static final @Nonnull String UPGRADE_NAME = "thaumaturge_robes_";

  public static final @Nonnull ThaumaturgeRobesUpgrade CHEST = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.CHEST);
  public static final @Nonnull ThaumaturgeRobesUpgrade LEGS = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.LEGS);
  public static final @Nonnull ThaumaturgeRobesUpgrade BOOTS = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.FEET);

  private final @Nonnull EntityEquipmentSlot slot;

  public ThaumaturgeRobesUpgrade(@Nonnull EntityEquipmentSlot slot) {
    super(UPGRADE_NAME + slot.getName(), "enderio.darksteel.upgrade.thaumaturge_robes." + slot.getName(), DarkSteelConfig.thaumaturgeRobesCost);
    this.slot = slot;
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(slot), Rules.callbacksFor(this), Rules.itemTypeTooltip(slot));
  }

}
