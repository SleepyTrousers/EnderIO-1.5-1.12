package crazypants.enderio.item.darksteel.upgrade.flippers;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SwimUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "swim";
  
  public static final SwimUpgrade INSTANCE = new SwimUpgrade();
  
  public static SwimUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SwimUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public SwimUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", new ItemStack(Blocks.WATERLILY), Config.darkSteelSwimCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != ModObject.itemDarkSteelBoots) {
      return false;
    }
    SwimUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }


}
