package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ThaumaturgeRobesUpgrade extends AbstractUpgrade {

  public static final @Nonnull String UPGRADE_NAME = "thaumaturge_robes_";
  
  public static final @Nonnull ThaumaturgeRobesUpgrade CHEST = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.CHEST);
  public static final @Nonnull ThaumaturgeRobesUpgrade LEGS = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.LEGS);
  public static final @Nonnull ThaumaturgeRobesUpgrade BOOTS = new ThaumaturgeRobesUpgrade(EntityEquipmentSlot.FEET);

  private final @Nonnull EntityEquipmentSlot slot;

  public static @Nonnull ItemStack getRobeArmor(EntityEquipmentSlot slot) {
    Item i;
    switch (slot) {
    case FEET:
      i = Item.REGISTRY.getObject(new ResourceLocation("thaumcraft", "cloth_boots"));
      if (i != null) {
        return new ItemStack(i);
      }
    case LEGS:
      i = Item.REGISTRY.getObject(new ResourceLocation("thaumcraft", "cloth_legs"));
      if (i != null) {
        return new ItemStack(i);
      }
    case CHEST:
      i = Item.REGISTRY.getObject(new ResourceLocation("thaumcraft", "cloth_chest"));
      if (i != null) {
        return new ItemStack(i);
      }
    default:
      break;
    }
    return Prep.getEmpty();
  }

  public ThaumaturgeRobesUpgrade(@Nonnull EntityEquipmentSlot slot) {
    super(UPGRADE_NAME + slot.getName(), "enderio.darksteel.upgrade.thaumaturge_robes." + slot.getName(), getRobeArmor(slot),
        DarkSteelConfig.thaumaturgeRobesCost);
    this.slot = slot;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(slot) && item.hasUpgradeCallbacks(this) && Prep.isValid(getUpgradeItem()) && !hasUpgrade(stack, item);
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    return upgradeItem = getRobeArmor(slot);
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "(???)";
    }
    return super.getUpgradeItemName();
  }

}
