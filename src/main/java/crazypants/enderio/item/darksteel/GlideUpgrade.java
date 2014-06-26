package crazypants.enderio.item.darksteel;

import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GlideUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "glide";
  
  public static final GlideUpgrade INSTANCE = new GlideUpgrade();
  
  public static GlideUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GlideUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public GlideUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public GlideUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.glide", new ItemStack(Items.feather), 15);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != EnderIO.itemDarkSteelChestplate|| !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    GlideUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }

}
