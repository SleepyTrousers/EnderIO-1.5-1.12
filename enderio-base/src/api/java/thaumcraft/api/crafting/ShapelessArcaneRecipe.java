package thaumcraft.api.crafting;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class ShapelessArcaneRecipe extends ShapelessOreRecipe implements IArcaneRecipe
{
	private String research;
	private int vis;
	private AspectList crystals;	
	
	public ShapelessArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, Block result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapelessArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, Item  result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapelessArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
    	super(group, input, result);
    	this.research = res;
		this.vis = vis;
		this.crystals = crystals;
    }
	public ShapelessArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, ItemStack result, Object[] recipe) {
		super(group, result, recipe);
		this.research = res;
		this.vis = vis;
		this.crystals = crystals;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		if (!(var1 instanceof IArcaneWorkbench)) return ItemStack.EMPTY; 
		return super.getCraftingResult(var1);
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		
		InventoryCrafting dummy = new InventoryCrafting(new ContainerDummy(),3,3);
		for (int a=0;a<9;a++) dummy.setInventorySlotContents(a, inv.getStackInSlot(a));
		
		if (crystals!=null)
		for (Aspect aspect:crystals.getAspects()) {
			ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect,crystals.getAmount(aspect));
			boolean b = false;
    		for (int i = 0; i < 6; ++i)
            {
            	ItemStack itemstack1 = inv.getStackInSlot(9+i);            	
            	if (itemstack1!=null && itemstack1.getItem()==ItemsTC.crystalEssence && itemstack1.getCount()>=cs.getCount() && ItemStack.areItemStackTagsEqual(cs,itemstack1))
                {
                    b = true;
                }
            }
    		if (!b) return false;
    	}
		
		return inv instanceof IArcaneWorkbench && super.matches(dummy, world);
	}
	
	@Override
	public int getVis() {
		return vis;
	}

	@Override
	public String getResearch() {
		return research;
	}

	@Override
	public AspectList getCrystals() {
		return crystals;
	}
	
}
