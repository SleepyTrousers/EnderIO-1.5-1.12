package appeng.api.storage.data;

import java.util.Collection;

import appeng.api.config.FuzzyMode;

/**
 * Represents a list of items in AE.
 * 
 * Don't Implement.
 * 
 * Construct with Util.createItemList()
 */
public interface IItemContainer<StackType extends IAEStack>
{

	/**
	 * add a stack to the list, this will merge the stack with an item already in the list if found.
	 * 
	 * @param option
	 */
	public void add(StackType option); // adds stack as is

	/**
	 * @param i
	 * @return a stack equivalent to the stack passed in, but with the correct stack size information, or null if its
	 *         not present
	 */
	StackType findPrecise(StackType i);

	/**
	 * @param input
	 * @return a list of relevant fuzzy matched stacks
	 */
	public Collection<StackType> findFuzzy(StackType input, FuzzyMode fuzzy);

	/**
	 * @return true if there are no items in the list
	 */
	public boolean isEmpty();

}