package crazypants.enderio.conduit;

public enum ConnectionMode {

  IN_OUT,
  INPUT,
  OUTPUT;

  public static ConnectionMode getNext(ConnectionMode mode) {
    int ord = mode.ordinal() + 1;
    if(ord >= ConnectionMode.values().length) {
      ord = 0;
    }
    return ConnectionMode.values()[ord];
  }
}
