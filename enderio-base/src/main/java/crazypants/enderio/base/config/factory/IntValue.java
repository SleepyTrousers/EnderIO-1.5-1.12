package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntValue extends AbstractValue<Integer> {

  protected IntValue(@Nonnull ValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Integer defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Integer makeValue() {
    return owner.getConfig().getInt(keyname, section, defaultValue, minValue != null ? minValue.intValue() : Integer.MIN_VALUE,
        maxValue != null ? maxValue.intValue() : Integer.MAX_VALUE, getText());
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.INTEGER;
  }

}