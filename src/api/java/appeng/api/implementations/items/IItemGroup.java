package appeng.api.implementations.items;

import java.util.Set;

import net.minecraft.item.ItemStack;

/**
 * Lets you specify the name of the group of items this falls under.
 */
public interface IItemGroup
{

	/**
	 * returning null, is the same as not implementing the interface at all.
	 * 
	 * @param is
	 * @return an unlocalized string to use for the items group name.
	 */
	String getUnlocalizedGroupName(Set<ItemStack> otherItems, ItemStack is);

}
