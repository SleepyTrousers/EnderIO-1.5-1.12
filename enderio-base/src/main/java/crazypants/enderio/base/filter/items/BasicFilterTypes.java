package crazypants.enderio.base.filter.items;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum BasicFilterTypes implements IStringSerializable {
  filterUpgradeBasic("basic"),
  filterUpgradeAdvanced("advanced"),
  filterUpgradeLimited("limited");

  public final @Nonnull String baseName;

  private BasicFilterTypes(@Nonnull String baseName) {
    this.baseName = name().replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull ItemStack getStack() {
    return getStack(1);
  }

  public @Nonnull ItemStack getStack(int size) {
    return new ItemStack(ModObject.itemItemFilter.getItemNN(), size, ordinal());
  }

  @Override
  public @Nonnull String getName() {
    return baseName;
  }

  public static @Nonnull BasicFilterTypes getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull BasicFilterTypes value) {
    return value.ordinal();
  }

}
