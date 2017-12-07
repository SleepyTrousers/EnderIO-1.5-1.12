package crazypants.enderio.machines.machine.spawner;

import crazypants.enderio.machines.EnderIOMachines;

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
    return EnderIOMachines.lang.localize("block_powered_spawner.note." + langStr);
  }

}
