package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.util.Prep;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApiaristArmorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "apiaristArmor";

  private static final @Nonnull NNList<String> forestryItemNames = new NNList<>("apiaristBoots", "apiaristLegs", "apiaristChest", "apiaristHelmet");

  public static final @Nonnull ApiaristArmorUpgrade HELMET = new ApiaristArmorUpgrade(EntityEquipmentSlot.HEAD);
  public static final @Nonnull ApiaristArmorUpgrade CHEST = new ApiaristArmorUpgrade(EntityEquipmentSlot.CHEST);
  public static final @Nonnull ApiaristArmorUpgrade LEGS = new ApiaristArmorUpgrade(EntityEquipmentSlot.LEGS);
  public static final @Nonnull ApiaristArmorUpgrade BOOTS = new ApiaristArmorUpgrade(EntityEquipmentSlot.FEET);

  public static @Nonnull ItemStack getApiaristArmor(EntityEquipmentSlot slot) {
    Item i = Item.REGISTRY.getObject(new ResourceLocation("Forestry", forestryItemNames.get(slot.getIndex())));
    if (i != null) {
      return new ItemStack(i);
    }
    return Prep.getEmpty();
  }

  public static ApiaristArmorUpgrade loadFromItem(ItemStack stack) {
    final NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound == null) {
      return null;
    }
    if (!tagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new ApiaristArmorUpgrade((NBTTagCompound) tagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private final EntityEquipmentSlot slot;

  public ApiaristArmorUpgrade(@Nonnull NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.slot = EntityEquipmentSlot.fromString(tag.getString("slot"));
  }

  public ApiaristArmorUpgrade(@Nonnull EntityEquipmentSlot slot) {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.apiaristArmor." + slot.getName(), getApiaristArmor(slot), Config.darkSteelApiaristArmorCost);
    this.slot = slot;
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack) {
    if (!(stack.getItem() instanceof ItemArmor) || ((ItemArmor) stack.getItem()).armorType != slot || Prep.isInvalid(getUpgradeItem())) {
      return false;
    }
    ApiaristArmorUpgrade up = loadFromItem(stack);
    return up == null;
  }

  @Override
  public boolean hasUpgrade(@Nonnull ItemStack stack) {
    return super.hasUpgrade(stack) && (stack.getItem() instanceof ItemArmor) && ((ItemArmor) stack.getItem()).armorType == slot;
  }

  @Override
  public void writeUpgradeToNBT(@Nonnull NBTTagCompound upgradeRoot) {
    upgradeRoot.setString("slot", slot.getName());
  }

  @Override
  public @Nonnull ItemStack getUpgradeItem() {
    if (Prep.isValid(upgradeItem)) {
      return upgradeItem;
    }
    upgradeItem = getApiaristArmor(slot);
    return upgradeItem;
  }

  @Override
  public @Nonnull String getUpgradeItemName() {
    if (Prep.isInvalid(getUpgradeItem())) {
      return "Apiarist Armor";
    }
    return super.getUpgradeItemName();
  }

}
