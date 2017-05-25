package crazypants.enderio.machine.modes;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import net.minecraft.util.text.TextFormatting;

public enum IoMode {

  NONE("gui.machine.ioMode.none"),
  PULL("gui.machine.ioMode.pull"),
  PUSH("gui.machine.ioMode.push"),
  PUSH_PULL("gui.machine.ioMode.pullPush"),
  DISABLED("gui.machine.ioMode.disabled");

  private final String unlocalisedName;

  IoMode(String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
  }

  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  public static ConnectionMode getNext(ConnectionMode mode) {
    int ord = mode.ordinal() + 1;
    if(ord >= ConnectionMode.values().length) {
      ord = 0;
    }
    return ConnectionMode.values()[ord];
  }

  public static ConnectionMode getPrevious(ConnectionMode mode) {

    int ord = mode.ordinal() - 1;
    if(ord < 0) {
      ord = ConnectionMode.values().length - 1;
    }
    return ConnectionMode.values()[ord];
  }

  public boolean pulls() {
    return this == PULL || this == PUSH_PULL;
  }

  public boolean pushes() {
    return this == PUSH || this == PUSH_PULL;
  }
  
  public boolean canOutput() {
    return pushes() || this == NONE;
  }
  
  public boolean canRecieveInput() {
    return pulls() || this == NONE;
  }

  public String getLocalisedName() {
    return EnderIO.lang.localize(unlocalisedName);
  }
  
  public String colorLocalisedName() {
    String loc = getLocalisedName();
    switch (this) {
    case DISABLED:
      return TextFormatting.RED + loc;
    case NONE:
      return TextFormatting.GRAY + loc;
    case PULL:
      return TextFormatting.AQUA + loc;
    case PUSH:
      return TextFormatting.GOLD + loc;
    case PUSH_PULL:
      return String.format(EnderIO.lang.localize(this.getUnlocalisedName() + ".colored"), TextFormatting.GOLD, TextFormatting.WHITE, TextFormatting.AQUA);
    default: return loc;
    }
  }

  public IoMode next() {
    int index = ordinal() + 1;
    if(index >= values().length) {
      index = 0;
    }
    return values()[index];
  }
}
