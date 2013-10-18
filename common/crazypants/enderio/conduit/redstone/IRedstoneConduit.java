package crazypants.enderio.conduit.redstone;

import java.util.Set;

import net.minecraft.item.ItemDye;
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

  enum SignalColor {
    BLACK,
    RED,
    GREEN,
    BROWN,
    BLUE,
    PURPLE,
    CYAN,
    SILVER,
    GRAY,
    PINK,
    LIME,
    YELLOW,
    LIGHT_BLUE,
    MAGENTA,
    ORANGE,
    WHITE;

    public static SignalColor getNext(SignalColor mode) {
      int ord = mode.ordinal() + 1;
      if(ord >= SignalColor.values().length) {
        ord = 0;
      }
      return SignalColor.values()[ord];
    }

    private SignalColor() {
    }

    int getColor() {
      return ItemDye.dyeColors[ordinal()];
    }

  }

  // External redstone interface

  int isProvidingStrongPower(ForgeDirection toDirection);

  int isProvidingWeakPower(ForgeDirection toDirection);

  Set<Signal> getNetworkInputs();

  Set<Signal> getNetworkInputs(ForgeDirection side);

  Set<Signal> getNetworkOutputs(ForgeDirection side);

  SignalColor getSignalColor(ForgeDirection dir);

  void updateNetwork();

}
