package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloatValue extends AbstractValue<Float> {

  protected FloatValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Float defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Float makeValue() {
    return owner.getConfig().getFloat(keyname, section, defaultValue, minValue == null ? Float.NEGATIVE_INFINITY : minValue.floatValue(),
        maxValue == null ? Float.MAX_VALUE : maxValue.floatValue(), getText());
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.FLOAT;
  }

}