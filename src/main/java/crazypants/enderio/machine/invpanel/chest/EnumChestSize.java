package crazypants.enderio.machine.invpanel.chest;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

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

  @SuppressWarnings("null")
  public static final @Nonnull PropertyEnum<EnumChestSize> SIZE = PropertyEnum.<EnumChestSize> create("size", EnumChestSize.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public String getUnlocalizedName(Item me) {
    return me.getUnlocalizedName() + "_" + getName();
  }

  public static EnumChestSize getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(EnumChestSize value) {
    return value.ordinal();
  }


}
