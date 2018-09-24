package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.core.util.helpers.MathHelper;
import net.minecraftforge.common.config.Property;

public class IntValue extends AbstractValue<Integer> {

  protected IntValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Integer defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Integer makeValue() {
    int min = minValue != null ? minValue.intValue() : Integer.MIN_VALUE, max = maxValue != null ? maxValue.intValue() : Integer.MAX_VALUE;
    Property prop = owner.getConfig().get(section, keyname, defaultValue);
    prop.setLanguageKey(keyname);
    prop.setComment(getText() + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
    prop.setMinValue(min);
    prop.setMaxValue(max);
    prop.setRequiresMcRestart(isStartup);
    return MathHelper.clamp(prop.getInt(defaultValue), min, max);
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.INTEGER;
  }

}