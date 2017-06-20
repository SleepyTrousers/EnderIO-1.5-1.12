package crazypants.enderio.machine.capbank;

import com.enderio.core.common.util.NullHelper;
import crazypants.enderio.config.Config;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum CapBankType implements IStringSerializable {

  CREATIVE("CREATIVE", "tile.blockCapBank.creative", 500000, Config.capacitorBankTierTwoMaxStorageRF, false, true),

  SIMPLE("SIMPLE", "tile.blockCapBank.simple", Config.capacitorBankTierOneMaxIoRF, Config.capacitorBankTierOneMaxStorageRF, true, false),

  ACTIVATED("ACTIVATED", "tile.blockCapBank.activated", Config.capacitorBankTierTwoMaxIoRF, Config.capacitorBankTierTwoMaxStorageRF, true, false),

  VIBRANT("VIBRANT", "tile.blockCapBank.vibrant", Config.capacitorBankTierThreeMaxIoRF, Config.capacitorBankTierThreeMaxStorageRF, true, false),

  NONE("NONE", "tile.blockCapBank.none", 0, 0, false, true),

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
  private final int maxIO;
  private final int maxStored;
  private final boolean isMultiblock;
  private final boolean isCreative;

  private CapBankType(@Nonnull String uid, @Nonnull String unlocalizedName, int maxIO, int maxStored, boolean isMultiblock, boolean isCreative) {
    this.uid = uid;
    this.unlocalizedName = unlocalizedName;
    this.maxIO = maxIO;
    this.maxStored = maxStored;
    this.isMultiblock = isMultiblock;
    this.isCreative = isCreative;
  }

  public int getMaxIO() {
    return maxIO;
  }

  public int getMaxEnergyStored() {
    return maxStored;
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
