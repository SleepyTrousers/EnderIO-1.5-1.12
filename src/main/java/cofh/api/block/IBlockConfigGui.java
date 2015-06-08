package cofh.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this interface on blocks which have a GUI that needs a tool (e.g., multimeter) to open.
 *
 */
public interface IBlockConfigGui {

	/**
	 * This function will open a GUI if the player has permission.
	 *
	 * @param world
	 *            Reference to the world.
	 * @param x
	 *            X coordinate of the block.
	 * @param y
	 *            Y coordinate of the block.
	 * @param z
	 *            Z coordinate of the block.
	 * @param side
	 *            The side of the block.
	 * @param player
	 *            Player doing the configuring.
	 * @return True if the GUI was opened.
	 */
	public boolean openConfigGui(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player);
}
