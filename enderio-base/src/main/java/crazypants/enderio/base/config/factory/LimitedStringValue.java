package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LimitedStringValue extends AbstractValue<String> {

  private final @Nonnull String[] limit;

  protected LimitedStringValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue,
      @Nonnull String[] limit, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
    this.limit = limit;
  }

  @Override
  protected @Nullable String makeValue() {
    return owner.getConfig().getString(keyname, section, defaultValue, getText(), limit);
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.STRING127;
  }

}