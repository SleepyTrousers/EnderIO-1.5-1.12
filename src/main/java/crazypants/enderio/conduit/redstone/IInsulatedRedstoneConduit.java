package crazypants.enderio.conduit.redstone;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.ConnectionMode;

public interface IInsulatedRedstoneConduit extends IRedstoneConduit {

    static final String KEY_INS_CONDUIT_ICON = "enderio:redstoneInsulatedConduit";
    static final String KEY_INS_CORE_OFF_ICON = "enderio:redstoneInsulatedConduitCoreOff";
    static final String KEY_INS_CORE_ON_ICON = "enderio:redstoneInsulatedConduitCoreOn";

    public static final String COLOR_CONTROLLER_ID = "ColorController";

    void onInputsChanged(ForgeDirection side, int[] inputValues);

    void onInputChanged(ForgeDirection side, int inputValue);

    void forceConnectionMode(ForgeDirection dir, ConnectionMode mode);

    void setSignalColor(ForgeDirection dir, DyeColor col);

    boolean isSpecialConnection(ForgeDirection dir);

    boolean isOutputStrong(ForgeDirection dir);

    void setOutputStrength(ForgeDirection dir, boolean isStrong);
}
