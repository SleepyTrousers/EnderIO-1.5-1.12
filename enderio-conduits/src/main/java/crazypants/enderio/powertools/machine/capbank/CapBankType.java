package crazypants.enderio.powertools.machine.capbank;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import info.loenwind.autoconfig.factory.IValue;
import crazypants.enderio.powertools.config.CapBankConfig;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum CapBankType implements IStringSerializable {

  CREATIVE("CREATIVE", "tile.block_cap_bank.creative", CapBankConfig.tierC_maxIO, CapBankConfig.tierC_maxStorage, false, true),

  SIMPLE("SIMPLE", "tile.block_cap_bank.simple", CapBankConfig.tier1_maxIO, CapBankConfig.tier1_maxStorage, true, false),

  ACTIVATED("ACTIVATED", "tile.block_cap_bank.activated", CapBankConfig.tier2_maxIO, CapBankConfig.tier2_maxStorage, true, false),

  VIBRANT("VIBRANT", "tile.block_cap_bank.vibrant", CapBankConfig.tier3_maxIO, CapBankConfig.tier3_maxStorage, true, false),

  NONE("NONE", "tile.block_cap_bank.none", CapBankConfig.tierC_maxIO, CapBankConfig.tierC_maxStorage, false, true),

  ;

  public static final @Nonnull PropertyEnum<CapBankType> KIND = NullHelper.notnullM(PropertyEnum.<CapBankType> create("kind", CapBankType.class),
      "PropertyEnum.create()");

  public static @Nonnull List<CapBankType> types() {
    List<CapBankType> result = new ArrayList<CapBankType>();
    for (CapBankType capBankType : values()) {
      if (capBankType != NONE) {
        result.add(capBankType);
      }
    }
    return result;
  }

  public static int getMetaFromType(CapBankType type) {
    return type == null ? 0 : type.ordinal();
  }

  public static @Nonnull CapBankType getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static @Nonnull CapBankType getTypeFromUID(String uid) {
    for (CapBankType type : values()) {
      if (type.uid.equals(uid)) {
        return type;
      }
    }
    return ACTIVATED;
  }

  private final @Nonnull String uid;
  private final @Nonnull String unlocalizedName;
  private final @Nonnull IValue<Integer> maxIO;
  private final @Nonnull IValue<Integer> maxStored;
  private final boolean isMultiblock;
  private final boolean isCreative;

  private CapBankType(@Nonnull String uid, @Nonnull String unlocalizedName, @Nonnull IValue<Integer> maxIO, @Nonnull IValue<Integer> maxStored,
      boolean isMultiblock, boolean isCreative) {
    this.uid = uid;
    this.unlocalizedName = unlocalizedName;
    this.maxIO = maxIO;
    this.maxStored = maxStored;
    this.isMultiblock = isMultiblock;
    this.isCreative = isCreative;
  }

  public int getMaxIO() {
    return maxIO.get();
  }

  public int getMaxEnergyStored() {
    return maxStored.get();
  }

  public boolean isMultiblock() {
    return isMultiblock;
  }

  public boolean isCreative() {
    return isCreative;
  }

  public @Nonnull String getUnlocalizedName() {
    return unlocalizedName;
  }

  public @Nonnull String getUid() {
    return uid;
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

}
