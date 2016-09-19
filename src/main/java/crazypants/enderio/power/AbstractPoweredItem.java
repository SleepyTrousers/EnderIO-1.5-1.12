package crazypants.enderio.power;

import crazypants.util.NbtValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractPoweredItem extends Item implements IInternalPoweredItem {

  protected int maxEneryStored;
  protected int maxInput;
  protected int maxOutput;

  public AbstractPoweredItem(int maxEneryStored, int maxInput, int maxOutput) {
    this.maxEneryStored = maxEneryStored;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return maxEneryStored;
  }
  
  @Override
  public int getEnergyStored(ItemStack container) {
    return NbtValue.ENERGY.getInt(container);
  }
    
  @Override
  public void setEnergyStored(ItemStack container, int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp_int(energy, 0, maxEneryStored));
  }

  @Override
  public int getMaxInput(ItemStack container) {
    return maxInput;
  }

  @Override
  public int getMaxOutput(ItemStack container) {
    return maxOutput;
  }
  
  public void setFull(ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
  }

}
