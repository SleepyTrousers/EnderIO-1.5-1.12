package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDustTrigger {
	
	/**
	 * Checks to see if using dust on the passed in location and face will result in a valid operation.
	 * This is performed on client and server
	 * @param world
	 * @param player
	 * @param pos
	 * @param face
	 * @return the placement offset (from clicked block) and facing (if used). Can contain . Return Null if not valid
	 */
	public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face);
	
	class Placement {
		public int xOffset,yOffset,zOffset;
		public EnumFacing facing;
		public Placement(int xOffset, int yOffset, int zOffset, EnumFacing facing) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.zOffset = zOffset;
			this.facing = facing;
		}		
	}
	
	/**
	 * The operation to perfom if the location is valid.
	 * This is performed on client and server
	 * @param world
	 * @param player
	 * @param pos
	 * @param placement
	 * @param side 
	 */
	public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side);
	
	/**
	 * This method returns a list of block locations that should display the dust sparkle fx.
	 * By default it will return the block clicked on.
	 * @param world
	 * @param player
	 * @param pos
	 * @param placement
	 * @param side
	 * @return
	 */
	public default List<BlockPos> sparkle(World world, EntityPlayer player, BlockPos pos, Placement placement) {
		return Arrays.asList(new BlockPos[]{pos});
	}
	
	/* 
	 * Internal methods
	 */
	public static ArrayList<IDustTrigger> triggers = new ArrayList<>();
	
	/**
	 * Adds a custom trigger class to the registry
	 * @param trigger
	 */
	public static void registerDustTrigger(IDustTrigger trigger) {
		triggers.add(trigger);
	}
	
	
}
