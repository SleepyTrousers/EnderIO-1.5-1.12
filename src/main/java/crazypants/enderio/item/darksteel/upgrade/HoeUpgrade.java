package crazypants.enderio.item.darksteel.upgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.IDarkSteelItem;
import crazypants.enderio.material.Material;

public class HoeUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "hoe";

  public static final HoeUpgrade INSTANCE = new HoeUpgrade();

  public static HoeUpgrade loadFromItem(ItemStack stack) {
    if (stack == null) {
      return null;
    }
    if (stack.stackTagCompound == null) {
      return null;
    }
    if (!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new HoeUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public HoeUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public HoeUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade." + UPGRADE_NAME, new ItemStack(Items.diamond_hoe), Config.darkSteelHoeCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if (stack == null || !(stack.getItem() instanceof IRightClickUpgradable) || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)
        || ((IRightClickUpgradable) stack.getItem()).hasRightClickUpgrade(stack)) {
      return false;
    }
    HoeUpgrade up = loadFromItem(stack);
    if (up == null) {
      return true;
    }
    return false;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

  public static boolean handleItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {
    return Items.diamond_hoe.onItemUse(stack, player, world, x, y, z, side, par8, par9, par10);
  }

  public static boolean handleRightClick(ItemStack stack, World world, EntityPlayer player) {
    return false;
  }

}
