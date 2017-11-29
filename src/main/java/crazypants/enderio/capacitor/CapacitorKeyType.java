package crazypants.enderio.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

public enum CapacitorKeyType {
  ENERGY_BUFFER,
  ENERGY_INTAKE,
  ENERGY_USE,
  ENERGY_LOSS,
  ENERGY_GEN,
  SPEED,
  AREA,
  AMOUNT,;

  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

}