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

public class NaturalistEyeUpgrade extends AbstractUpgrade {

  public static final @Nonnull NaturalistEyeUpgrade INSTANCE = new NaturalistEyeUpgrade();

  private static final @Nonnull String UPGRADE_NAME = "naturalist_eye";

  protected NaturalistEyeUpgrade() {
    super(EnderIOIntegrationForestry.MODID, UPGRADE_NAME, "enderio.darksteel.upgrade.naturalist_eye", ForestryConfig.naturalistEyeCost);
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.HEAD),
        Rules.or(Rules.callbacksFor(this), Rules.staticCheck(item -> item instanceof ItemDarkSteelArmor)), Rules.itemTypeTooltip(EntityEquipmentSlot.HEAD));
  }

}
