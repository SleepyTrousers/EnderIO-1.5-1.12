package crazypants.enderio.base.render.property;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

public enum EnumDecoBlock implements IStringSerializable {
  TYPE00,
  TYPE01,
  TYPE02,
  TYPE03,
  TYPE04,
  TYPE05,
  TYPE06,
  TYPE07,
  TYPE08,
  TYPE09,
  TYPE10,
  TYPE11,
  TYPE12,
  TYPE13,
  TYPE14,
  TYPE15,

  ;

  public static final @Nonnull PropertyEnum<EnumDecoBlock> TYPE = PropertyEnum.<EnumDecoBlock> create("type", EnumDecoBlock.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public @Nonnull String getUnlocalizedName(Item me) {
    return me.getUnlocalizedName() + "." + getName();
  }

  public static @Nonnull EnumDecoBlock getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "enum has null members...");
  }

  public static int getMetaFromType(@Nonnull EnumDecoBlock value) {
    return value.ordinal();
  }


}
