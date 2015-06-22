package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.material.Material;
import crazypants.enderio.teleport.TravelController;

public class TravelUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "travel";
  
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
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.travel", new ItemStack(EnderIO.itemMaterial,1,Material.ENDER_CRYSTAL.ordinal()), Config.darkSteelTravelCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if (stack == null || !(stack.getItem() instanceof IShiftRightClickUpgradable) || !(stack.getItem() instanceof IItemOfTravel)
        || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)
        || ((IShiftRightClickUpgradable) stack.getItem()).hasShiftRightClickUpgrade(stack)) {
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

  private static long lastBlickTick = -1;

  public static boolean handleRightClick(ItemStack stack, World world, EntityPlayer player) {
    if (world.isRemote) {
      if (TravelController.instance.activateTravelAccessable(stack, world, player, TravelSource.STAFF)) {
        player.swingItem();
        return true;
      }
  
      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if (ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if (Config.travelStaffBlinkEnabled && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if (TravelController.instance.doBlink(stack, player)) {
          player.swingItem();
          lastBlickTick = EnderIO.proxy.getTickCount();
          return true;
        }
      }
    }
    return false;
  }

  public static boolean handleItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {
    return false;
  }

}
