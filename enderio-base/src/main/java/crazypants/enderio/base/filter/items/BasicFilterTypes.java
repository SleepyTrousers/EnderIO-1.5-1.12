package crazypants.enderio.base.filter.items;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum BasicFilterTypes implements IStringSerializable {
  filterUpgradeBasic("basic", false, 5),
  filterUpgradeAdvanced("advanced", true, 10),
  filterUpgradeLimited("limited", true, 10);

  public final @Nonnull String baseName;
  public final boolean isAdvanced;
  public final int slots;

  private BasicFilterTypes(@Nonnull String baseName, boolean isAdvanced, int slots) {
    this.baseName = name().replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.isAdvanced = isAdvanced;
    this.slots = slots;
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull ItemStack getStack() {
    return getStack(1);
  }

  public @Nonnull ItemStack getStack(int size) {
    return new ItemStack(ModObject.itemBasicItemFilter.getItemNN(), size);
  }

  @Override
  public @Nonnull String getName() {
    return baseName;
  }

  public static int getMetaFromType(@Nonnull BasicFilterTypes value) {
    return value.ordinal();
  }

  public boolean isAdvanced() {
    return isAdvanced;
  }

  public int getSlots() {
    return slots;
  }

}
