package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BooleanValue extends AbstractValue<Boolean> {

  protected BooleanValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Boolean makeValue() {
    return owner.getConfig().getBoolean(keyname, section, defaultValue, getText());
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.BOOLEAN;
  }

}