package crazypants.enderio.base.integration.thaumcraft;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.base.integration.thaumcraft.ThaumcraftUtil.MODID_THAUMCRAFT;

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
      i = Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_boots"));
      if (i != null) {
        return new ItemStack(i);
      }
    case LEGS:
      i = Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_legs"));
      if (i != null) {
        return new ItemStack(i);
      }
    case CHEST:
      i = Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_chest"));
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
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    switch (slot) {
    case FEET:
      return new NNList<>(Lang.DSU_CLASS_ARMOR_FEET::get);
    case LEGS:
      return new NNList<>(Lang.DSU_CLASS_ARMOR_LEGS::get);
    case CHEST:
      return new NNList<>(Lang.DSU_CLASS_ARMOR_CHEST::get);
    case HEAD:
      return new NNList<>(Lang.DSU_CLASS_ARMOR_HEAD::get);
    default:
      return new NNList<>(Lang.DSU_CLASS_ARMOR::get);
    }
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
