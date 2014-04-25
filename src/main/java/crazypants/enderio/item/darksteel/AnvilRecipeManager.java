package crazypants.enderio.item.darksteel;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;

public class AnvilRecipeManager {

	public static AnvilRecipeManager instance= new AnvilRecipeManager();


	public AnvilRecipeManager( ) {

	}

	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent evt) {
		if(evt.left == null || evt.right == null) {
		  return;
		}
		if(evt.left.getItem() == EnderIO.itemDarkSteelHelmet && evt.right.getItem() == Items.apple) {
		  evt.output = new ItemStack(Items.boat);
		  evt.cost = 30;
		}
	}

}
