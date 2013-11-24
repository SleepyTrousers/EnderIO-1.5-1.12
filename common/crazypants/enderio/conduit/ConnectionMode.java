package crazypants.enderio.conduit;

public enum ConnectionMode {

  IN_OUT("In / Out"),
  INPUT("Input"),
  OUTPUT("Output"),
  DISABLED("Disabled"),
  NOT_SET("Default");

  private final String unlocalisedName;

  ConnectionMode(String unlocalisedName) {
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
}
