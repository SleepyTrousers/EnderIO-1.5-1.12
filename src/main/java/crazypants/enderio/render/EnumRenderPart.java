package crazypants.enderio.render;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumRenderPart implements IStringSerializable {
  DEFAULTS,
  BODY(true),
  BODY_SOUTH(BODY),
  BODY_WEST(BODY),
  BODY_EAST(BODY), ;

  public static final PropertyEnum<EnumRenderPart> SUB = PropertyEnum.<EnumRenderPart> create("sub", EnumRenderPart.class);

  private final int parentid;
  private final boolean rotates;

  private EnumRenderPart() {
    this(null, false);
  }

  private EnumRenderPart(boolean rotates) {
    this(null, rotates);
  }

  private EnumRenderPart(EnumRenderPart parent) {
    this(parent, parent != null);
  }

  private EnumRenderPart(EnumRenderPart parent, boolean rotates) {
    if (parent != null) {
      parentid = parent.ordinal();
      this.rotates = true;
    } else {
      parentid = ordinal();
      this.rotates = rotates;
    }
  }

  /* D0-U1-N2-S3-W4-E5 */
  public EnumRenderPart rotate(EnumFacing facing) {
    if (rotates && facing.getIndex() >= 2) {
      return values()[parentid + facing.getIndex() - 2];
    } else {
      return this;
    }
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

}
