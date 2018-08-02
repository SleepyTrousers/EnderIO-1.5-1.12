package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.power.PoweredBlockItem;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public abstract class AbstractPowerConsumerBlock<T extends AbstractPowerConsumerEntity> extends AbstractPoweredMachineBlock<T> {

  protected AbstractPowerConsumerBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  protected AbstractPowerConsumerBlock(@Nonnull IModObject mo) {
    super(mo);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new PoweredBlockItem(this));
  }

}
