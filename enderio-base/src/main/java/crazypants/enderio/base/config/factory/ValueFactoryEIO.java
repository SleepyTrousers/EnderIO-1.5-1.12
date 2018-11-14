package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import info.loenwind.autoconfig.factory.ValueFactory;

public class ValueFactoryEIO extends ValueFactory implements IFactoryEIO {

  public ValueFactoryEIO(@Nonnull String modid) {
    super(modid);
  }

  @Override
  public @Nonnull IValueFactoryEIO section(@SuppressWarnings("hiding") @Nonnull String section) {
    return new SlaveFactoryEIO(this, section);
  }

}
