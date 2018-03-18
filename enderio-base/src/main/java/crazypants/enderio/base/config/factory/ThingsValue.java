package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import io.netty.buffer.ByteBuf;

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
    return ByteBufHelper.NONE;
  }

  @Override
  public void save(ByteBuf buf) {
    Log.warn("StringList config options cannot be synced to the client. This is a coding error.");
  }
}