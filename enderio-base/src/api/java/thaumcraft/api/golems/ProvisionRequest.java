package thaumcraft.api.golems;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.seals.ISealEntity;

public class ProvisionRequest { 
	private ISealEntity seal;
	private Entity entity;
	private BlockPos pos;
	private EnumFacing side;
	private ItemStack stack;
	private int id;
	
	ProvisionRequest(ISealEntity seal, ItemStack stack) {
		this.seal = seal;
		this.stack = stack.copy();
		String s = seal.getSealPos().pos.toString() + seal.getSealPos().face.name() +stack.toString();
		if (stack.hasTagCompound()) s += stack.getTagCompound().toString();
		this.id = s.hashCode();
	}
	
	ProvisionRequest(BlockPos pos, EnumFacing side, ItemStack stack) {
		this.pos = pos;
		this.side = side;
		this.stack = stack.copy();
		String s = pos.toString() + side.name() +stack.toString();
		if (stack.hasTagCompound()) s += stack.getTagCompound().toString();
		this.id = s.hashCode();
	}
	
	ProvisionRequest(Entity entity, ItemStack stack) {
		this.entity = entity;
		this.stack = stack.copy();
		String s = entity.getEntityId() + stack.toString();
		if (stack.hasTagCompound()) s += stack.getTagCompound().toString();
		this.id = s.hashCode();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ISealEntity getSeal() {
		return seal;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public ItemStack getStack() {
		return stack;
	}
	
	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}
	
	public EnumFacing getSide() {
		return side;
	}

	public void setSide(EnumFacing side) {
		this.side = side;
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
            return this.id == pr.id;
        }
    }
	
	private boolean isItemStackEqual(ItemStack first, ItemStack other)
    {
        return first.getCount() != other.getCount() ? false : 
        	(first.getItem() != other.getItem() ? false : 
        		(first.getItemDamage() != other.getItemDamage() ? false : 
        			(first.getTagCompound() == null && other.getTagCompound() != null ? false : 
        				first.getTagCompound() == null || first.getTagCompound().equals(other.getTagCompound()))));
    }
}