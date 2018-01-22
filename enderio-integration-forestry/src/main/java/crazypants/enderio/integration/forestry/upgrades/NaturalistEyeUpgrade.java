package crazypants.enderio.integration.forestry.upgrades;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.ForestryItemStacks;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public class NaturalistEyeUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "naturalist_eye";

  public static @Nonnull ItemStack getNaturalistEye() {
    return ForestryItemStacks.FORESTRY_HELMET != null ? ForestryItemStacks.FORESTRY_HELMET : Prep.getEmpty();
  }

  public NaturalistEyeUpgrade() {
    super(EnderIOIntegrationForestry.MODID, UPGRADE_NAME, "enderio.darksteel.upgrade.naturalist_eye", getNaturalistEye(), Config.darkSteelApiaristArmorCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return stack.getItem() == ModObject.itemDarkSteelHelmet.getItem() && Prep.isValid(getUpgradeItem()) && !hasUpgrade(stack);
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return upgradeItem = getNaturalistEye();
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "(???)";
    }
    return super.getUpgradeItemName();
  }

}
