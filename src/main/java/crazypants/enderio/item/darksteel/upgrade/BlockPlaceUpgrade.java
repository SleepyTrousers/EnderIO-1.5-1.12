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

public class BlockPlaceUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "blockplace";

  public static final BlockPlaceUpgrade INSTANCE = new BlockPlaceUpgrade();

  public static BlockPlaceUpgrade loadFromItem(ItemStack stack) {
    if (stack == null) {
      return null;
    }
    if (stack.stackTagCompound == null) {
      return null;
    }
    if (!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new BlockPlaceUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  public BlockPlaceUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
  }

  public BlockPlaceUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade." + UPGRADE_NAME, new ItemStack(Blocks.stone), Config.darkSteelBlockPlaceCost);
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
    if (stack == null || !(stack.getItem() instanceof IRightClickUpgradable) || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)
        || ((IRightClickUpgradable) stack.getItem()).hasRightClickUpgrade(stack)) {
      return false;
    }
    BlockPlaceUpgrade up = loadFromItem(stack);
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
    if (world.isRemote) {
      int current = player.inventory.currentItem;
      int slot = current == 0 && Config.slotZeroPlacesEight ? 8 : current == 8 ? 0 : current + 1;
      if (player.inventory.mainInventory[slot] != null
          && !(player.inventory.mainInventory[slot].getItem() instanceof IDarkSteelItem)) {
        /*
         * this will not work with buckets unless we don't switch back to the
         * current item (the pick); there's probably some client <-> server
         * event thing going on with buckets, so our item-switch within the same
         * tick would be a problem.
         */
        player.inventory.currentItem = slot;
        Minecraft mc = Minecraft.getMinecraft();
        boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, player.inventory.mainInventory[slot],
            mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ, mc.objectMouseOver.sideHit,
            mc.objectMouseOver.hitVec);
        player.inventory.currentItem = current;
        return (result);
      }
    }
    return false;
  }

  public static boolean handleRightClick(ItemStack stack, World world, EntityPlayer player) {
    return false;
  }

}
