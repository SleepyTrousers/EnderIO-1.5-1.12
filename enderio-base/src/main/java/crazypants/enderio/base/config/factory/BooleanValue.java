package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

public class BooleanValue extends AbstractValue<Boolean> {

  protected BooleanValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Boolean makeValue() {
    Property prop = owner.getConfig().get(section, keyname, defaultValue);
    prop.setLanguageKey(keyname);
    prop.setComment(getText() + " [default: " + defaultValue + "]");
    prop.setRequiresMcRestart(isStartup);
    return prop.getBoolean(defaultValue);
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.BOOLEAN;
  }

}