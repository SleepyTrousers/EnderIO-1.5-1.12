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

  /**
   * Marks this config value as one that needs to be in sync between the server and the client but cannot be changed at runtime. Returns the object itself for
   * chaining.
   * 
   * Note: Not all config values support this.
   */
  @Nonnull
  default IValue<T> startup() {
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