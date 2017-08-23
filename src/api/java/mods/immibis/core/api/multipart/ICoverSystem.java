package mods.immibis.core.api.multipart;


/**
 * An object that allows extra parts to be added to multipart tiles.
 * For example, you can add covers to InfiniTubes transport conduits
 * because they use a MicroblockCoverSystem.
 *
 * This interface's existence is a bit unintuitive, but it seemed to "naturally fall out" of the design.
 */
public interface ICoverSystem extends IPartContainer {
	/**
	 * Converts the block containing this cover system into
	 * a block containing only parts from this cover system,
	 * or to air if the cover system has no parts.
	 * 
	 * Call it when, for example, all wires in a RedLogic wire block are destroyed,
	 * to replace it with a microblock container block if there were any microblocks
	 * in the wire block.
	 * 
	 * TODO should this be called on the client, server or both?
	 * 
	 * Note: For custom implementations, this should be capable of being called even after
	 * the block and tile entity are removed (and should resurrect them)
	 */
	public void convertToContainerBlock();
}
