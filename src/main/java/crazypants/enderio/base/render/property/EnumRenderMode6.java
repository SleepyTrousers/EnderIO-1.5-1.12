package crazypants.enderio.base.render.property;

import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * A property for blocks that use the MachineSmartModel for rendering and can be rotated in all 6 directions.
 *
 */
public enum EnumRenderMode6 implements IStringSerializable {
  /**
   * The blockstate that contains the particle texture and the transforms for the item.
   * <p>
   * This will be used when the smart model is asked directly about stuff.
   */
  DEFAULTS,
  /**
   * The blockstate that will be taken over by the smart model.
   */
  AUTO,
  /**
   * The blockstate that contains the front of the machine, facing down.
   */
  FRONT(true),
  FRONT_UP(FRONT),
  FRONT_NORTH(FRONT),
  FRONT_SOUTH(FRONT),
  FRONT_WEST(FRONT),
  FRONT_EAST(FRONT),
  /**
   * The blockstate that contains the front of the machine when it is active, facing down.
   */
  FRONT_ON(true),
  FRONT_ON_UP(FRONT),
  FRONT_ON_NORTH(FRONT),
  FRONT_ON_SOUTH(FRONT_ON),
  FRONT_ON_WEST(FRONT_ON),
  FRONT_ON_EAST(FRONT_ON);

  public static final PropertyEnum<EnumRenderMode6> RENDER = PropertyEnum.<EnumRenderMode6> create("render", EnumRenderMode6.class);

  private final int parentid;
  private final boolean rotates;

  private EnumRenderMode6() {
    this(null, false);
  }

  private EnumRenderMode6(boolean rotates) {
    this(null, rotates);
  }

  private EnumRenderMode6(EnumRenderMode6 parent) {
    this(parent, parent != null);
  }

  private EnumRenderMode6(EnumRenderMode6 parent, boolean rotates) {
    if (parent != null) {
      parentid = parent.ordinal();
      this.rotates = true;
    } else {
      parentid = ordinal();
      this.rotates = rotates;
    }
  }

  /* D0-U1-N2-S3-W4-E5 */
  public EnumRenderMode6 rotate(EnumFacing facing) {
    if (rotates) {
      return values()[parentid + facing.getIndex()];
    } else {
      return this;
    }
  }

  @Override
  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
