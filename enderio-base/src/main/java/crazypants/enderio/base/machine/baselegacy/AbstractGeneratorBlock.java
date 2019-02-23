package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.power.forge.item.GeneratorBlockItem;
import net.minecraft.block.material.Material;

public abstract class AbstractGeneratorBlock<T extends AbstractGeneratorEntity> extends AbstractPoweredMachineBlock<T> {

  protected AbstractGeneratorBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  protected AbstractGeneratorBlock(@Nonnull IModObject mo) {
    super(mo);
  }

  @Override
  public GeneratorBlockItem createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new GeneratorBlockItem(this));
  }

}
