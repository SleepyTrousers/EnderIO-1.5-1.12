package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.Things;

public class ThingsValue extends AbstractValue<Things> {

  protected ThingsValue(@Nonnull IValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultValue, text);
  }

  @Override
  protected @Nullable Things makeValue() {
    return new Things(owner.getConfig().getStringList(keyname, section, defaultValue.getNameList().toArray(new String[0]), getText()));
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.THINGS;
  }

}