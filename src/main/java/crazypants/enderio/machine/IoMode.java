package crazypants.enderio.machine;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.EnumChatFormatting;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.util.Lang;

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

  public String getLocalisedName() {
    return Lang.localize(unlocalisedName);
  }
  
  public String colorLocalisedName() {
    String loc = getLocalisedName();
    switch (this) {
    case DISABLED:
      return EnumChatFormatting.RED + loc;
    case NONE:
      return EnumChatFormatting.GRAY + loc;
    case PULL:
      return EnumChatFormatting.AQUA + loc;
    case PUSH:
      return EnumChatFormatting.GOLD + loc;
    case PUSH_PULL:
      return String.format(Lang.localize(this.getUnlocalisedName() + ".colored"), EnumChatFormatting.GOLD, EnumChatFormatting.WHITE, EnumChatFormatting.AQUA);
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
