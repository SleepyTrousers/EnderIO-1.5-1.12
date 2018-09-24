package crazypants.enderio.base.config.factory;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

public class EnumValue<E extends Enum<E>> implements IValue<E> {

  private final @Nonnull Class<E> enumClazz;
  private final @Nonnull E defaultEnumValue;
  private final @Nonnull IValue<String> storage;

  protected EnumValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull E defaultValue, @Nonnull String text) {
    this.defaultEnumValue = defaultValue;
    this.enumClazz = NullHelper.notnull(defaultValue.getDeclaringClass(), "enum without a class");
    storage = owner.make(keyname, defaultValue.name(),
        NullHelper.notnullJ(Arrays.stream(enumClazz.getEnumConstants()).map(Enum::name).toArray(String[]::new), "Stream.toArray"), text);
  }

  @Override
  public @Nonnull E get() {
    try {
      return Enum.valueOf(enumClazz, storage.get());
    } catch (IllegalArgumentException e) {
      return defaultEnumValue;
    }
  }

  @Override
  @Nonnull
  public IValue<E> sync() {
    storage.sync();
    return this;
  }

  @Override
  @Nonnull
  public IValue<E> startup() {
    storage.startup();
    return this;
  }

}