package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

public class StringValue extends AbstractValue<String> {

  protected StringValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable String makeValue() {
    Property prop = owner.getConfig().get(section, keyname, defaultValue);
    prop.setLanguageKey(keyname);
    prop.setValidationPattern(null);
    prop.setComment(getText() + " [default: " + defaultValue + "]");
    prop.setRequiresMcRestart(isStartup);
    return prop.getString();
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.STRING;
  }

}