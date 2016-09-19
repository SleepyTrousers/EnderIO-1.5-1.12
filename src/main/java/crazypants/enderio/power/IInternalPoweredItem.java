package crazypants.enderio.power;

import net.minecraft.item.ItemStack;

/**
 * Implementations must store power level in the stacks NBT using NBTValue.ENERGY
 */
public interface IInternalPoweredItem {

  int getMaxEnergyStored(ItemStack stack);
  
  int getEnergyStored(ItemStack stack);
  
  void setEnergyStored(ItemStack container, int energy);
  
  int getMaxInput(ItemStack stack);
  
  int getMaxOutput(ItemStack stack);
  
  
     
}
