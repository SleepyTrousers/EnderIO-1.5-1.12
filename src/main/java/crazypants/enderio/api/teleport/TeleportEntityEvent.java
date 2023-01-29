package crazypants.enderio.api.teleport;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * An event that can be used to respond to, edit, and prevent entity teleports.
 * <p>
 * This event will fire under all circumstances:
 * <ul>
 * <li>Travel Anchors</li>
 * <li>Staff of travelling</li>
 * <li>Telepad</li>
 * </ul>
 *
 * As well as any externally added teleports, assuming they fire this event manually.
 *
 */
@Cancelable
public class TeleportEntityEvent extends EntityEvent {

    /**
     * The target coords. These can be edited by event handlers.
     */
    public int targetX, targetY, targetZ;

    public final TravelSource source;

    /**
     * Fired before an entity teleports to the given location.
     *
     * @param entity The entity teleporting
     * @param x      The target X coord
     * @param y      The target Y coord
     * @param z      The target Z coord
     */
    public TeleportEntityEvent(Entity entity, TravelSource source, int x, int y, int z) {
        super(entity);
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.source = source;
    }
}
