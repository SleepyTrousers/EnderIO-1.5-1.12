package crazypants.enderio.api.teleport;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

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
  private int targetX;

  private int targetY;

  private int targetZ;

  private int dimension;

  private final TravelSource source;

  /**
   * Fired before an entity teleports to the given location.
   * 
   * @param entity
   *          The entity teleporting
   * @param x
   *          The target X coord
   * @param y
   *          The target Y coord
   * @param z
   *          The target Z coord
   */
  public TeleportEntityEvent(Entity entity, TravelSource source, int x, int y, int z, int dimension) {
    super(entity);
    this.setTargetX(x);
    this.setTargetY(y);
    this.setTargetZ(z);
    this.source = source;
    this.setDimension(dimension);
  }

  public int getTargetX() {
    return targetX;
  }

  public void setTargetX(int targetX) {
    this.targetX = targetX;
  }

  public int getTargetY() {
    return targetY;
  }

  public void setTargetY(int targetY) {
    this.targetY = targetY;
  }

  public int getTargetZ() {
    return targetZ;
  }

  public void setTargetZ(int targetZ) {
    this.targetZ = targetZ;
  }

  public int getDimension() {
    return dimension;
  }

  public void setDimension(int dimension) {
    this.dimension = dimension;
  }

  public TravelSource getSource() {
    return source;
  }
}
