package crazypants.enderio.conduit.redstone;

import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;

public interface IRedstoneConduit extends IConduit {

  public static final String KEY_CONDUIT_ICON = "enderio:redstoneConduit";
  public static final String KEY_TRANSMISSION_ICON = "enderio:redstoneConduitTransmission";
  public static final String KEY_CORE_OFF_ICON = "enderio:redstoneConduitCoreOff";
  public static final String KEY_CORE_ON_ICON = "enderio:redstoneConduitCoreOn";

  public static final String[] DYE_ORE_NAMES = {
      "dyeBlack",
      "dyeRed",
      "dyeGreen",
      "dyeBrown",
      "dyeBlue",
      "dyePurple",
      "dyeCyan",
      "dyeLightGray",
      "dyeGray",
      "dyePink",
      "dyeLime",
      "dyeYellow",
      "dyeLightBlue",
      "dyeMagenta",
      "dyeOrange",
      "dyeWhite"
  };

  // External redstone interface

  int isProvidingStrongPower(ForgeDirection toDirection);

  int isProvidingWeakPower(ForgeDirection toDirection);

  Set<Signal> getNetworkInputs();

  Set<Signal> getNetworkInputs(ForgeDirection side);

  Set<Signal> getNetworkOutputs(ForgeDirection side);

  SignalColor getSignalColor(ForgeDirection dir);

  void updateNetwork();

  int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side);

  int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet);

}
