package thaumcraft.api.casters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICasterTriggerManager {

	/**
	 * This class will be called by casters with the proper parameters. It is up to you to decide what to do with them.
	 */
	public boolean performTrigger(World world, ItemStack casterStack, EntityPlayer player, 
			BlockPos pos, EnumFacing side, int event);
	
}
