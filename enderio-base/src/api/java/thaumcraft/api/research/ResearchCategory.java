package thaumcraft.api.research;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ResearchCategory {
	
	/** Is the smallest column used on the GUI. */
    public int minDisplayColumn;

    /** Is the smallest row used on the GUI. */
    public int minDisplayRow;

    /** Is the biggest column used on the GUI. */
    public int maxDisplayColumn;

    /** Is the biggest row used on the GUI. */
    public int maxDisplayRow;
    
    /** display variables **/
    public ResourceLocation icon;
    public ResourceLocation background;
    public ResourceLocation background2;
    
    public String researchKey;
    public String key;
    
    public AspectList formula;
	
	public ResearchCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background) {
		this.key = key;
		this.researchKey = researchkey;
		this.icon = icon;
		this.background = background;
		this.background2 = null;
		this.formula = formula;
	}
	
	public ResearchCategory(String key, String researchKey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
		this.key = key;
		this.researchKey = researchKey;
		this.icon = icon;
		this.background = background;
		this.background2 = background2;
		this.formula = formula;
	}
	
	/**
	 * For a given list of aspects this method will calculate the amount of raw knowledge you will be able to gain for the knowledge field.
	 * @param as
	 * @return
	 */
	public int applyFormula(AspectList as) {		
		return applyFormula(as,1);
	}
	
	/**
	 * This version of the method accepts a multiplier for the total - should usually not be needed by addon mods
	 * @param as
	 * @param mod multiplier to total
	 * @return
	 */
	public int applyFormula(AspectList as, double mod) {			
		if (formula==null) return 0;
		double total=0;
		for (Aspect aspect:formula.getAspects()) {
			total += (mod * mod) * as.getAmount(aspect) * (formula.getAmount(aspect) / 10d);
		}
		if (total>0) total = Math.sqrt(total); 
		return MathHelper.ceil( total );
	}

	//Research
	public Map<String, ResearchEntry> research = new HashMap<String,ResearchEntry>();	
	
}
