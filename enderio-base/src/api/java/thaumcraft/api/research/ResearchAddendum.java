package thaumcraft.api.research;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ResearchAddendum {
	String text;
	ResourceLocation[] recipes;
	String[] research;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	public String getTextLocalized() {
		return I18n.translateToLocal(getText());
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}	
	
	/**
	 * @return the recipes
	 */
	public ResourceLocation[] getRecipes() {
		return recipes;
	}
	/**
	 * @param recipes the recipes to set
	 */
	public void setRecipes(ResourceLocation[] recipes) {
		this.recipes = recipes;
	}
	
	/**
	 * @return the research
	 */
	public String[] getResearch() {
		return research;
	}
	/**
	 * @param research the research to set
	 */
	public void setResearch(String[] research) {
		this.research = research;
	}
	
}
