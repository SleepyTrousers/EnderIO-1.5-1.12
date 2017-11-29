package crazypants.enderio.machines.machine.spawner;

import crazypants.enderio.base.EnderIO;

public enum SpawnerNotification {

  AREA_FULL("areaFull"),
  NO_LOCATION_FOUND("noLocationFound"),
  NO_LOCATION_AT_ALL("noLocationAtAll"),
  BAD_SOUL("badSoul"),
  NO_PLAYER("noPlayer");

  private final String langStr;

  private SpawnerNotification(String langStr) {
    this.langStr = langStr;
  }

  public String getDisplayString() {
    return EnderIO.lang.localize("spawner.note." + langStr);
  }

}
