package thaumcraft.api.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;

public class ResearchStage {
	String text;
	ResourceLocation[] recipes;
	ItemStack[] obtain;
	ItemStack[] craft;
	int[] craftReference;
	Knowledge[] know;
	String[] research;
	int warp;
	
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
	 * @return the obtain
	 */
	public ItemStack[] getObtain() {
		return obtain;
	}
	/**
	 * @param obtain the obtain to set
	 */
	public void setObtain(ItemStack[] obtain) {
		this.obtain = obtain;
	}
	/**
	 * @return the craft
	 */
	public ItemStack[] getCraft() {
		return craft;
	}
	/**
	 * @param craft the craft to set
	 */
	public void setCraft(ItemStack[] craft) {
		this.craft = craft;
	}	
	/**
	 * @return the craftReference
	 */
	public int[] getCraftReference() {
		return craftReference;
	}
	/**
	 * @param craftReference the craftReference to set
	 */
	public void setCraftReference(int[] craftReference) {
		this.craftReference = craftReference;
	}
	/**
	 * @return the know
	 */
	public Knowledge[] getKnow() {
		return know;
	}
	/**
	 * @param know the know to set
	 */
	public void setKnow(Knowledge[] know) {
		this.know = know;
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
	/**
	 * @return the warp
	 */
	public int getWarp() {
		return warp;
	}
	/**
	 * @param warp the warp to set
	 */
	public void setWarp(int warp) {
		this.warp = warp;
	}
		
	public static class Knowledge {
		public EnumKnowledgeType type;
    	public ResearchCategory category; 
    	public int amount = 0;
    	
    	public Knowledge(EnumKnowledgeType type, ResearchCategory category, int num) {
			super();
			this.type = type;
			this.category = category;
			this.amount = num;
		}

		public static Knowledge parse(String text) {
    		String[] s = text.split(";");
    		if (s.length==2) {
    			int num = 0;
    			try {
    				num = Integer.parseInt(s[1]);
    			} catch (Exception e) {}    			
    			EnumKnowledgeType t = EnumKnowledgeType.valueOf(s[0].toUpperCase());
    			if (t!=null && !t.hasFields() && num>0) {
    				return new Knowledge(t, null, num);
    			}
    		} else if (s.length==3) {
    			int num = 0;
    			try {
    				num = Integer.parseInt(s[2]);
    			} catch (Exception e) {}    			
    			EnumKnowledgeType t = EnumKnowledgeType.valueOf(s[0].toUpperCase());
    			ResearchCategory f = ResearchCategories.getResearchCategory(s[1].toUpperCase());
    			if (t!=null && f!=null && num>0) {
    				return new Knowledge(t,f,num);
    			}
    		}
    		return null;
    	}
    }
	
}
