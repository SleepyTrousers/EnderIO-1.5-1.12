package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.util.Lang;

public class DarkSteelRecipeManager {

  public static DarkSteelRecipeManager instance = new DarkSteelRecipeManager();

  private List<IDarkSteelUpgrade> upgrades = new ArrayList<IDarkSteelUpgrade>();

  public DarkSteelRecipeManager() {
    upgrades.add(EnergyUpgrade.EMPOWERED);
    upgrades.add(EnergyUpgrade.EMPOWERED_TWO);
    upgrades.add(EnergyUpgrade.EMPOWERED_THREE);
    upgrades.add(EnergyUpgrade.EMPOWERED_FOUR);
    upgrades.add(JumpUpgrade.JUMP_ONE);
    upgrades.add(JumpUpgrade.JUMP_TWO);
    upgrades.add(JumpUpgrade.JUMP_THREE);
    upgrades.add(SpeedUpgrade.SPEED_ONE);
    upgrades.add(SpeedUpgrade.SPEED_TWO);
    upgrades.add(SpeedUpgrade.SPEED_THREE);
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if(evt.left == null || evt.right == null) {
      return;
    }

    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.isUpgradeItem(evt.right) && upgrade.canAddToItem(evt.left)) {
        ItemStack res = new ItemStack(evt.left.getItem(), 1, evt.left.getItemDamage());
        if(evt.left.stackTagCompound != null) {
          res.stackTagCompound = (NBTTagCompound)evt.left.stackTagCompound.copy();
        }
        upgrade.writeToItem(res);
        evt.output = res;
        evt.cost = upgrade.getLevelCost();
        return;
      }
    }

  }

  public List<IDarkSteelUpgrade> getUpgrades() {
    return upgrades;
  }

  public void addCommonTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addBasicTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addAdvancedTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

    List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addDetailedEntries(itemstack, entityplayer, list, flag);
      } else if(upgrade.canAddToItem(itemstack)) {
        applyableUpgrades.add(upgrade);
      }
    }
    if(!applyableUpgrades.isEmpty()) {
      list.add(EnumChatFormatting.YELLOW + "Anvil Upgrades: ");
      for (IDarkSteelUpgrade up : applyableUpgrades) {
        list.add(EnumChatFormatting.DARK_AQUA + "" +  "" + Lang.localize(up.getUnlocalizedName() + ".name", false) + ": ");
        list.add(EnumChatFormatting.DARK_AQUA + "" + EnumChatFormatting.ITALIC + "  " + up.getUpgradeItem().getDisplayName() + " + " + up.getLevelCost() + " lvs");
      }
    }
  }

}
