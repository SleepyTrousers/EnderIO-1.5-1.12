package crazypants.enderio.base.filter.items;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum BasicFilterTypes implements IStringSerializable {
  filterUpgradeBasic("basic", false, 5, false),
  filterUpgradeAdvanced("advanced", true, 10, false),
  filterUpgradeLimited("limited", true, 10, true);

  private final @Nonnull String baseName;
  private final boolean isAdvanced;
  private final int slots;
  private final boolean isLimited;

  private BasicFilterTypes(@Nonnull String baseName, boolean isAdvanced, int slots, boolean isLimited) {
    this.baseName = name().replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.isAdvanced = isAdvanced;
    this.slots = slots;
    this.isLimited = isLimited;
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull ItemStack getBasicFilterStack() {
    return new ItemStack(ModObject.itemBasicItemFilter.getItemNN(), 1);
  }

  @Override
  public @Nonnull String getName() {
    return baseName;
  }

  public boolean isAdvanced() {
    return isAdvanced;
  }

  public int getSlots() {
    return slots;
  }

  public boolean isLimited() {
    return isLimited;
  }

}
