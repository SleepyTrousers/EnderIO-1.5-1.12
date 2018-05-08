package thaumcraft.api.items;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IArchitect {
	
	/**
	 * Returns the location that should be used as the starting point. 
	 */
	public RayTraceResult getArchitectMOP(ItemStack stack, World world, EntityLivingBase player);

	/**
	 * @return will this trigger on block highlighting event
	 */
	public boolean useBlockHighlight(ItemStack stack);

	/**
	 * Returns a list of blocks that should be highlighted in world. The starting point is whichever block the player currently has highlighted in the world.
	 */
	public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, World world, 
			BlockPos pos, EnumFacing side, EntityPlayer player);
	
	/**
	 * which axis should be displayed. 
	 */
	public boolean showAxis(ItemStack stack, World world, EntityPlayer player, EnumFacing side, 
			EnumAxis axis);
	
	public enum EnumAxis {
		X, // east / west
		Y, // up / down
		Z; // north / south
	}
}
