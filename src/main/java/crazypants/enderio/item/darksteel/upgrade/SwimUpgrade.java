package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.endsteel.EndSteelItems;

public class SwimUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "swim";

  public static final SwimUpgrade INSTANCE = new SwimUpgrade();

  public static SwimUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SwimUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }


  public SwimUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", new ItemStack(Blocks.waterlily), Config.darkSteelSwimCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || (stack.getItem() != DarkSteelItems.itemDarkSteelBoots && stack.getItem() != EndSteelItems.itemEndSteelBoots)) {
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
