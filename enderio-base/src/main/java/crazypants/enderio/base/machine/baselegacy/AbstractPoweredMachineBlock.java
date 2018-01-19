package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import net.minecraft.block.material.Material;

public abstract class AbstractPoweredMachineBlock<T extends AbstractPoweredMachineEntity> extends AbstractInventoryMachineBlock<T> {

  AbstractPoweredMachineBlock(@Nonnull IModObject mo, Class<T> teClass, @Nonnull Material mat) {
    super(mo, teClass, mat);
  }

  AbstractPoweredMachineBlock(@Nonnull IModObject mo, Class<T> teClass) {
    super(mo, teClass);
  }

}
