package mods.immibis.microblocks.api;

import mods.immibis.core.api.multipart.ICoverableTile;

/**
 * A multipart tile that is compatible with microblocks.
 * Only tile entities should implement this.
 */
public interface IMicroblockSupporterTile extends ICoverableTile {
	/**
	 * Returns true if the tile is "in the way" of this part and will prevent it being placed.
	 * @param type The type of part being placed.
	 * @param pos The position the part is being placed in.
	 * @return False to prevent part placement, otherwise true.
	 */
	public boolean isPlacementBlocked(PartType<?> type, EnumPosition pos);

	@Override
	public IMicroblockCoverSystem getCoverSystem();
	
	/**
	 * Called after microblocks are added or removed.
	 * 
	 * You should probably at least call World.markBlockForUpdate and world.notifyBlocksOfNeighborChange.
	 */
	public void onMicroblocksChanged();
}
