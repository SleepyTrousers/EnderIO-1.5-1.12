package thaumcraft.api.research;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.research.ResearchStage.Knowledge;

public class ResearchEntry 
{
	

	/**
	 * A short string used as a key for this research. Must be unique
	 */
	String key;
	
	/**
	 * A short string used as a reference to the research category to which this must be added.
	 */
	String category;
	
	/**
	 * A text name of the research entry. Can be a localizable string.
	 */
	String name;
	
	/**
     * This links to any research that needs to be completed before this research can be discovered or learnt.
     */
    String[] parents;
        
    /**
     * any research linked to this that will be unlocked automatically when this research is complete
     */
    String[] siblings;
    
	
    /**
     * the horizontal position of the research icon
     */
    int displayColumn;

    /**
     * the vertical position of the research icon
     */
    int displayRow;
    
    /**
     * the icon to be used for this research 
     */
    Object[] icons;    

    /**
     * special meta-data tags that indicate how this research must be handled
     */
    EnumResearchMeta[] meta;
    
    /**
     * items the player will receive on completion of this research
     */
    ItemStack[] rewardItem;
    
    /**
     * knowledge the player will receive on completion of this research
     */
    Knowledge[] rewardKnow;
    
    
    
    public enum EnumResearchMeta {
    	ROUND,
    	SPIKY,//these also grant .5 bonus inspiration for theorycrafting
    	REVERSE,
    	HIDDEN,//these also grant .1 bonus inspiration for theorycrafting
    	AUTOUNLOCK,
    	HEX;
	}
    
    /**
     * The various stages present in this research entry
     */
    ResearchStage[] stages;
    
    /**
     * The various addena present in this research entry
     */
    ResearchAddendum[] addenda;
    
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the name
	 */
	public String getLocalizedName() {
		return I18n.translateToLocal(getName());
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parents
	 */
	public String[] getParents() {
		return parents;
	}
	
	/**
	 * @return return parents with ALL prefixes and postfixes stripped away
	 */
	public String[] getParentsClean() {
		String[] out = null;
		if (parents!=null) { 
			out = getParentsStripped();
			for (int q=0;q<out.length;q++) {
				if (out[q].contains("@")) 
					out[q] = out[q].substring(0,out[q].indexOf("@"));
			}
		}
		return out;
	}
	
	
	/**
	 * @return return parents with prefixes stripped away
	 */
	public String[] getParentsStripped() {
		String[] out = null;
		if (parents!=null) { 
			out = new String[parents.length];
			for (int q=0;q<out.length;q++) {
				out[q] = ""+parents[q];
				if (out[q].startsWith("~")) 
					out[q] = out[q].substring(1);
			}
		}
		return out;
	}

	/**
	 * @param parents the parents to set
	 */
	public void setParents(String[] parents) {
		this.parents = parents;
	}

	/**
	 * @return the siblings
	 */
	public String[] getSiblings() {
		return siblings;
	}

	/**
	 * @param siblings the siblings to set
	 */
	public void setSiblings(String[] siblings) {
		this.siblings = siblings;
	}

	/**
	 * @return the displayColumn
	 */
	public int getDisplayColumn() {
		return displayColumn;
	}

	/**
	 * @param displayColumn the displayColumn to set
	 */
	public void setDisplayColumn(int displayColumn) {
		this.displayColumn = displayColumn;
	}

	/**
	 * @return the displayRow
	 */
	public int getDisplayRow() {
		return displayRow;
	}

	/**
	 * @param displayRow the displayRow to set
	 */
	public void setDisplayRow(int displayRow) {
		this.displayRow = displayRow;
	}

	/**
	 * @return the icons
	 */
	public Object[] getIcons() {
		return icons;
	}

	/**
	 * @param icons the icons to set
	 */
	public void setIcons(Object[] icons) {
		this.icons = icons;
	}

	/**
	 * @return the meta
	 */
	public EnumResearchMeta[] getMeta() {
		return meta;
	}
	
	public boolean hasMeta(EnumResearchMeta me) {
		return meta==null ? false : Arrays.asList(meta).contains(me);
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(EnumResearchMeta[] meta) {
		this.meta = meta;
	}

	/**
	 * @return the stages
	 */
	public ResearchStage[] getStages() {
		return stages;
	}

	/**
	 * @param stages the stages to set
	 */
	public void setStages(ResearchStage[] stages) {
		this.stages = stages;
	}

	/**
	 * @return the rewardItem
	 */
	public ItemStack[] getRewardItem() {
		return rewardItem;
	}

	/**
	 * @param rewardItem the rewardItem to set
	 */
	public void setRewardItem(ItemStack[] rewardItem) {
		this.rewardItem = rewardItem;
	}

	/**
	 * @return the rewardKnow
	 */
	public Knowledge[] getRewardKnow() {
		return rewardKnow;
	}

	/**
	 * @param rewardKnow the rewardKnow to set
	 */
	public void setRewardKnow(Knowledge[] rewardKnow) {
		this.rewardKnow = rewardKnow;
	}

	/**
	 * @return the addenda
	 */
	public ResearchAddendum[] getAddenda() {
		return addenda;
	}

	/**
	 * @param addenda the addenda to set
	 */
	public void setAddenda(ResearchAddendum[] addenda) {
		this.addenda = addenda;
	}
    
    
	
}
