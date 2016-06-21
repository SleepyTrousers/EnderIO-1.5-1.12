package mods.immibis.microblocks.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface IMicroblockSystem {

	/**
	 * Creates a microblock ICoverSystem.
	 * @param tile The tile entity this IMicroblockCoverSystem is for.
	 */
	IMicroblockCoverSystem createMicroblockCoverSystem(IMicroblockSupporterTile tile);

	/**
	 * Allows the given block to be cut up.
	 * Your block must have a reasonable implementation of getBlockTextureFromSideAndMetadata.
	 * Part IDs will be assigned automatically based on the block ID and metadata value.
	 */
	void addCuttableBlock(Block block, int meta);

	/**
	 * Returns a part type, given its ID.
	 */
	PartType<?> getPartTypeByID(int id);

	Block getMicroblockContainerBlock();
	
	
	/**
	 * Registers a custom part type.
	 * @throw IllegalArgumentException if the part type ID is already in use.
	 */
	void registerPartType(PartType<?> type) throws IllegalArgumentException;
	
	
	/**
	 * Creates an item stack of microblocks with the given part type ID and stack size.
	 * @throw IllegalArgumentException if there is no part type with the given ID.
	 */
	ItemStack partTypeIDToItemStack(int id, int stackSize) throws IllegalArgumentException;
	
	/**
	 * Gets the part ID from an item stack.
	 * @throw NullPointerException if stack == null
	 * @throw IllegalArgumentException if stack is not a stack of microblocks
	 */
	int itemStackToPartID(ItemStack stack) throws NullPointerException, IllegalArgumentException;
}
