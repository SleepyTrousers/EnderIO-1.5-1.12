package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

public interface IValue<T> {
  @Nonnull
  T get();

  /**
   * Marks this config value as one that needs to be synced from the server to the client. Returns the object itself for chaining.
   * 
   * Note: Not all config values support this.
   */
  @Nonnull
  default IValue<T> sync() {
    return this;
  }

  @Nonnull
  default IValue<T> setRange(double min, double max) {
    setMin(min);
    setMax(max);
    return this;
  }

  @Nonnull
  default IValue<T> setMin(double min) {
    return this;
  }

  @Nonnull
  default IValue<T> setMax(double max) {
    return this;
  }

}