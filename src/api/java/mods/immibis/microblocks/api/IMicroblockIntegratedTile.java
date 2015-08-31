package mods.immibis.microblocks.api;

/**
 * If you have an IMicroblockSupporterTile that is also an IPartContainer,
 * you should implement this for better integration.
 * 
 * You might, in rare circumstances, want to implement it just for isPositionOccupied, without being an IPartContainer.
 */
public interface IMicroblockIntegratedTile {
	/**
	 * Returns the microblock position a tile-owned part mainly occupies, or null if
	 * unknown or the part occupies the whole block.
	 * Used to determine the position of new microblocks placed by the player.
	 * 
	 * If this tile doesn't implement IPartContainer as well, this is never called.
	 * 
	 * @param subHit The part index.
	 * @return The position the part occupies.
	 */
	public EnumPosition getPartPosition(int index);
	
	/**
	 * Returns true if the given position is occupied by the tile.
	 * Does not check for collisions with other positions.
	 */
	public boolean isPositionOccupied(EnumPosition pos);
}
