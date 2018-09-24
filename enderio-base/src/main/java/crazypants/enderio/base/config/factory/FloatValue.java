package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.core.util.helpers.MathHelper;
import net.minecraftforge.common.config.Property;

public class FloatValue extends AbstractValue<Float> {

  protected FloatValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Float defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Float makeValue() {
    float min = minValue == null ? Float.NEGATIVE_INFINITY : minValue.floatValue(), max = maxValue == null ? Float.MAX_VALUE : maxValue.floatValue();
    @SuppressWarnings("cast")
    Property prop = owner.getConfig().get(section, keyname, (double) defaultValue);
    prop.setLanguageKey(keyname);
    prop.setComment(getText() + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
    prop.setMinValue(min);
    prop.setMaxValue(max);
    prop.setRequiresMcRestart(isStartup);
    return (float) MathHelper.clamp(prop.getDouble(defaultValue), min, max);
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.FLOAT;
  }

}