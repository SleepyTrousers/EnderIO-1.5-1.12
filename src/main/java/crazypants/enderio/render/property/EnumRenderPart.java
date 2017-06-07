package crazypants.enderio.render.property;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumRenderPart implements IStringSerializable {
  DEFAULTS,
  BODY(true),
  BODY_SOUTH(BODY),
  BODY_WEST(BODY),
  BODY_EAST(BODY),
  SOUL(true),
  SOUL_SOUTH(SOUL),
  SOUL_WEST(SOUL),
  SOUL_EAST(SOUL),
  PAINT_OVERLAY,
  SOUL_FRAME(true),
  SOUL_FRAME_SOUTH(SOUL_FRAME),
  SOUL_FRAME_WEST(SOUL_FRAME),
  SOUL_FRAME_EAST(SOUL_FRAME);

  public static final @Nonnull PropertyEnum<EnumRenderPart> SUB = PropertyEnum.<EnumRenderPart> create("sub", EnumRenderPart.class);

  private final int parentid;
  private final boolean rotates;

  private EnumRenderPart() {
    this(null, false);
  }

  private EnumRenderPart(boolean rotates) {
    this(null, rotates);
  }

  private EnumRenderPart(@Nullable EnumRenderPart parent) {
    this(parent, parent != null);
  }

  private EnumRenderPart(@Nullable EnumRenderPart parent, boolean rotates) {
    if (parent != null) {
      parentid = parent.ordinal();
      this.rotates = true;
    } else {
      parentid = ordinal();
      this.rotates = rotates;
    }
  }

  /* D0-U1-N2-S3-W4-E5 */
  public @Nonnull EnumRenderPart rotate(@Nonnull EnumFacing facing) {
    if (rotates && facing.getIndex() >= 2) {
      return NullHelper.notnullJ(values()[parentid + facing.getIndex() - 2], "Enum.values()");
    } else {
      return this;
    }
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

}
