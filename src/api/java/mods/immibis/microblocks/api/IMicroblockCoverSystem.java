package mods.immibis.microblocks.api;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import mods.immibis.core.api.multipart.ICoverSystem;
import net.minecraft.nbt.NBTTagCompound;

/** Not intended to be implemented outside of the Immibis's Microblocks mod. */
public interface IMicroblockCoverSystem extends ICoverSystem, IMicroblockIntegratedTile {
	/**
	 * Returns the position of a part.
	 * 
	 * @param part The part number.
	 * @return The part's position, or null if it's an invalid part.
	 */
	@Override
	EnumPosition getPartPosition(int part);

	boolean addPart(Part part);
	
	/**
	 * Returns true if tubes/cables/etc that use the centre of the block
	 * can connect through the specified side - ie if it is not blocked by a non-hollow cover.
	 */
	boolean isSideOpen(int side);
	
	/**
	 * Serializes the microblocks to a byte array.
	 */
	byte[] writeDescriptionBytes();
	
	/**
	 * Unserializes the microblocks from a byte array.
	 */
	void readDescriptionBytes(byte[] data, int start);
	
	/**
	 * Serializes the microblocks to a DataOutput.
	 */
	void writeDescriptionPacket(DataOutput data) throws IOException;
	
	/**
	 * Unserializes the microblocks from a DataInput.
	 */
	void readDescriptionPacket(DataInput data) throws IOException;
	
	/**
	 * Serializes the microblocks to NBT.
	 * Data will be stored in the "ICMP" key of the given compound tag.
	 */
	void writeToNBT(NBTTagCompound tag);
	
	/**
	 * Unserializes the microblocks from NBT.
	 * Data will be read from the "ICMP" key of the given compound tag.
	 */
	void readFromNBT(NBTTagCompound tag);
	
	/**
	 * Returns true if there is a part in the specified position.
	 */
	boolean isPositionOccupied(EnumPosition pos);

	/**
	 * Returns all parts in this block.
	 */
	Collection<Part> getAllParts();
	
	/**
	 * Checks if an edge is unused (for example, if wire connections can connect around corners through it)
	 * Returns the same result if face1 and face2 are swapped.
	 * 
	 * @param face1 One face bordering the edge to check.
	 * @param face2 The other face bordering the edge to check.
	 * @return True if the edge is unused.
	 */
	public boolean isEdgeOpen(int face1, int face2);
}
