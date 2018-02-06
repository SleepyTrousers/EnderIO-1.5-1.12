package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import net.minecraft.block.material.Material;

public abstract class AbstractPoweredTaskBlock<T extends AbstractPoweredTaskEntity> extends AbstractPowerConsumerBlock<T> {

  protected AbstractPoweredTaskBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  protected AbstractPoweredTaskBlock(@Nonnull IModObject mo) {
    super(mo);
  }

}
