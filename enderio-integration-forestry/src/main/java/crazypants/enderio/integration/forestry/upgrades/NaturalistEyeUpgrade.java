package crazypants.enderio.integration.forestry.upgrades;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.ForestryItemStacks;
import crazypants.enderio.integration.forestry.config.ForestryConfig;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class NaturalistEyeUpgrade extends AbstractUpgrade {

  public static final @Nonnull NaturalistEyeUpgrade INSTANCE = new NaturalistEyeUpgrade();

  private static final @Nonnull String UPGRADE_NAME = "naturalist_eye";

  public static @Nonnull ItemStack getNaturalistEye() {
    return ForestryItemStacks.FORESTRY_HELMET != null ? ForestryItemStacks.FORESTRY_HELMET : Prep.getEmpty();
  }

  protected NaturalistEyeUpgrade() {
    super(EnderIOIntegrationForestry.MODID, UPGRADE_NAME, "enderio.darksteel.upgrade.naturalist_eye", getNaturalistEye(), ForestryConfig.naturalistEyeCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.HEAD) && (item instanceof ItemDarkSteelArmor || item.hasUpgradeCallbacks(this)) && Prep.isValid(getUpgradeItem())
        && !hasUpgrade(stack, item);
  }

  @Override
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    return new NNList<>(Lang.DSU_CLASS_ARMOR_HEAD::get);
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
