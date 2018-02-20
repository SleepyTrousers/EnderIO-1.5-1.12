package crazypants.enderio.integration.forestry.upgrades;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.ForestryItemStacks;
import crazypants.enderio.integration.forestry.config.ForestryConfig;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ApiaristArmorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "apiarist_armor_";

  public static final @Nonnull ApiaristArmorUpgrade HELMET = new ApiaristArmorUpgrade(EntityEquipmentSlot.HEAD);
  public static final @Nonnull ApiaristArmorUpgrade CHEST = new ApiaristArmorUpgrade(EntityEquipmentSlot.CHEST);
  public static final @Nonnull ApiaristArmorUpgrade LEGS = new ApiaristArmorUpgrade(EntityEquipmentSlot.LEGS);
  public static final @Nonnull ApiaristArmorUpgrade BOOTS = new ApiaristArmorUpgrade(EntityEquipmentSlot.FEET);

  public static @Nonnull ItemStack getApiaristArmor(EntityEquipmentSlot slot) {
    switch (slot) {
    case FEET:
      return NullHelper.first(ForestryItemStacks.FORESTRY_FEET, Prep.getEmpty());
    case LEGS:
      return NullHelper.first(ForestryItemStacks.FORESTRY_LEGS, Prep.getEmpty());
    case CHEST:
      return NullHelper.first(ForestryItemStacks.FORESTRY_CHEST, Prep.getEmpty());
    case HEAD:
      return NullHelper.first(ForestryItemStacks.FORESTRY_HEAD, Prep.getEmpty());
    default:
      break;
    }
    return Prep.getEmpty();
  }

  private final EntityEquipmentSlot slot;

  public ApiaristArmorUpgrade(@Nonnull EntityEquipmentSlot slot) {
    super(EnderIOIntegrationForestry.MODID, UPGRADE_NAME + slot.getName(), "enderio.darksteel.upgrade.apiarist_armor." + slot.getName(), getApiaristArmor(slot),
        ForestryConfig.apiaristArmorCost);
    this.slot = slot;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    return (stack.getItem() instanceof ItemArmor) && ((ItemArmor) stack.getItem()).armorType == slot && Prep.isValid(getUpgradeItem()) && !hasUpgrade(stack);
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return upgradeItem = getApiaristArmor(slot);
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "(???)";
    }
    return super.getUpgradeItemName();
  }

}
