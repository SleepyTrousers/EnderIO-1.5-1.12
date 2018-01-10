package crazypants.enderio.powertools.machine.capbank;

public enum InfoDisplayType {

  NONE,
  LEVEL_BAR,
  IO;

  public InfoDisplayType next() {
    int ord = ordinal();
    ++ord;
    if (ord >= values().length) {
      ord = 0;
    }
    return values()[ord];
  }

}
