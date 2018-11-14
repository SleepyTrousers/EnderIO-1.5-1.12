package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import info.loenwind.autoconfig.factory.IFactory;

public interface IFactoryEIO extends IFactory {

  @Override
  @Nonnull
  IValueFactoryEIO section(@Nonnull String section);

}
