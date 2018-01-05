package crazypants.enderio.conduit.redstone;

import java.util.Collection;
import java.util.Set;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public interface IRedstoneConduit extends IConduit {

  public static final String KEY_CONDUIT_ICON = "enderio:blocks/redstoneConduit";
  public static final String KEY_TRANSMISSION_ICON = "enderio:blocks/redstoneConduitTransmission";
  
  public static final String KEY_INS_CONDUIT_ICON = "enderio:blocks/redstoneInsulatedConduit";
  public static final String KEY_INS_CORE_OFF_ICON = "enderio:blocks/redstoneInsulatedConduitCoreOff";
  public static final String KEY_INS_CORE_ON_ICON = "enderio:blocks/redstoneInsulatedConduitCoreOn";

  // External redstone interface

  int isProvidingStrongPower(EnumFacing toDirection);

  int isProvidingWeakPower(EnumFacing toDirection);  

  Set<Signal> getNetworkInputs(EnumFacing side);

  Collection<Signal> getNetworkOutputs(EnumFacing side);

  DyeColor getSignalColor(EnumFacing dir);

  void updateNetwork();

  
  // Old insulated interface


  public static final String COLOR_CONTROLLER_ID = "ColorController";

  void onInputsChanged(EnumFacing side, int[] inputValues);

  void onInputChanged(EnumFacing side, int inputValue);

  void forceConnectionMode(EnumFacing dir, ConnectionMode mode);

  void setSignalColor(EnumFacing dir, DyeColor col);

  boolean isSpecialConnection(EnumFacing dir);

  boolean isOutputStrong(EnumFacing dir);

  void setOutputStrength(EnumFacing dir, boolean isStrong);
}
