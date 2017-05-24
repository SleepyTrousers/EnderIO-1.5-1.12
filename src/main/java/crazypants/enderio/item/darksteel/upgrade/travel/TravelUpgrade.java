package crazypants.enderio.item.darksteel.upgrade.travel;

import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.material.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static crazypants.enderio.ModObject.itemMaterial;

public class TravelUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "travel";
  
  public static final TravelUpgrade INSTANCE = new TravelUpgrade();
  
  public static TravelUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.getTagCompound() == null) {
      return null;
    }
    if(!stack.getTagCompound().hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new TravelUpgrade((NBTTagCompound) stack.getTagCompound().getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public TravelUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public TravelUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.travel", new ItemStack(itemMaterial.getItem(), 1, Material.ENDER_CRYSTAL.ordinal()),
        Config.darkSteelTravelCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || (stack.getItem() != ModObject.itemDarkSteelSword && stack.getItem() != ModObject.itemDarkSteelPickaxe) || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
      return false;
    }
    TravelUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {    
  }

}
