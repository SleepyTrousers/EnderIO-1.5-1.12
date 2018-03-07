package crazypants.enderio.machine.invpanel.chest;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum EnumChestSize implements IStringSerializable {
  TINY(9),
  SMALL(12),
  MEDIUM(15),
  BIG(18),
  LARGE(21),
  HUGE(24),
  ENORMOUS(27),
  WAREHOUSE(30),
  WAREHOUSE13(60),

  // Be honest, you expected a bra size joke here, didn't you?

  ;

  private final int slots;

  private EnumChestSize(int rows) {
    this.slots = rows * 9;
  }

  public int getSlots() {
    return slots;
  }

  public static final @Nonnull PropertyEnum<EnumChestSize> SIZE = PropertyEnum.<EnumChestSize> create("size", EnumChestSize.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  @Nonnull
  public String getUnlocalizedName(Item me) {
    return me.getUnlocalizedName() + "_" + getName();
  }

  @Nonnull
  public static EnumChestSize getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "EnumChestSize#values");
  }

  public static int getMetaFromType(EnumChestSize value) {
    return value.ordinal();
  }


}
