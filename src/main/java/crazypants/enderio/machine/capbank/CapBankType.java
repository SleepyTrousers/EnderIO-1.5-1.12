package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import crazypants.enderio.config.Config;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;

public enum CapBankType implements IStringSerializable {

  NONE("NONE", "tile.blockCapBank.none", 0, 0, false, true),

  CREATIVE("CREATIVE", "tile.blockCapBank.creative", 500000, Config.capacitorBankTierTwoMaxStorageRF, false, true),

  SIMPLE("SIMPLE", "tile.blockCapBank.simple", Config.capacitorBankTierOneMaxIoRF, Config.capacitorBankTierOneMaxStorageRF, true, false),

  ACTIVATED("ACTIVATED", "tile.blockCapBank.activated", Config.capacitorBankTierTwoMaxIoRF, Config.capacitorBankTierTwoMaxStorageRF, true, false),

  VIBRANT("VIBRANT", "tile.blockCapBank.vibrant", Config.capacitorBankTierThreeMaxIoRF, Config.capacitorBankTierThreeMaxStorageRF, true, false);

  public static final PropertyEnum<CapBankType> KIND = PropertyEnum.<CapBankType> create("kind", CapBankType.class);

  private static final List<CapBankType> TYPES = new ArrayList<CapBankType>();

  static {
    TYPES.add(CREATIVE);
    TYPES.add(SIMPLE);
    TYPES.add(ACTIVATED);
    TYPES.add(VIBRANT);
  }

  public static List<CapBankType> types() {
    return TYPES;
  }

  public static int getMetaFromType(CapBankType type) {
    for (int i = 0; i < TYPES.size(); i++) {
      if(TYPES.get(i) == type) {
        return i;
      }
    }
    return 1;
  }

  public static CapBankType getTypeFromMeta(int meta) {
    meta = MathHelper.clamp_int(meta, 0, TYPES.size() - 1);
    return types().get(meta);
  }

  public static CapBankType getTypeFromUID(String uid) {
    for (CapBankType type : TYPES) {
      if(type.uid.equals(uid)) {
        return type;
      }
    }
    return ACTIVATED;
  }

  private final String uid;
  private final String unlocalizedName;
  private final int maxIO;
  private final int maxStored;
  private final boolean isMultiblock;
  private final boolean isCreative;

  private CapBankType(String uid, String unlocalizedName, int maxIO, int maxStored, boolean isMultiblock, boolean isCreative) {
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

  public String getUnlocalizedName() {
    return unlocalizedName;
  }

  public String getUid() {
    return uid;
  }

  public void writeTypeToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setString("type", getUid());
  }

  public static CapBankType readTypeFromNBT(NBTTagCompound nbtRoot) {
    if(nbtRoot == null || !nbtRoot.hasKey("type")) {
      return ACTIVATED;
    }
    return getTypeFromUID(nbtRoot.getString("type"));
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

}
