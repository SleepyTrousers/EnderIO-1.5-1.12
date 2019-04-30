package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import info.loenwind.autoconfig.factory.AbstractValue;
import info.loenwind.autoconfig.factory.ByteBufAdapters;
import info.loenwind.autoconfig.factory.IByteBufAdapter;
import info.loenwind.autoconfig.factory.IValueFactory;
import io.netty.buffer.ByteBuf;
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
  protected @Nonnull IByteBufAdapter<Things> getDataType() {
    return THINGS;
  }

  public static final @Nonnull IByteBufAdapter<Things> THINGS = ByteBufAdapters.register(new IByteBufAdapter<Things>() {

    @Override
    public void saveValue(@Nonnull ByteBuf buf, @Nonnull Things value) {
      NNList<String> nameList = value.getNameList();
      if (nameList.size() > 0x7F) {
        throw new RuntimeException("Thing too big");
      }
      buf.writeByte(nameList.size());
      for (String string : nameList) {
        ByteBufAdapters.STRING127.saveValue(buf, NullHelper.first(string, ""));
      }
    }

    @Override
    public Things readValue(@Nonnull ByteBuf buf) {
      Things result = new Things();
      final int len = buf.readByte();
      for (int i = 0; i < len; i++) {
        result.add(ByteBufAdapters.STRING127.readValue(buf));
      }
      return result;
    }

    @Override
    public @Nonnull String getName() {
      return "T";
    }

  });

}
