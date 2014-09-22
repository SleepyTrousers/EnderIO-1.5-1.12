package crazypants.enderio.item.darksteel;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class GogglesOfRevealingUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "gogglesRevealing";

  public static final GogglesOfRevealingUpgrade INSTANCE = new GogglesOfRevealingUpgrade();

  public static ItemStack getGoggles() {
    Item i = GameRegistry.findItem("Thaumcraft", "ItemGoggles");
    if(i != null) {
      return new ItemStack(i);
    }
    return null;
  }

  public static GogglesOfRevealingUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GogglesOfRevealingUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public GogglesOfRevealingUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public GogglesOfRevealingUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.gogglesOfRevealing",getGoggles(), Config.darkSteelGogglesOfRevealingCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != EnderIO.itemDarkSteelHelmet || getGoggles() == null) {
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

}
