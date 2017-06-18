package crazypants.enderio.conduit;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;

public enum ConnectionMode {

  IN_OUT("gui.conduit.ioMode.inOut"),
  INPUT("gui.conduit.ioMode.input"),
  OUTPUT("gui.conduit.ioMode.output"),
  DISABLED("gui.conduit.ioMode.disabled"),
  NOT_SET("gui.conduit.ioMode.notSet");

  private final @Nonnull String unlocalisedName;

  ConnectionMode(@Nonnull String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public static ConnectionMode getNext(ConnectionMode mode) {
    int ord = mode.ordinal() + 1;
    if (ord >= ConnectionMode.values().length) {
      ord = 0;
    }
    return ConnectionMode.values()[ord];
  }

  public static ConnectionMode getPrevious(ConnectionMode mode) {
    int ord = mode.ordinal() - 1;
    if (ord < 0) {
      ord = ConnectionMode.values().length - 1;
    }
    return ConnectionMode.values()[ord];
  }

  public boolean acceptsInput() {
    return this == IN_OUT || this == INPUT;
  }

  public boolean acceptsOutput() {
    return this == IN_OUT || this == OUTPUT;
  }

  public @Nonnull String getLocalisedName() {
    return EnderIO.lang.localize(unlocalisedName);
  }

  static {
    for (ConnectionMode lang : values()) {
      if (!EnderIO.lang.canLocalize(lang.unlocalisedName)) {
        Log.error("Missing translation for '" + lang.unlocalisedName);
      }
    }
  }

}
