package crazypants.enderio.base.conduit.facade;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public enum EnumFacadeType implements IStringSerializable {

  BASIC("", false, false),
  HARDENED(".hardened", true, false),
  TRANSPARENT(".transparent", false, true),
  TRANSPARENT_HARDENED(".transparent.hardened", true, true);

  private final @Nonnull String namePostfix;
  private final boolean hardened, transparent;

  private EnumFacadeType(@Nonnull String namePostfix, boolean hardened, boolean transparent) {
    this.namePostfix = namePostfix;
    this.hardened = hardened;
    this.transparent = transparent;
  }

  @Override
  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public @Nonnull String getUnlocName(@Nonnull Item me) {
    return me.getUnlocalizedName() + namePostfix;
  }

  public static @Nonnull EnumFacadeType getTypeFromMeta(int meta) {
    return NullHelper.first(values()[MathHelper.clamp(meta, 0, values().length - 1)], BASIC);
  }

  public static int getMetaFromType(@Nonnull EnumFacadeType value) {
    return value.ordinal();
  }

  public boolean isHardened() {
    return hardened;
  }

  public boolean isTransparent() {
    return transparent;
  }

}
