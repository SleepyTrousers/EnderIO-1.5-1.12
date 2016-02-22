package crazypants.enderio.render;

import java.util.Locale;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumRenderOverlay implements IStringSerializable {
  NONE,
  BODY(true),
  BODY_SOUTH(BODY),
  BODY_WEST(BODY),
  BODY_EAST(BODY), ;

  private final int parentid;
  private final boolean rotates;

  private EnumRenderOverlay() {
    this(null, false);
  }

  private EnumRenderOverlay(boolean rotates) {
    this(null, rotates);
  }

  private EnumRenderOverlay(EnumRenderOverlay parent) {
    this(parent, parent != null);
  }

  private EnumRenderOverlay(EnumRenderOverlay parent, boolean rotates) {
    if (parent != null) {
      parentid = parent.ordinal();
      this.rotates = true;
    } else {
      parentid = 0;
      this.rotates = rotates;
    }
  }

  /* D-U-N-S-W-E */
  public EnumRenderOverlay rotate(EnumFacing facing) {
    if (rotates) {
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
