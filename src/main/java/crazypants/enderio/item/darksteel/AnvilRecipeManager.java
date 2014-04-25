package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AnvilRecipeManager {

	public static AnvilRecipeManager instance= new AnvilRecipeManager();


	private List<IDarkSteelUpgrade> upgrades = new ArrayList<IDarkSteelUpgrade>();

	public AnvilRecipeManager( ) {
	  upgrades.add(EnergyUpgrade.VIBRANT);
	  upgrades.add(EnergyUpgrade.ENERGY_ONE);
	  upgrades.add(EnergyUpgrade.ENERGY_TWO);
	  upgrades.add(EnergyUpgrade.ENERGY_THREE);
	}

	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent evt) {
		if(evt.left == null || evt.right == null) {
		  return;
		}

		for(IDarkSteelUpgrade upgrade : upgrades) {
		  if(upgrade.isUpgradeItem(evt.right) && upgrade.canAddToItem(evt.left)) {
		    ItemStack res = new ItemStack(evt.left.getItem(), 1, evt.left.getItemDamage());
		    upgrade.writeToItem(res);
		    evt.output  = res;
		    evt.cost = upgrade.getLevelCost();
		    return;
		  }
		}

	}

  public List<IDarkSteelUpgrade> getUpgrades() {
    return upgrades;
  }

  public void addCommonTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    for(IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addBasicTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    for(IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addAdvancedTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    for(IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addAdvancedEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

}
