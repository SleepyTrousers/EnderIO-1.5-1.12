package mods.immibis.microblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IMicroblockPermissionHandler {
	/**
	 * Returns true if this permission handler wants to prevent placing a microblock.
	 * This is called on both the client and the server.
	 * 
	 * @param player The player placing a part.
	 * @param w The world.
	 * @param x The x coordinate of the microblock container block.
	 * @param y The y coordinate of the microblock container block.
	 * @param z The z coordinate of the microblock container block.
	 * @param p The part being placed.
	 */
	public boolean doesPreventPlacing(EntityPlayer player, World w, int x, int y, int z, Part p);
	
	/**
	 * Returns true if this permission handler wants to prevent breaking a microblock.
	 * This is called on both the client and the server.
	 * 
	 * Not currently implemented. If you want to use this, get immibis to finish it.
	 * 
	 * @param player The player breaking a part.
	 * @param w The world.
	 * @param x The x coordinate of the microblock container block.
	 * @param y The y coordinate of the microblock container block.
	 * @param z The z coordinate of the microblock container block.
	 * @param tile The microblock container tile entity.
	 * @param p The part being broken.
	 */
	public boolean doesPreventBreaking(EntityPlayer player, World w, int x, int y, int z, IMicroblockSupporterTile tile, Part p);
}
