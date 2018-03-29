package crazypants.enderio.base.filter.items;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum BasicFilterTypes implements IStringSerializable {
  filterUpgradeBasic("basic", false, 5, false, false),
  filterUpgradeAdvanced("advanced", true, 10, false, false),
  filterUpgradeLimited("limited", true, 10, true, false),
  filterUpgradeBig("big", false, 36, false, true),
  filterUpgradeBigAdvanced("big_advanced", true, 36, false, true),

  ;

  private final @Nonnull String baseName;
  private final boolean isAdvanced;
  private final int slots;
  private final boolean isLimited;
  private final boolean isBig;

  private BasicFilterTypes(@Nonnull String baseName, boolean isAdvanced, int slots, boolean isLimited, boolean isBig) {
    this.baseName = name().replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.isAdvanced = isAdvanced;
    this.slots = slots;
    this.isLimited = isLimited;
    this.isBig = isBig;
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

  public boolean isBig() {
    return isBig;
  }

}
