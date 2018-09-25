package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

public class LimitedStringValue extends AbstractValue<String> {

  private final @Nonnull String[] limit;

  protected LimitedStringValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue,
      @Nonnull String[] limit, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
    this.limit = limit;
  }

  @Override
  protected @Nullable String makeValue() {
    Property prop = owner.getConfig().get(section, keyname, defaultValue);
    prop.setValidValues(limit);
    prop.setLanguageKey(keyname);
    prop.setComment(getText() + " [default: " + defaultValue + "]");
    prop.setRequiresMcRestart(isStartup);
    return prop.getString();
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.STRING127;
  }

}