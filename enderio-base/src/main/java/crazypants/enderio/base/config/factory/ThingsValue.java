package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.Things;

import net.minecraftforge.common.config.Property;

public class ThingsValue extends AbstractValue<Things> {

  protected ThingsValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Things makeValue() {
    Property prop = owner.getConfig().get(section, keyname, defaultValue.getNameList().toArray(new String[0]));
    prop.setLanguageKey(keyname);
    prop.setValidValues(null);
    prop.setComment(getText() + " [default: " + prop.getDefault() + "]");
    prop.setRequiresMcRestart(isStartup);
    return new Things(prop.getStringList());
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.THINGS;
  }

}