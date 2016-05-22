package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.config.Config;
import crazypants.util.NullHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public enum CapBankType implements IStringSerializable {

  NONE("NONE", "tile.blockCapBank.none", 0, 0, false, true),

  CREATIVE("CREATIVE", "tile.blockCapBank.creative", 500000, Config.capacitorBankTierTwoMaxStorageRF, false, true),

  SIMPLE("SIMPLE", "tile.blockCapBank.simple", Config.capacitorBankTierOneMaxIoRF, Config.capacitorBankTierOneMaxStorageRF, true, false),

  ACTIVATED("ACTIVATED", "tile.blockCapBank.activated", Config.capacitorBankTierTwoMaxIoRF, Config.capacitorBankTierTwoMaxStorageRF, true, false),

  VIBRANT("VIBRANT", "tile.blockCapBank.vibrant", Config.capacitorBankTierThreeMaxIoRF, Config.capacitorBankTierThreeMaxStorageRF, true, false);

  public static final @Nonnull PropertyEnum<CapBankType> KIND = PropertyEnum.<CapBankType> create("kind", CapBankType.class);

  private static final @Nonnull List<CapBankType> TYPES = new ArrayList<CapBankType>();

  static {
    TYPES.add(CREATIVE);
    TYPES.add(SIMPLE);
    TYPES.add(ACTIVATED);
    TYPES.add(VIBRANT);
  }

  public static @Nonnull List<CapBankType> types() {
    return TYPES;
  }

  public static int getMetaFromType(@Nonnull CapBankType type) {
    for (int i = 0; i < TYPES.size(); i++) {
      if (TYPES.get(i) == type) {
        return i;
      }
    }
    return 1;
  }

  public static @Nonnull CapBankType getTypeFromMeta(int metaIn) {
    int meta = MathHelper.clamp_int(metaIn, 0, TYPES.size() - 1);
    return NullHelper.notnull(types().get(meta), "CapBank type list corrupted");
  }

  public static @Nonnull CapBankType getTypeFromUID(String uid) {
    for (CapBankType type : TYPES) {
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

  public void writeTypeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    nbtRoot.setString("type", getUid());
  }

  public static @Nonnull CapBankType readTypeFromNBT(@Nullable NBTTagCompound nbtRoot) {
    if (nbtRoot == null || !nbtRoot.hasKey("type")) {
      return ACTIVATED;
    }
    return getTypeFromUID(nbtRoot.getString("type"));
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

}
