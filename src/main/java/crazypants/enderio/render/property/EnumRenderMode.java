package crazypants.enderio.render.property;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * A property for blocks that use the MachineSmartModel for rendering.
 *
 */
public enum EnumRenderMode implements IStringSerializable {
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
   * The blockstate that contains the front of the machine, facing north.
   */
  FRONT(true),
  FRONT_SOUTH(FRONT),
  FRONT_WEST(FRONT),
  FRONT_EAST(FRONT),
  /**
   * The blockstate that contains the front of the machine when it is active, facing north.
   */
  FRONT_ON(true),
  FRONT_ON_SOUTH(FRONT_ON),
  FRONT_ON_WEST(FRONT_ON),
  FRONT_ON_EAST(FRONT_ON);

  public static final @Nonnull PropertyEnum<EnumRenderMode> RENDER = PropertyEnum.<EnumRenderMode> create("render", EnumRenderMode.class);

  private final int parentid;
  private final boolean rotates;

  private EnumRenderMode() {
    this(null, false);
  }

  private EnumRenderMode(boolean rotates) {
    this(null, rotates);
  }

  private EnumRenderMode(@Nullable EnumRenderMode parent) {
    this(parent, parent != null);
  }

  private EnumRenderMode(@Nullable EnumRenderMode parent, boolean rotates) {
    if (parent != null) {
      parentid = parent.ordinal();
      this.rotates = true;
    } else {
      parentid = ordinal();
      this.rotates = rotates;
    }
  }

  /* D0-U1-N2-S3-W4-E5 */
  public EnumRenderMode rotate(@Nonnull EnumFacing facing) {
    if (rotates && facing.getIndex() >= 2) {
      return values()[parentid + facing.getIndex() - 2];
    } else {
      return this;
    }
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

}
