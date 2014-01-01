package crazypants.enderio.conduit.redstone;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.util.DyeColor;

public interface IInsulatedRedstoneConduit extends IRedstoneConduit {

  static final String KEY_INS_CONDUIT_ICON = "enderio:redstoneInsulatedConduit";
  static final String KEY_INS_CORE_OFF_ICON = "enderio:redstoneInsulatedConduitCoreOff";
  static final String KEY_INS_CORE_ON_ICON = "enderio:redstoneInsulatedConduitCoreOn";

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  public void onInputsChanged(ForgeDirection side, int[] inputValues);

  public void onInputChanged(ForgeDirection side, int inputValue);

  public void forceConnectionMode(ForgeDirection dir, ConnectionMode mode);

  void setSignalColor(ForgeDirection dir, DyeColor col);

}
