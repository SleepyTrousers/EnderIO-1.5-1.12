package crazypants.enderio.base.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

public enum CapacitorKeyType {
  ENERGY_BUFFER,
  ENERGY_INTAKE,
  ENERGY_USE,
  ENERGY_LOSS,
  ENERGY_GEN,
  ENERGY_EFFICIENCY,
  SPEED,
  AREA,
  AMOUNT,;

  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

}