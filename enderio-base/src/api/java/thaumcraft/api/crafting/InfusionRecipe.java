package thaumcraft.api.crafting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class InfusionRecipe implements IThaumcraftRecipe
{
	public AspectList aspects;
	public String research;
	private String name;
	protected NonNullList<Ingredient> components = NonNullList.create();
	public Ingredient sourceInput; //Use Ingredient.EMPTY of the source item can be anything
	public Object recipeOutput;
	public int instability;
	
	public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object ... recipe) {
		this.name="";
		this.research = research;
		this.recipeOutput = outputResult;
		this.aspects = aspects2;
		this.instability = inst;		
		this.sourceInput = ThaumcraftApiHelper.getIngredient(centralItem);
		if (sourceInput==null) {
			String ret = "Invalid infusion central item: "+centralItem;
            throw new RuntimeException(ret);
		}		
		for (Object in : recipe)
        {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
            	components.add(ing);
            } else {
                String ret = "Invalid infusion recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += outputResult;
                throw new RuntimeException(ret);
            }
        }
	}

	/**
     * Used to check if a recipe matches current crafting inventory
     * @param player 
     */
	public boolean matches(List<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
		if (getRecipeInput()==null) return false;			
		if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
    		return false;
    	}		
		return (getRecipeInput()==Ingredient.EMPTY || this.getRecipeInput().apply(central)) && RecipeMatcher.findMatches(input, getComponents()) != null;
    }
    
	@Override
    public String getResearch() {
		return research;
    }
    
	public Ingredient getRecipeInput() {
		return sourceInput;
	}

	public NonNullList<Ingredient> getComponents() {
		return components;
	}
	
	public Object getRecipeOutput() {
		return recipeOutput;
	}
	
	public AspectList getAspects() {
		return aspects;
	}			
	
	public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps ) {
		return recipeOutput;
    }
    
    public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
		return aspects;
    }
    
    public int getInstability(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
		return instability;
    }
    
    private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public InfusionRecipe setGroup(ResourceLocation s) {
		this.group=s.toString();
		return this;
	}
}
