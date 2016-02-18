package thaumcraft.api.golems;

import net.minecraft.item.ItemStack;
import thaumcraft.api.golems.seals.ISealEntity;

public class ProvisionRequest { 
	private ISealEntity seal;
	private ItemStack stack;
	
	ProvisionRequest(ISealEntity seal, ItemStack stack) {
		this.seal = seal;
		this.stack = stack.copy();
		this.stack.stackSize=this.stack.getMaxStackSize();
	}
	
	public ISealEntity getSeal() {
		return seal;
	}
	
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ProvisionRequest))
        {
            return false;
        }
        else
        {
        	ProvisionRequest pr = (ProvisionRequest)p_equals_1_;
            return !this.seal.getSealPos().equals(pr.getSeal().getSealPos()) ? false : isItemStackEqual(this.stack, pr.getStack());
        }
    }
	
	private boolean isItemStackEqual(ItemStack first, ItemStack other)
    {
        return first.stackSize != other.stackSize ? false : 
        	(first.getItem() != other.getItem() ? false : 
        		(first.getItemDamage() != other.getItemDamage() ? false : 
        			(first.getTagCompound() == null && other.getTagCompound() != null ? false : 
        				first.getTagCompound() == null || first.getTagCompound().equals(other.getTagCompound()))));
    }
}