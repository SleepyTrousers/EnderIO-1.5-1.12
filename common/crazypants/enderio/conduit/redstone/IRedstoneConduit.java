package crazypants.enderio.conduit.redstone;

import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.DyeColor;

public interface IRedstoneConduit extends IConduit {

  public static final String KEY_CONDUIT_ICON = "enderio:redstoneConduit";
  public static final String KEY_TRANSMISSION_ICON = "enderio:redstoneConduitTransmission";
  public static final String KEY_CORE_OFF_ICON = "enderio:redstoneConduitCoreOff";
  public static final String KEY_CORE_ON_ICON = "enderio:redstoneConduitCoreOn";

  // External redstone interface

  int isProvidingStrongPower(ForgeDirection toDirection);

  int isProvidingWeakPower(ForgeDirection toDirection);

  Set<Signal> getNetworkInputs();

  Set<Signal> getNetworkInputs(ForgeDirection side);

  Set<Signal> getNetworkOutputs(ForgeDirection side);

  DyeColor getSignalColor(ForgeDirection dir);

  void updateNetwork();

  int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side);

  int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet);

}
