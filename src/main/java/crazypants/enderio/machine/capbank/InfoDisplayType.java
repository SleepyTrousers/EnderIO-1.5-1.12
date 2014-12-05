package crazypants.enderio.machine.capbank;


public enum InfoDisplayType {

  NONE,
  LEVEL_BAR;

  public InfoDisplayType next() {
    int ord = ordinal();
    ++ord;
    if(ord >= values().length) {
      ord = 0;
    }
    return values()[ord];
  }

}
