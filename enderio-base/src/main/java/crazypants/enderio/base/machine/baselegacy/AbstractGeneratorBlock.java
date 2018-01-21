package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.power.GeneratorBlockItem;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public abstract class AbstractGeneratorBlock<T extends AbstractGeneratorEntity> extends AbstractPoweredMachineBlock<T> {

  protected AbstractGeneratorBlock(@Nonnull IModObject mo, Class<T> teClass, @Nonnull Material mat) {
    super(mo, teClass, mat);
  }

  protected AbstractGeneratorBlock(@Nonnull IModObject mo, Class<T> teClass) {
    super(mo, teClass);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new GeneratorBlockItem(this));
  }

}
