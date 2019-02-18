package crazypants.enderio.base.machine.base.block;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityGeneratorEntity;
import crazypants.enderio.base.power.forge.item.GeneratorBlockItem;
import net.minecraft.block.material.Material;

public abstract class AbstractCapabilityGeneratorBlock<T extends AbstractCapabilityGeneratorEntity> extends AbstractCapabilityPoweredMachineBlock<T> {

  protected AbstractCapabilityGeneratorBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  protected AbstractCapabilityGeneratorBlock(@Nonnull IModObject mo) {
    super(mo);
  }

  @Override
  public GeneratorBlockItem createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new GeneratorBlockItem(this));
  }

}
