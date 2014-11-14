package appeng.api.implementations;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import appeng.api.networking.crafting.ICraftingPatternDetails;

/**
 * Implemented on {@link Item}
 */
public interface ICraftingPatternItem
{

	/**
	 * Access Details about a patern
	 * 
	 * @param is
	 * @param w
	 * @return
	 */
	ICraftingPatternDetails getPatternForItem(ItemStack is, World w);
}
