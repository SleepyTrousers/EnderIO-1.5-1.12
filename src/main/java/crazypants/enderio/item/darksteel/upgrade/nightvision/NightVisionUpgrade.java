package crazypants.enderio.item.darksteel.upgrade.nightvision;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;

public class NightVisionUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "nightVision";
  
  public static final NightVisionUpgrade INSTANCE = new NightVisionUpgrade();
  
  public static NightVisionUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new NightVisionUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  private static ItemStack createUpgradeItem() {
    ItemStack pot = new ItemStack(Items.POTIONITEM, 1, 0);
    PotionUtils.addPotionToItemStack(pot, PotionTypes.NIGHT_VISION);
    return pot;
  }
  
  public NightVisionUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public NightVisionUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.nightVision", createUpgradeItem(), Config.darkSteelNightVisionCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelHelmet) {
      return false;
    }
    NightVisionUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }
}
