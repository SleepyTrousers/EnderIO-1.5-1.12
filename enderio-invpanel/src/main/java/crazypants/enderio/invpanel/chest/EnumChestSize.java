package crazypants.enderio.invpanel.chest;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

public enum EnumChestSize implements IStringSerializable {
  // Simple
  TINY(9),
  SMALL(12),
  MEDIUM(15),
  // Normal
  BIG(18),
  LARGE(24),
  HUGE(30),
  // Enhanced
  ENORMOUS(39),
  WAREHOUSE(48),
  WAREHOUSE13(60),

  // Be honest, you expected a bra size joke here, didn't you?
  // TODO add bad bra joke here
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

  public static int getMetaFromType(EnumChestSize value) {
    return value.ordinal();
  }


}
