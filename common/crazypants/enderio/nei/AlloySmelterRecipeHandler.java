package crazypants.enderio.nei;

import static codechicken.core.gui.GuiDraw.drawStringC;
import static codechicken.core.gui.GuiDraw.changeTexture;
import static codechicken.core.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.forge.GuiContainerManager;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;
import crazypants.enderio.material.Alloy;

public class AlloySmelterRecipeHandler extends TemplateRecipeHandler {

	@Override
	public String getRecipeName() {
		return "Alloy Smelter";
	}

	@Override
	public String getGuiTexture() {
		return "enderio:textures/gui/alloySmelter.png";
	}

	public PositionedStack getResult() {
		return null;
	}
	
	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiAlloySmelter.class;
	}
	
	@Override
	public String getOverlayIdentifier()
	{
		return "EnderIOAlloySmelter";
	}
	
	@Override
	public void loadTransferRects()
	{
		//Set this up later to show all alloy recipes
		//transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(77, 7, 22, 12), "EnderIOAlloySmelter", new Object[0]));
	}
	
	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		//If this is an alloy
		if(result.itemID == NEIEnderIOConfig.alloyItemID) {
			//Get recipe based on meta
			int damage = result.getItemDamage();
			
			arecipes.add(new AlloySmelterRecipe(Alloy.values()[damage].ingrediants,result));
		} else if (result.itemID == NEIEnderIOConfig.fusedQuartzBlockID) {
			arecipes.add(new AlloySmelterRecipe(NEIEnderIOConfig.fusedQuartzIngredients,result));
		}
	}
	
	//Move the GUI down 8 pixels.
	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 5, 3, 166, 73);
	}

	@Override
	public void drawExtras(int recipe) {
		String energyString = "1 MEEELLION MJ!";
		drawProgressBar(98, 33, 176, 0, 22, 13, 48, 3);
		drawProgressBar(50, 33, 176, 0, 22, 13, 48, 3);
//		drawStringC(energyString, 82, 80, 0xFFFFFFFF);
	}
	
	
	
	
	
	public class AlloySmelterRecipe extends TemplateRecipeHandler.CachedRecipe {
		
		private ArrayList<PositionedStack> input;
		private PositionedStack output;
		private double energy;
		
		// Possible energy cost in the future?
		public double getEnergy() {
			return energy;
		}
		
		@Override
        public List<PositionedStack> getIngredients() {
			return input;
		}

		
		@Override
		public PositionedStack getResult() {
			return output;
		}
		
		public AlloySmelterRecipe(ItemStack ingredients[],ItemStack result)
		{
			int recipeSize = ingredients.length;
			this.input = new ArrayList<PositionedStack>();
			if(recipeSize>0) {
				this.input.add(new PositionedStack(ingredients[0], 49, 14));
			}
			if(recipeSize>1) {
				this.input.add(new PositionedStack(ingredients[1], 73, 4));
			}
			if(recipeSize>2) {
				this.input.add(new PositionedStack(ingredients[2], 98, 14));
			}
			this.output = new PositionedStack(result, 74, 54);
//			this.energy = something?; //If we wanted to do an energy cost
		}
		
		
		public AlloySmelterRecipe(ItemStack ingredient,ItemStack result)
		{
			this.input = new ArrayList<PositionedStack>();
			this.input.add(new PositionedStack(ingredient, 49, 14));
			this.output = new PositionedStack(result, 74, 54);
//			this.energy = something?;
		}
	} 
}
