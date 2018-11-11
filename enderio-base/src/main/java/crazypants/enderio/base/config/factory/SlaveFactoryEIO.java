package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import info.loenwind.autoconfig.factory.IFactory;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.SlaveFactory;

public class SlaveFactoryEIO extends SlaveFactory implements IValueFactoryEIO {

  public SlaveFactoryEIO(@Nonnull IFactory valueFactory, @Nonnull String section) {
    super(valueFactory, section);
  }

  @Override
  public @Nonnull IValueFactoryEIO section(@SuppressWarnings("hiding") @Nonnull String section) {
    if (section.startsWith(".")) {
      return new SlaveFactoryEIO(this, this.getSection() + section);
    } else {
      return new SlaveFactoryEIO(this, section);
    }
  }

  @Override
  public @Nonnull IValue<Things> make(@Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text) {
    return new ThingsValue(this, getSection(), keyname, defaultValue, text).preload();
  }

}
