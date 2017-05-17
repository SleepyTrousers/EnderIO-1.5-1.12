package crazypants.enderio.capacitor;

import javax.annotation.Nonnull;

public interface ICapacitorData {

  int getBaseLevel();

  float getUnscaledValue(@Nonnull ICapacitorKey key);

  @Nonnull
  String getUnlocalizedName();

  @Nonnull
  String getLocalizedName();

}