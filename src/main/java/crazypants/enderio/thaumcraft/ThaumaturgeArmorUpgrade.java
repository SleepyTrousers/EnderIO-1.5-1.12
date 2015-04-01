package crazypants.enderio.thaumcraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;

public class ThaumaturgeArmorUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "thaumaturgeArmor";

  private static final String thaumcraftItemNames[] = {
      "", "ItemChestplateRobe", "ItemLeggingsRobe", "ItemBootsRobe" };

  public static final ThaumaturgeArmorUpgrade CHEST = new ThaumaturgeArmorUpgrade(1);
  public static final ThaumaturgeArmorUpgrade LEGS = new ThaumaturgeArmorUpgrade(2);
  public static final ThaumaturgeArmorUpgrade BOOTS = new ThaumaturgeArmorUpgrade(3);

  public static ItemStack getThaumaturgeArmor(int slot) {
    Item i = GameRegistry.findItem("Thaumcraft", thaumcraftItemNames[slot]);
    if(i != null) {
      return new ItemStack(i);
    }
    return null;
  }

  public static ThaumaturgeArmorUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new ThaumaturgeArmorUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private final int slot;

  public ThaumaturgeArmorUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    this.slot = tag.getInteger("slot");
  }

  public ThaumaturgeArmorUpgrade(int slot) {
    super(UPGRADE_NAME,
        getThaumaturgeArmor(slot).getUnlocalizedName(),
        getThaumaturgeArmor(slot), Config.darkSteelThaumaturgeArmorCost);
    this.slot = slot;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ItemDarkSteelArmor.forArmorType(slot) || getUpgradeItem() == null) {
      return false;
    }
    ThaumaturgeArmorUpgrade up = loadFromItem(stack);
    return up == null;
  }

  @Override
  public boolean hasUpgrade(ItemStack stack) {
    return super.hasUpgrade(stack) && stack.getItem() == ItemDarkSteelArmor.forArmorType(slot);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte("slot", (byte) slot);
  }

  @Override
  public ItemStack getUpgradeItem() {
    if(upgradeItem != null) {
      return upgradeItem;
    }
    upgradeItem = getThaumaturgeArmor(slot);
    return upgradeItem;
  }

  @Override
  public String getUpgradeItemName() {
    if(getUpgradeItem() == null) {
      return "Thaumturge's Armor";
    }
    return super.getUpgradeItemName();
  }
}
