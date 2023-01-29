package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import crazypants.enderio.config.Config;

public class CapBankType {

    public static final CapBankType CREATIVE = new CapBankType(
            "CREATIVE",
            "tile.blockCapBank.creative",
            500000,
            Config.capacitorBankTierTwoMaxStorageRF,
            false,
            true,
            "enderio:capacitorBank",
            "enderio:capacitorBankCreativeBorder",
            "enderio:capacitorBankInput",
            "enderio:capacitorBankOutput",
            "enderio:capacitorBankLocked");

    public static final CapBankType SIMPLE = new CapBankType(
            "SIMPLE",
            "tile.blockCapBank.simple",
            Config.capacitorBankTierOneMaxIoRF,
            Config.capacitorBankTierOneMaxStorageRF,
            true,
            false,
            "enderio:capacitorBank",
            "enderio:capacitorBankSimpleBorder",
            "enderio:capacitorBankInput",
            "enderio:capacitorBankOutput",
            "enderio:capacitorBankLocked");

    public static final CapBankType ACTIVATED = new CapBankType(
            "ACTIVATED",
            "tile.blockCapBank.activated",
            Config.capacitorBankTierTwoMaxIoRF,
            Config.capacitorBankTierTwoMaxStorageRF,
            true,
            false,
            "enderio:capacitorBank",
            "enderio:capacitorBankActivatedBorder",
            "enderio:capacitorBankInput",
            "enderio:capacitorBankOutput",
            "enderio:capacitorBankLocked");

    public static final CapBankType VIBRANT = new CapBankType(
            "VIBRANT",
            "tile.blockCapBank.vibrant",
            Config.capacitorBankTierThreeMaxIoRF,
            Config.capacitorBankTierThreeMaxStorageRF,
            true,
            false,
            "enderio:capacitorBank",
            "enderio:capacitorBankVibrantBorder",
            "enderio:capacitorBankInput",
            "enderio:capacitorBankOutput",
            "enderio:capacitorBankLocked");

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
            if (TYPES.get(i) == type) {
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
            if (type.uid.equals(uid)) {
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
    private final String icon;
    private final String borderIcon;
    private final String inputIcon;
    private final String outputIcon;
    private final String lockedIcon;

    public CapBankType(String uid, String unlocalizedName, int maxIO, int maxStored, boolean isMultiblock,
            boolean isCreative, String icon, String borderIcon, String inputIcon, String outputIcon,
            String lockedIcon) {
        this.uid = uid;
        this.unlocalizedName = unlocalizedName;
        this.maxIO = maxIO;
        this.maxStored = maxStored;
        this.isMultiblock = isMultiblock;
        this.isCreative = isCreative;
        this.icon = icon;
        this.borderIcon = borderIcon;
        this.inputIcon = inputIcon;
        this.outputIcon = outputIcon;
        this.lockedIcon = lockedIcon;
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

    public String getIcon() {
        return icon;
    }

    public String getBorderIcon() {
        return borderIcon;
    }

    public String getInputIcon() {
        return inputIcon;
    }

    public String getOutputIcon() {
        return outputIcon;
    }

    public String getLockedIcon() {
        return lockedIcon;
    }

    public String getUid() {
        return uid;
    }

    public void writeTypeToNBT(NBTTagCompound nbtRoot) {
        nbtRoot.setString("type", getUid());
    }

    public static CapBankType readTypeFromNBT(NBTTagCompound nbtRoot) {
        if (nbtRoot == null || !nbtRoot.hasKey("type")) {
            return ACTIVATED;
        }
        return getTypeFromUID(nbtRoot.getString("type"));
    }
}
