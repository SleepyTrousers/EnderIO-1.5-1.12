package thaumcraft.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe implements IThaumcraftRecipe  {

	private ItemStack recipeOutput;	
	private Ingredient catalyst;
	private AspectList aspects;
	private String research;
	private String name;
	public int hash;
	
	
	public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
		recipeOutput = result;
		this.name="";
		this.setAspects(tags);
		this.research = researchKey;
		this.setCatalyst(ThaumcraftApiHelper.getIngredient(catalyst));
		
		if (this.getCatalyst() == null)
        {
            throw new RuntimeException("Invalid crucible recipe catalyst: "+ catalyst);
        }
		
		generateHash();
	}
		
	private void generateHash() {
		String hc = research;		
		hc += recipeOutput.toString();
		if (recipeOutput.hasTagCompound()) {
			hc += recipeOutput.getTagCompound().toString();
		}	
		for (ItemStack is:getCatalyst().getMatchingStacks()) {
			hc += is.toString();
			if (is.hasTagCompound()) {
				hc += is.getTagCompound().toString();
			}			
		}
		hash = hc.hashCode();
	}

	public boolean matches(AspectList itags, ItemStack cat) {	
		if (!getCatalyst().apply(cat)) return false;		
		if (itags==null) return false;
		for (Aspect tag:getAspects().getAspects()) {
			if (itags.getAmount(tag)<getAspects().getAmount(tag)) return false;
		}
		return true;
	}
	
	public boolean catalystMatches(ItemStack cat) {
		return getCatalyst().apply(cat);
	}
	
	public AspectList removeMatching(AspectList itags) {
		AspectList temptags = new AspectList();
		temptags.aspects.putAll(itags.aspects);		
		for (Aspect tag:getAspects().getAspects()) {
			temptags.remove(tag, getAspects().getAmount(tag));
		}		
		itags = temptags;
		return itags;
	}
	
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public String getResearch() {
		return research;
	}

	public Ingredient getCatalyst() {
		return catalyst;
	}

	public void setCatalyst(Ingredient catalyst) {
		this.catalyst = catalyst;
	}

	public AspectList getAspects() {
		return aspects;
	}

	public void setAspects(AspectList aspects) {
		this.aspects = aspects;
	}

	private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public CrucibleRecipe setGroup(ResourceLocation s) {
		this.group=s.toString();
		return this;
	}
}
