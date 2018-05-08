package thaumcraft.api.casters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *  
 * @author azanor
 * 
 * Add this to a tile entity that you wish casters to interact with in some way. 
 *
 */

public interface IInteractWithCaster {

	public boolean onCasterRightClick(World world, ItemStack casterStack, EntityPlayer player, BlockPos pos, EnumFacing side, EnumHand hand);
	
}
