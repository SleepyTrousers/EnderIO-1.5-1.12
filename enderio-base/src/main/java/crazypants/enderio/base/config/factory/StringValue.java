package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringValue extends AbstractValue<String> {

  protected StringValue(@Nonnull ValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable String makeValue() {
    return owner.getConfig().getString(keyname, section, defaultValue, getText());
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.STRING;
  }

}