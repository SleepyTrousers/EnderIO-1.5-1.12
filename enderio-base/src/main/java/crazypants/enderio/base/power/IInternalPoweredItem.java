package crazypants.enderio.base.power;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Implementations must store power level in the stacks NBT using NBTValue.ENERGY
 */
public interface IInternalPoweredItem {

  // TODO Java8: All these can be default implementations

  int getMaxEnergyStored(@Nonnull ItemStack stack);

  int getEnergyStored(@Nonnull ItemStack stack);

  void setEnergyStored(@Nonnull ItemStack container, int energy);

  int getMaxInput(@Nonnull ItemStack stack);

  int getMaxOutput(@Nonnull ItemStack stack);

}
