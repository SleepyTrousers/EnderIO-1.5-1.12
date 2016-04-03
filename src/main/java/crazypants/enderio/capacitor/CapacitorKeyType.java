package crazypants.enderio.capacitor;

import java.util.Locale;

public enum CapacitorKeyType {
  ENERGY_BUFFER,
  ENERGY_INTAKE,
  ENERGY_USE,
  SPEED,
  AREA,
  AMOUNT, ;

  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

}