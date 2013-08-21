package crazypants.enderio.conduit.redstone;

import java.util.Set;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;

public interface IRedstoneConduit extends IConduit {

  public static final String KEY_CONDUIT_ICON = "enderio:redstoneConduit";
  public static final String KEY_TRANSMISSION_ICON = "enderio:redstoneConduitTransmission";
  public static final String KEY_CORE_OFF_ICON = "enderio:redstoneConduitCoreOff";
  public static final String KEY_CORE_ON_ICON = "enderio:redstoneConduitCoreOn";

  enum SignalColor {
    RED
  }

  // External redstone interface

  int isProvidingStrongPower(ForgeDirection toDirection);

  int isProvidingWeakPower(ForgeDirection toDirection);

  // signals

  // --- Server Side Only
  Set<Signal> getNetworkInputs();

  Set<Signal> getNetworkOutputs(ForgeDirection side);

  // --- Set on server side, used on client side for rendering

  boolean isReplaceableByControl(Class<? extends IRedstoneConduit> replacenebtType);

}
