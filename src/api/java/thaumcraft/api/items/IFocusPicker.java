package thaumcraft.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Used by wand foci like the trade focus to get an itemstack and display it on the wand hud.
 * @author azanor
 */
public interface IFocusPicker {	
	/**
	 * Get the currently picked item 
	 * @param stack the focus stack
	 * @return the picked stack
	 */
	public ItemStack getPickedBlock(ItemStack stack);
}
