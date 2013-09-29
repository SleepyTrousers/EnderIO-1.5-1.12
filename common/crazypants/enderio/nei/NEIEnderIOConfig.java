package crazypants.enderio.nei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;

public class NEIEnderIOConfig implements IConfigureNEI {
	public static ItemStack fusedQuartzIngredients;
	
	//Cache item IDs
	public static int alloyItemID;
	public static int fusedQuartzBlockID;

	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new AlloySmelterRecipeHandler());
		API.setGuiOffset(GuiAlloySmelter.class, 50, 50);
		API.hideItem(EnderIO.blockConduitFacade.blockID);
		
		//Fused Quartz
		//Need to find a way to pull this from FusedQuartzRecipe.java
		fusedQuartzIngredients = new ItemStack(Item.netherQuartz,4);
		
		alloyItemID = EnderIO.itemAlloy.itemID;
		fusedQuartzBlockID = EnderIO.blockFusedQuartz.blockID;
	}

	@Override
	public String getName() {
		return "Ender IO NEI Plugin";
	}

	@Override
	public String getVersion() {
		return "0.0.1";
	}

}
