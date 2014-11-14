package appeng.api.parts;

import net.minecraft.item.ItemStack;

public interface IAlphaPassItem
{

	/**
	 * Extend, and return true to enable a second pass for your parts in the bus rendering pipe line.
	 * 
	 * @param is
	 * @return
	 */
	boolean useAlphaPass(ItemStack is);

}
