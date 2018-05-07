package thaumcraft.api.golems.seals;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ISealConfigFilter {
	
	public NonNullList<ItemStack> getInv();
	
	public int getFilterSize();
	
	public ItemStack getFilterSlot(int i);
	
	public void setFilterSlot(int i, ItemStack stack);
	
	public boolean isBlacklist();
	
	public void setBlacklist(boolean black);
	
	public boolean hasStacksizeLimiters();
	
}
