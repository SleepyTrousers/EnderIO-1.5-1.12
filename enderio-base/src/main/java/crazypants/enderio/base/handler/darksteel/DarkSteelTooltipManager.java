package crazypants.enderio.base.handler.darksteel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class DarkSteelTooltipManager {

  public static void addCommonTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem) {
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addCommonEntries(itemstack, entityplayer, list, flag);
        }
      }
    }
  }

  public static void addBasicTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem) {
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addBasicEntries(itemstack, entityplayer, list, flag);
        }
      }
    }
  }

  public static void setSkipUpgradeTooltips(boolean skipUpgradeTooltips) {
    DarkSteelTooltipManager.skipUpgradeTooltips = skipUpgradeTooltips;
  }

  private static boolean skipUpgradeTooltips = false;

  public static void addAdvancedTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem && !skipUpgradeTooltips) {
      List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addDetailedEntries(itemstack, entityplayer, list, flag);
        } else if (upgrade.canAddToItem(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          applyableUpgrades.add(upgrade);
        }
      }
      if (!applyableUpgrades.isEmpty()) {
        list.add(TextFormatting.YELLOW + EnderIO.lang.localize("tooltip.anvilupgrades") + " ");
        for (IDarkSteelUpgrade up : applyableUpgrades) {
          list.add(Lang.DARK_STEEL_LEVELS2.get(TextFormatting.DARK_AQUA, TextFormatting.ITALIC, up.getDisplayName(), 1));
        }
      }
    }
  }

}
