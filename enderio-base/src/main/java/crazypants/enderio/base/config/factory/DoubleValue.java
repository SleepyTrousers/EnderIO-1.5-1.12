package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

public class DoubleValue extends AbstractValue<Double> {

  protected DoubleValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Double defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Double makeValue() {
    String comment = getText() + " [range: " + (minValue != null ? minValue : Double.NEGATIVE_INFINITY) + " ~ "
        + (maxValue != null ? maxValue : Double.MAX_VALUE) + ", default: " + defaultValue + "]";
    final Property property = owner.getConfig().get(section, keyname, defaultValue, comment);
    if (minValue != null) {
      property.setMinValue(minValue);
    }
    if (maxValue != null) {
      property.setMaxValue(maxValue);
    }
    return property.getDouble(defaultValue);
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.DOUBLE;
  }

}