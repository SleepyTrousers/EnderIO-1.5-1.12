package crazypants.enderio.integration.thaumcraft;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GogglesOfRevealingUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "gogglesRevealing";

  public static final GogglesOfRevealingUpgrade INSTANCE = new GogglesOfRevealingUpgrade();

  public static ItemStack getGoggles() {
    Item i = Item.REGISTRY.getObject(new ResourceLocation("Thaumcraft", "ItemGoggles"));    
    if(i != null) {
      return new ItemStack(i);
    }
    return null;
  }

  public static GogglesOfRevealingUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GogglesOfRevealingUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  public static boolean isUpgradeEquipped(EntityPlayer player) {
    ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);    
    return GogglesOfRevealingUpgrade.loadFromItem(helmet) != null;  
  }

  public GogglesOfRevealingUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public GogglesOfRevealingUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.gogglesOfRevealing",getGoggles(), Config.darkSteelGogglesOfRevealingCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelHelmet || getGoggles() == null) {
      return false;
    }
    GogglesOfRevealingUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

  @Override
  public ItemStack getUpgradeItem() {
    if(upgradeItem != null) {
      return upgradeItem;
    }
    upgradeItem = getGoggles();
    return upgradeItem;
  }

  @Override
  public String getUpgradeItemName() {
    if(getUpgradeItem() == null) {
      return "Goggles of Revealing";
    }
    return super.getUpgradeItemName();
  }

}
