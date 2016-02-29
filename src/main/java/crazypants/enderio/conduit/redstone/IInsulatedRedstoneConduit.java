package crazypants.enderio.conduit.redstone;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.ConnectionMode;
import net.minecraft.util.EnumFacing;

public interface IInsulatedRedstoneConduit extends IRedstoneConduit {

  static final String KEY_INS_CONDUIT_ICON = "enderio:blocks/redstoneInsulatedConduit";
  static final String KEY_INS_CORE_OFF_ICON = "enderio:blocks/redstoneInsulatedConduitCoreOff";
  static final String KEY_INS_CORE_ON_ICON = "enderio:blocks/redstoneInsulatedConduitCoreOn";

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  void onInputsChanged(EnumFacing side, int[] inputValues);

  void onInputChanged(EnumFacing side, int inputValue);

  void forceConnectionMode(EnumFacing dir, ConnectionMode mode);

  void setSignalColor(EnumFacing dir, DyeColor col);

  boolean isSpecialConnection(EnumFacing dir);

  boolean isOutputStrong(EnumFacing dir);

  void setOutputStrength(EnumFacing dir, boolean isStrong);

}
