package crazypants.enderio.item.darksteel.upgrade;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.stream.Stream;

public class TravelUpgrade extends AbstractUpgrade {

  private static final String UPGRADE_NAME = "travel";

  public static final TravelUpgrade INSTANCE = new TravelUpgrade();

  public static TravelUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new TravelUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }


  public TravelUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public TravelUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.travel", new ItemStack(EnderIO.itemMaterial, 1, Material.ENDER_CRYSTAL.ordinal()), Config.darkSteelTravelCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null) return false;
    if(Stream.of(
      DarkSteelItems.itemDarkSteelSword, DarkSteelItems.itemEndSteelSword, DarkSteelItems.itemStellarSword,
      DarkSteelItems.itemDarkSteelPickaxe, DarkSteelItems.itemEndSteelPickaxe, DarkSteelItems.itemStellarPickaxe,
      DarkSteelItems.itemEndSteelAxe, DarkSteelItems.itemStellarAxe
    ).anyMatch(item -> stack.getItem() == item)) {
      return EnergyUpgrade.itemHasAnyPowerUpgrade(stack) && loadFromItem(stack) == null;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

}
