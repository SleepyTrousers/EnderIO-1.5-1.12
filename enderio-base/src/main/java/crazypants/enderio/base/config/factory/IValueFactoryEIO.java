package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public interface IValueFactoryEIO extends IValueFactory {

  @Nonnull
  IValue<Things> make(@Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text);

  @Override
  @Nonnull
  IValueFactoryEIO section(@Nonnull String section);

}
