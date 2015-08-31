package mods.immibis.core.api.multipart;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * Fired on the Forge event bus when a part finishes being broken.
 * 
 * This is fired on both the client and server.
 */
@Cancelable
public class PartBreakEvent extends Event {
	public final World world;
	public final PartCoordinates coords;
	public final EntityLivingBase player;
	
	public PartBreakEvent(World world, PartCoordinates coords, EntityLivingBase player) {
		this.world = world;
		this.coords = coords;
		this.player = player;
	}
}
