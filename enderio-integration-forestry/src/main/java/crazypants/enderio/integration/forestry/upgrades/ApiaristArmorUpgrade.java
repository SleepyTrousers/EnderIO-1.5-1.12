package crazypants.enderio.integration.forestry.upgrades;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.config.ForestryConfig;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ApiaristArmorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "apiarist_armor_";

  public static final @Nonnull ApiaristArmorUpgrade HELMET = new ApiaristArmorUpgrade(EntityEquipmentSlot.HEAD);
  public static final @Nonnull ApiaristArmorUpgrade CHEST = new ApiaristArmorUpgrade(EntityEquipmentSlot.CHEST);
  public static final @Nonnull ApiaristArmorUpgrade LEGS = new ApiaristArmorUpgrade(EntityEquipmentSlot.LEGS);
  public static final @Nonnull ApiaristArmorUpgrade BOOTS = new ApiaristArmorUpgrade(EntityEquipmentSlot.FEET);

  private final @Nonnull EntityEquipmentSlot slot;

  protected ApiaristArmorUpgrade(@Nonnull EntityEquipmentSlot slot) {
    super(EnderIOIntegrationForestry.MODID, UPGRADE_NAME + slot.getName(), "enderio.darksteel.upgrade.apiarist_armor." + slot.getName(),
        ForestryConfig.apiaristArmorCost);
    this.slot = slot;
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(slot), Rules.or(Rules.callbacksFor(this), Rules.staticCheck(item -> item instanceof ItemDarkSteelArmor)),
        Rules.itemTypeTooltip(slot));
  }

}
