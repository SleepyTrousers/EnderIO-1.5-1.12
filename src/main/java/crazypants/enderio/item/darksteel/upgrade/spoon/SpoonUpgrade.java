package crazypants.enderio.item.darksteel.upgrade.spoon;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SpoonUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "spoon";
  
  public static final SpoonUpgrade INSTANCE = new SpoonUpgrade();
  
  public static SpoonUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SpoonUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public SpoonUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public SpoonUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.spoon", new ItemStack(Items.DIAMOND_SHOVEL), Config.darkSteelSpoonCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelPickaxe || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    SpoonUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }


}
