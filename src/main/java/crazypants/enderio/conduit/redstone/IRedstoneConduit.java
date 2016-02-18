package crazypants.enderio.conduit.redstone;

import java.util.Set;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.IConduit;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IRedstoneConduit extends IConduit {

  public static final String KEY_CONDUIT_ICON = "enderio:redstoneConduit";
  public static final String KEY_TRANSMISSION_ICON = "enderio:redstoneConduitTransmission";
  public static final String KEY_CORE_OFF_ICON = "enderio:redstoneConduitCoreOff";
  public static final String KEY_CORE_ON_ICON = "enderio:redstoneConduitCoreOn";

  // External redstone interface

  int isProvidingStrongPower(EnumFacing toDirection);

  int isProvidingWeakPower(EnumFacing toDirection);

  Set<Signal> getNetworkInputs();

  Set<Signal> getNetworkInputs(EnumFacing side);

  Set<Signal> getNetworkOutputs(EnumFacing side);

  DyeColor getSignalColor(EnumFacing dir);

  void updateNetwork();

  // MFR RedNet

  int[] getOutputValues(World world, int x, int y, int z, EnumFacing side);

  int getOutputValue(World world, int x, int y, int z, EnumFacing side, int subnet);

  void onInputsChanged(World world, int x, int y, int z, EnumFacing side, int[] inputValues);
}
