package thaumcraft.api.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IArchitectExtended extends IArchitect {

	/**
	 * Returns the location that should be used as the starting point. 
	 */
	public MovingObjectPosition getArchitectMOP(ItemStack stack, World world, EntityLivingBase player);

	/**
	 * @return will this trigger on block highlighting event
	 */
	public boolean useBlockHighlight(ItemStack stack);
}
