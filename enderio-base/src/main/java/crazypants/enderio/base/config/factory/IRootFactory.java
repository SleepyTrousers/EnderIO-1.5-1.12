package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import net.minecraftforge.common.config.Configuration;

public interface IRootFactory {

  @Nonnull
  IValueFactory section(@Nonnull String section);

  @Nonnull
  String getModid();

  @Nonnull
  String getSection();

  boolean isInInit();

  void addPreloadValue(@Nonnull AbstractValue<?> value);

  int getGeneration();

  Configuration getConfig();

}
